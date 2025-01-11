/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ueditor.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.DataCommonUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.eve.dao.EditUploadDao;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.ueditor.service.EditUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;


@Service
public class EditUploadServiceImpl implements EditUploadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditUploadServiceImpl.class);

    @Autowired
    private EditUploadDao editUploadDao;

    @Autowired
    public JedisClientService jedisClient;

    @Value("${IMAGES_PATH}")
    private String tPath;

    /**
     * 上传富文本图片
     */
    @Override
    public void uploadContentPic(InputObject inputObject, OutputObject outputObject) {
        // 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(PutObject.getRequest().getSession().getServletContext());
        // 检查form中是否有enctype="multipart/form-data"
        if (!multipartResolver.isMultipart(PutObject.getRequest())) {
            throw new RuntimeException("表单中没有发现文件信息");
        }
        Map<String, Object> rs = new HashMap<>();
        // 将request变成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) PutObject.getRequest();
        // 获取multiRequest 中所有的文件名
        Iterator iter = multiRequest.getFileNames();
        String fileName;
        // 原始文件名   UEDITOR创建页面元素时的alt和title属性
        String originalFileName;
        try {
            while (iter.hasNext()) {
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                if (file == null) {
                    break;
                }
                // 取得文件的原始文件名称
                fileName = file.getOriginalFilename();
                originalFileName = fileName;
                /*你的处理图片的代码*/
                rs.put("state", "SUCCESS");// UEDITOR的规则:不为SUCCESS则显示state的内容
                rs.put("title", originalFileName);
                rs.put("original", originalFileName);
                String basePath = tPath + FileConstants.FileUploadPath.getSavePath(FileConstants.FileUploadPath.UEDITOR.getType()[0]);
                FileUtil.createDirs(basePath);

                // 得到文件扩展名
                String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
                // 自定义的文件名称
                String newFileName = String.format(Locale.ROOT, "%s.%s", System.currentTimeMillis(), fileExtName);
                String path = basePath + "/" + newFileName;
                file.transferTo(new File(path));
                // 能访问到你现在图片的路径
                String filePath = FileConstants.FileUploadPath.getVisitPath(FileConstants.FileUploadPath.UEDITOR.getType()[0]) + newFileName;
                rs.put("url", filePath);
                Map<String, Object> bean = new HashMap<>();
                bean.put("imgPath", filePath);
                bean.put("fileOriginalName", fileName);
                // 用户信息
                Map<String, Object> userMation = InputObject.getLogParamsStatic();
                DataCommonUtil.setCommonData(bean, userMation.get("id").toString());
                bean.put("createType", "2");
                editUploadDao.insertFileImgMation(bean);
                rs.put("returnCode", CommonNumConstants.NUM_ZERO);
                rs.put("returnMessage", "上传成功!");
                outputObject.setObject(rs);
            }
        } catch (Exception ee) {
            LOGGER.warn("uploadContentPic failed {}.", ee);
            rs.put("state", "文件上传失败!");
            rs.put("url", "");
            rs.put("title", "");
            rs.put("original", "");
            rs.put("returnCode", "-9999");
            rs.put("returnMessage", "文件上传失败!");
            outputObject.setObject(rs);
        }
    }

    /**
     * 回显富文本图片
     */
    @Override
    public Map<String, Object> downloadContentPic(HttpServletRequest req, String userId) {
        Map<String, Object> rs = new HashMap<>();
        rs.put("state", "SUCCESS");// UEDITOR的规则:不为SUCCESS则显示state的内容
        Map<String, Object> bean = new HashMap<>();
        // 用户信息
        bean.put("createId", userId);
        bean.put("url", "");
        int page = Integer.parseInt(req.getParameter("start")) / Integer.parseInt(req.getParameter("size"));
        page++;
        int limit = Integer.parseInt(req.getParameter("size"));
        Page pages = PageHelper.startPage(page, limit);
        List<Map<String, Object>> beans = editUploadDao.queryFileImgMation(bean);
        //获取当前页数的总数
        long total = pages.getTotal();
        rs.put("list", beans);
        rs.put("total", total);
        rs.put("start", req.getParameter("start"));
        return rs;
    }


}
