/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.annotation.tenant.TenantIsolation;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.DataCommonUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.HttpClient;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CommonDao;
import com.skyeye.eve.service.CommonService;
import com.skyeye.exception.CustomException;
import com.skyeye.win.entity.SysEveWinBgPic;
import com.skyeye.win.entity.SysEveWinLockBgPic;
import com.skyeye.win.entity.SysEveWinThemeColor;
import com.skyeye.win.service.SysEveWinBgPicService;
import com.skyeye.win.service.SysEveWinLockBgPicService;
import com.skyeye.win.service.SysEveWinThemeColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName: CommonServiceImpl
 * @Description: 公共类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 17:29
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private CommonDao commonDao;

    @Autowired
    private SysEveWinBgPicService sysEveWinBgPicService;

    @Autowired
    private SysEveWinLockBgPicService sysEveWinLockBgPicService;

    @Autowired
    private SysEveWinThemeColorService sysEveWinThemeColorService;

    @Value("${IMAGES_PATH}")
    private String tPath;

    /**
     * 代码生成器生成下载文件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    @TenantIsolation(TenantEnum.PLATE)
    public void downloadFileByJsonData(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> array = JSONUtil.toList(map.get("jsonData").toString(), null);
        List<Map<String, Object>> inBeans = new ArrayList<>();
        Map<String, Object> user = inputObject.getLogParams();
        String zipName = ToolUtil.getSurFaceId() + ".zip";
        String basePath = tPath + FileConstants.FileUploadPath.CODE_GENERATOR.getSavePath();
        FileUtil.createDirs(basePath);
        String strZipPath = tPath + FileConstants.FileUploadPath.CODE_GENERATOR.getSavePath() + "/" + zipName;
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(strZipPath));
            byte[] buffer = new byte[1024];

            for (int i = 0; i < array.size(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                String content = object.getStr("content");
                // 加入压缩包
                ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes());
                if ("javascript".equals(object.getStr("modelType").toLowerCase())) {
                    out.putNextEntry(new ZipEntry(object.getStr("fileName") + ".js"));
                } else {
                    out.putNextEntry(new ZipEntry(object.getStr("fileName") + "." + object.getStr("modelType").toLowerCase()));
                }
                int len;
                // 读入需要下载的文件的内容，打包到zip文件
                while ((len = stream.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.closeEntry();
                Map<String, Object> bean = getCodeModelHoitoryObject(user, zipName, object, content);
                inBeans.add(bean);
            }
        } catch (Exception ex) {
            throw new CustomException(ex);
        } finally {
            FileUtil.close(out);
        }
        commonDao.insertCodeModelHistory(inBeans);
    }

    private Map<String, Object> getCodeModelHoitoryObject(Map<String, Object> user, String zipName, JSONObject object, String content) {
        Map<String, Object> bean = new HashMap<>();
        bean.put("tableName", object.getStr("tableName"));
        bean.put("groupId", object.getStr("groupId"));
        bean.put("modelId", object.getStr("modelId"));
        bean.put("content", content);
        bean.put("fileName", object.getStr("fileName"));
        if ("javascript".equals(object.getStr("modelType").toLowerCase())) {
            bean.put("fileType", "js");
        } else {
            bean.put("fileType", object.getStr("modelType").toLowerCase());
        }
        bean.put("filePath", zipName);
        DataCommonUtil.setCommonData(bean, user.get("id").toString());
        return bean;
    }

    /**
     * 获取win系统桌列表信息供展示
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySysWinMationById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();

        // 获取win系统桌面图片列表供展示
        List<SysEveWinBgPic> winBgPic = sysEveWinBgPicService.querySystemSysEveWinBgPicList();

        // 获取win系统锁屏桌面图片列表供展示
        List<SysEveWinLockBgPic> winLockBgPic = sysEveWinLockBgPicService.querySystemSysEveWinLockBgPicList();

        // 获取win系统主题颜色列表供展示
        List<SysEveWinThemeColor> winThemeColor = sysEveWinThemeColorService.querySysEveWinThemeColorList();

        map.put("winBgPic", winBgPic);
        map.put("winLockBgPic", winLockBgPic);
        map.put("winThemeColor", winThemeColor);
        outputObject.setBean(map);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 根据文件类型获取文件的保存地址以及访问地址
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryFilePathByFileType(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Integer fileType = Integer.parseInt(map.get("fileType").toString());
        String savePath = tPath + FileConstants.FileUploadPath.getSavePath(fileType);
        FileUtil.createDirs(savePath);
        String visitPath = FileConstants.FileUploadPath.getVisitPath(fileType);

        Map<String, Object> result = new HashMap<>();
        result.put("savePath", savePath);
        result.put("visitPath", visitPath);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 验证接口是否正确
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryInterfaceIsTrueOrNot(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        map.put("loginPCIp", PutObject.getRequest().getParameter("loginPCIp"));
        map.put("userToken", PutObject.getRequest().getParameter("userToken"));
        String str = HttpClient.doPost(map.get("interfa").toString(), map);
        if (!ToolUtil.isBlank(str)) {
            if (ToolUtil.isJson(str)) {
                Map<String, Object> json = JSONUtil.toBean(str, null);
                if ("0".equals(json.get("returnCode").toString())) {
                    if (!ToolUtil.isBlank(json.get("rows").toString())) {
                        map.put("aData", json.get("rows").toString());
                        outputObject.setBean(map);
                        outputObject.settotal(CommonNumConstants.NUM_ONE);
                    } else {
                        outputObject.setreturnMessage("该接口没有拿到数据，请重新填写接口！");
                    }
                } else {
                    outputObject.setreturnMessage("该接口无效，请重新填写接口!");
                }
            } else {
                outputObject.setreturnMessage("接口拿到的不是json串，请重新填写接口!");
            }
        } else {
            outputObject.setreturnMessage("该接口无效，请重新填写接口!");
        }
    }

    /**
     * 获取接口中的值
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryInterfaceValue(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        map.put("loginPCIp", PutObject.getRequest().getParameter("loginPCIp"));
        map.put("userToken", PutObject.getRequest().getParameter("userToken"));
        String str = HttpClient.doPost(map.get("interfa").toString(), map);
        Map<String, Object> json = JSONUtil.toBean(str, null);
        map.put("aData", json.get("rows").toString());
        outputObject.setBean(map);
    }

}
