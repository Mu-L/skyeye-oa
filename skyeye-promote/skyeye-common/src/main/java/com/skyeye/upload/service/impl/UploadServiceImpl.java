/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.upload.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.*;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.framework.file.core.client.FileClient;
import com.skyeye.framework.file.core.client.s3.FilePresignedUrlRespDTO;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.upload.entity.Upload;
import com.skyeye.upload.entity.UploadChunks;
import com.skyeye.upload.service.FileConfigService;
import com.skyeye.upload.service.FileService;
import com.skyeye.upload.service.UploadService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * @ClassName: UploadServiceImpl
 * @Description: 文件上传、下载服务服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/28 21:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class UploadServiceImpl implements UploadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadServiceImpl.class);

    @Autowired
    public JedisClientService jedisClient;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private FileConfigService fileConfigService;

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Autowired
    private FileService fileService;

    /**
     * 断点续传上传文件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void uploadFileResume(InputObject inputObject, OutputObject outputObject) {
        Upload upload = inputObject.getParams(Upload.class);
        // 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(PutObject.getRequest().getSession().getServletContext());
        // 检查form中是否有enctype="multipart/form-data"
        if (!multipartResolver.isMultipart(PutObject.getRequest())) {
            return;
        }
        String userId = inputObject.getLogParams().get("id").toString();
        // 将request变成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) PutObject.getRequest();
        // 获取multiRequest 中所有的文件名
        Iterator iter = multiRequest.getFileNames();
        while (iter.hasNext()) {
            String fileAddress = uploadFile(multiRequest, iter, upload.getType(), userId);
            upload.setFileType(fileAddress.substring(fileAddress.lastIndexOf(".") + 1).toLowerCase());
            upload.setFileSizeType("bytes");
            upload.setFileAddress(fileAddress);
            upload.setFileThumbnail("-1");

            String cacheKey = getCacheKey(upload.getMd5());
            List<Upload> beans = redisCache.getList(cacheKey, key -> new ArrayList<>(), RedisConstants.ONE_DAY_SECONDS, Upload.class);
            beans.add(upload);
            jedisClient.set(cacheKey, JSONUtil.toJsonStr(beans));
        }
    }

    private String uploadFile(MultipartHttpServletRequest multiRequest, Iterator iter, Integer type, String userId) {
        String basePath = tPath + FileConstants.FileUploadPath.getSavePath(type, userId);
        MultipartFile file = multiRequest.getFile(iter.next().toString());
        if (file == null) {
            throw new CustomException("file is not null.");
        }
        String fileName = file.getOriginalFilename();
        // 得到文件扩展名
        String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        FileUtil.createDirs(basePath);
        // 自定义的文件名称
        String newFileName = String.format(Locale.ROOT, "%s.%s", System.currentTimeMillis(), fileExtName);
        String path = basePath + "/" + newFileName;
        // 上传
        try {
            file.transferTo(new File(path));
        } catch (IOException e) {
            throw new CustomException(e);
        }
        return FileConstants.FileUploadPath.getVisitPath(type, userId) + newFileName;
    }

    /**
     * 上传文件合并块
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void uploadFileChunks(InputObject inputObject, OutputObject outputObject) {
        UploadChunks uploadChunks = inputObject.getParams(UploadChunks.class);
        String cacheKey = getCacheKey(uploadChunks.getMd5());
        List<Upload> beans = redisCache.getList(cacheKey, key -> new ArrayList<>(), RedisConstants.ONE_DAY_SECONDS, Upload.class);
        List<File> fileList = new ArrayList<>();
        for (Upload bean : beans) {
            File f = new File(tPath.replace("images", "") + bean.getFileAddress());
            fileList.add(f);
        }
        String userId = inputObject.getLogParams().get("id").toString();
        String fileName = uploadChunks.getName();
        String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        String newFileName = String.format(Locale.ROOT, "%s.%s", System.currentTimeMillis(), fileExtName);
        String path = tPath + FileConstants.FileUploadPath.getSavePath(uploadChunks.getType(), userId)
            + CommonCharConstants.SLASH_MARK + newFileName;
        FileChannel outChnnel = null;
        try {
            File outputFile = new File(path);
            // 创建文件
            outputFile.createNewFile();
            // 输出流
            outChnnel = new FileOutputStream(outputFile).getChannel();
            FileChannel inChannel;
            for (File file : fileList) {
                inChannel = new FileInputStream(file).getChannel();
                inChannel.transferTo(0, inChannel.size(), outChnnel);
                inChannel.close();
                // 删除分片
                file.delete();
            }
        } catch (Exception e) {
            throw new CustomException(e);
        } finally {
            FileUtil.close(outChnnel);
        }
        jedisClient.del(cacheKey);
        uploadChunks.setFileType(fileExtName);
        uploadChunks.setFileSizeType("bytes");
        newFileName = FileConstants.FileUploadPath.getVisitPath(uploadChunks.getType(), userId) + newFileName;
        uploadChunks.setFileAddress(newFileName);
        outputObject.setBean(uploadChunks);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private String getCacheKey(String md5) {
        return String.format(Locale.ROOT, "upload:file:chunks:%s", md5);
    }

    /**
     * 文件分块上传检测是否上传
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void checkUploadFileChunks(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String md5 = map.get("md5").toString();
        String chunk = map.get("chunk").toString();
        String cacheKey = getCacheKey(md5);
        List<Upload> beans = redisCache.getList(cacheKey, key -> new ArrayList<>(), RedisConstants.ONE_DAY_SECONDS, Upload.class);
        Upload bean = null;
        int index = -1;
        for (int i = 0; i < beans.size(); i++) {
            Upload upload = beans.get(i);
            if (chunk.equals(upload.getChunk())) {
                bean = upload;
                index = i;
                break;
            }
        }
        if (bean != null) {
            String fileAddress = tPath.replace("images", "") + bean.getFileAddress();
            File checkFile = new File(fileAddress);
            String chunkSize = map.get("chunkSize").toString();
            if (checkFile.exists() && checkFile.length() == Integer.parseInt(chunkSize)) {
            } else {
                beans.remove(index);
                jedisClient.set(cacheKey, JSONUtil.toJsonStr(beans));
                outputObject.setreturnMessage("文件上传失败");
            }
        } else {
            outputObject.setreturnMessage("文件上传失败");
        }
    }

    /**
     * 上传文件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void uploadFile(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(PutObject.getRequest().getSession().getServletContext());
        // 检查form中是否有enctype="multipart/form-data"
        if (!multipartResolver.isMultipart(PutObject.getRequest())) {
            return;
        }
        // 将request变成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) PutObject.getRequest();
        // 获取multiRequest 中所有的文件名
        Iterator iter = multiRequest.getFileNames();
        int type = Integer.parseInt(map.get("type").toString());
        String basePath = tPath + FileConstants.FileUploadPath.getSavePath(type);
        Map<String, Object> bean = new HashMap<>();
        StringBuffer trueFileName = new StringBuffer();
        String fileName = "";
// TODO 上传到文件存储器待测试
//        // 上传到文件存储器
//        FileClient client = fileConfigService.getMasterFileClient();
//        if (client == null) {
//            throw new CustomException("客户端(master) 不能为空");
//        }

        byte[] content = null;
        while (iter.hasNext()) {
            MultipartFile file = multiRequest.getFile(iter.next().toString());
            if (file == null) {
                break;
            }
            // 文件名称
            fileName = file.getOriginalFilename();
            // 得到文件扩展名
            String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
            // 自定义的文件名称
            String newFileName = String.format(Locale.ROOT, "%s.%s", System.currentTimeMillis(), fileExtName);
            bean.put("fileExtName", fileExtName);
            bean.put("size", file.getSize());
            bean.put("fileSizeType", "bytes");
            String path = basePath + "/" + newFileName;
//            try {
//                content = IoUtil.readBytes(file.getInputStream());
//                String url = client.upload(content, path, fileExtName);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
            FileUtil.createDirs(basePath);
            LOGGER.info("upload file type is: {}, path is: {}", type, path);
            // 上传
            try {
                file.transferTo(new File(path));
            } catch (IOException ex) {
                throw new CustomException(ex);
            }
            newFileName = FileConstants.FileUploadPath.getVisitPath(type) + newFileName;
            if (ToolUtil.isBlank(trueFileName.toString())) {
                trueFileName.append(newFileName);
            } else {
                trueFileName.append(",").append(newFileName);
            }
            saveFile(file, trueFileName.toString());
            break;
        }
        bean.put("picUrl", trueFileName.toString());
        bean.put("type", type);
        bean.put("fileName", fileName);

        outputObject.setBean(bean);
    }

    private void saveFile(MultipartFile multipartFile, String path) {
        com.skyeye.upload.entity.File file = new com.skyeye.upload.entity.File();
//        file.setConfigId(client.getId());
        file.setName(multipartFile.getOriginalFilename());
        file.setPath(path);
        file.setUrl(path);
        file.setType(FileUtil.getMineType(multipartFile.getOriginalFilename()));
        file.setSize(multipartFile.getSize());

        String userId = StrUtil.EMPTY;
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if (StrUtil.isNotEmpty(userToken)) {
            String userTokenUserId = GetUserToken.getUserTokenUserId(InputObject.getRequest());
            Boolean aBoolean = SysUserAuthConstants.exitUserLoginRedisCache(userTokenUserId);
            if (aBoolean) {
                userId = InputObject.getLogParamsStatic().get("id").toString();
            }
        }
        fileService.createEntity(file, userId);
    }

    /**
     * 上传文件Base64
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void uploadFileBase64(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        int type = Integer.parseInt(map.get("type").toString());
        String imgStr = map.get("images").toString();
        imgStr = imgStr.replaceAll("\\+", "%2B").replaceAll(" ", "+");
        String[] d = imgStr.split("base64,");
        // 上传数据是否合法
        if (d != null && d.length == 2) {
            String dataPrix = d[0];
            String data = d[1];
            if (FileUtil.checkBase64IsImage(dataPrix)) {
                try {
                    byte[] bytes = Base64.decodeBase64(data.getBytes());
                    // 决定存储路径
                    String basePath = tPath + FileConstants.FileUploadPath.getSavePath(type);
                    FileUtil.createDirs(basePath);
                    // 自定义的文件名称
                    String trueFileName = System.currentTimeMillis() + "." + FileUtil.getBase64FileTypeByPrix(dataPrix);
                    // 写入文件
                    FileUtil.writeByteToPointPath(bytes, basePath + "/" + trueFileName);
                    Map<String, Object> bean = new HashMap<>();
                    bean.put("picUrl", FileConstants.FileUploadPath.getVisitPath(type) + trueFileName);
                    bean.put("type", type);
                    outputObject.setBean(bean);
                } catch (Exception ee) {
                    LOGGER.warn("uploadFileBase64 failed. {}", ee);
                    outputObject.setreturnMessage("上传失败，数据不合法");
                }
            } else {
                outputObject.setreturnMessage("文件类型不正确，只允许上传jpg,png,jpeg格式的图片");
            }
        } else {
            outputObject.setreturnMessage("上传失败，数据不合法");
        }
    }

    @Override
    public void getFileContent(HttpServletRequest request, HttpServletResponse response, String configId) {
        // 获取请求的路径
        String path = StrUtil.subAfter(request.getRequestURI(), "/get/", false);
        if (StrUtil.isEmpty(path)) {
            throw new IllegalArgumentException("结尾的 path 路径必须传递");
        }
        // 解码，解决中文路径的问题 https://gitee.com/zhijiantianya/ruoyi-vue-pro/pulls/807/
        path = URLUtil.decode(path);

        // 读取内容
        FileClient client = fileConfigService.getFileClient(configId);
        Assert.notNull(client, "客户端({}) 不能为空", configId);
        try {
            byte[] content = client.getContent(path);
            if (content == null) {
                LOGGER.warn("[getFileContent][configId({}) path({}) 文件不存在]", configId, path);
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return;
            }
            FileUtil.writeAttachment(response, path, content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFileByPath(InputObject inputObject, OutputObject outputObject) {
        String path = inputObject.getParams().get("path").toString();
        com.skyeye.upload.entity.File file = fileService.queryByPath(path);
        if (file == null) {
            throw new CustomException("文件不存在");
        }
        // 从文件存储器中删除
        FileClient client = fileConfigService.getFileClient(file.getConfigId());
        Assert.notNull(client, "客户端({}) 不能为空", file.getConfigId());
        try {
            client.delete(file.getPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 删除记录
        fileService.deleteById(file.getId());
    }

    @Override
    public void getFilePresignedUrl(InputObject inputObject, OutputObject outputObject) {
        String path = inputObject.getParams().get("path").toString();
        FileClient fileClient = fileConfigService.getMasterFileClient();
        try {
            FilePresignedUrlRespDTO presignedObjectUrl = fileClient.getPresignedObjectUrl(path);
            presignedObjectUrl.setConfigId(fileClient.getId());
            outputObject.setBean(presignedObjectUrl);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
