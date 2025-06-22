/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.FileUtil;
import com.skyeye.eve.dao.CodeModelHistoryDao;
import com.skyeye.eve.entity.codedoc.history.CodeModelHistoryQueryDo;
import com.skyeye.eve.service.CodeModelHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName: CodeModelHistoryServiceImpl
 * @Description: 代码生成历史服务层--平台隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/6/22 11:15
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "代码生成历史", groupName = "代码生成历史", tenant = TenantEnum.PLATE)
public class CodeModelHistoryServiceImpl implements CodeModelHistoryService {

    @Autowired
    private CodeModelHistoryDao codeModelHistoryDao;

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    /**
     * 获取模板生成历史列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryCodeModelHistoryList(InputObject inputObject, OutputObject outputObject) {
        CodeModelHistoryQueryDo codeModelHistoryQuery = inputObject.getParams(CodeModelHistoryQueryDo.class);
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        codeModelHistoryQuery.setTenantId(tenantId);
        Page pages = PageHelper.startPage(codeModelHistoryQuery.getPage(), codeModelHistoryQuery.getLimit());
        List<Map<String, Object>> beans = codeModelHistoryDao.queryCodeModelHistoryList(codeModelHistoryQuery);
        String basePath = tPath + FileConstants.FileUploadPath.CODE_GENERATOR.getSavePath();
        for (Map<String, Object> bean : beans) {
            File file = new File(basePath + "/" + bean.get("filePath").toString());
            if (!file.exists()) {
                bean.put("isExist", "否");
            } else {
                bean.put("isExist", "是");
            }
        }
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 重新生成文件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void insertCodeModelHistoryCreate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String basePath = tPath + FileConstants.FileUploadPath.CODE_GENERATOR.getSavePath();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        map.put("tenantId", tenantId);
        FileUtil.createDirs(basePath);
        String strZipPath = basePath + "/" + map.get("filePath").toString();
        File zipFile = new File(strZipPath);
        if (zipFile.exists()) {
            outputObject.setreturnMessage("该文件已存在，生成失败。");
        } else {
            ZipOutputStream out = null;
            try {
                out = new ZipOutputStream(new FileOutputStream(strZipPath));
                byte[] buffer = new byte[1024];
                List<Map<String, Object>> beans = codeModelHistoryDao.queryCodeModelHistoryListByFilePath(map);
                for (Map<String, Object> bean : beans) {
                    //加入压缩包
                    ByteArrayInputStream stream = new ByteArrayInputStream(bean.get("content").toString().getBytes());
                    out.putNextEntry(new ZipEntry(bean.get("fileName").toString() + "." + bean.get("fileType").toString().toLowerCase()));
                    int len;
                    // 读入需要下载的文件的内容，打包到zip文件
                    while ((len = stream.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    out.closeEntry();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                FileUtil.close(out);
            }
        }
    }

    /**
     * 下载文件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void downloadCodeModelHistory(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String basePath = tPath + FileConstants.FileUploadPath.CODE_GENERATOR.getSavePath();
        String strZipPath = basePath + "/" + map.get("filePath").toString();
        InputStream bis = null;
        BufferedOutputStream out = null;
        try {
            // 获取输入流
            bis = new BufferedInputStream(new FileInputStream(new File(strZipPath)));
            PutObject.getResponse().setHeader("REQUESTMATION", "DOWNLOAD");
            // 转码，免得文件名中文乱码
            String filename = URLEncoder.encode(map.get("filePath").toString(), "UTF-8");
            // 设置文件下载头
            PutObject.getResponse().addHeader("Content-Disposition", "attachment;filename=" + filename);
            // 1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
            PutObject.getResponse().setContentType("multipart/form-data");
            out = new BufferedOutputStream(PutObject.getResponse().getOutputStream());
            int len = 0;
            while ((len = bis.read()) != -1) {
                out.write(len);
                out.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            FileUtil.close(bis);
            FileUtil.close(out);
        }
    }

}
