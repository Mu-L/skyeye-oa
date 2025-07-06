/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.diskcloud.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.BytesUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constans.DiskCloudConstants;
import com.skyeye.eve.diskcloud.classenum.DickCloudType;
import com.skyeye.eve.diskcloud.classenum.ShareState;
import com.skyeye.eve.diskcloud.classenum.ShareType;
import com.skyeye.eve.diskcloud.dao.FileShareDao;
import com.skyeye.eve.diskcloud.entity.FileCatalog;
import com.skyeye.eve.diskcloud.entity.FileConsole;
import com.skyeye.eve.diskcloud.entity.FileShare;
import com.skyeye.eve.diskcloud.service.FileCatalogService;
import com.skyeye.eve.diskcloud.service.FileConsoleService;
import com.skyeye.eve.diskcloud.service.FileShareService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FileShareServiceImpl
 * @Description: 文件分享服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/18 11:43
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "文件分享管理", groupName = "文件分享管理")
public class FileShareServiceImpl extends SkyeyeBusinessServiceImpl<FileShareDao, FileShare> implements FileShareService {

    @Autowired
    private FileConsoleService fileConsoleService;

    @Autowired
    private FileCatalogService fileCatalogService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        if (tenantEnable) {
            pageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryShareFileList(pageInfo);
        beans.forEach(bean -> {
            bean.put("fileTypeName", DickCloudType.getTypeName(bean.get("fileType").toString()));
            bean.put("shareTypeName", ShareType.getTypeName(Integer.parseInt(bean.get("shareType").toString())));
            bean.put("stateName", ShareState.getTypeName(Integer.parseInt(bean.get("state").toString())));
        });
        return beans;
    }

    @Override
    public void createPrepose(FileShare entity) {
        FileCatalog fileCatalog = fileCatalogService.selectById(entity.getFileId());
        if (ObjectUtil.isNotEmpty(fileCatalog) && StrUtil.isNotEmpty(fileCatalog.getId())) {
            entity.setFileType(DickCloudType.FOLDER.getKey());
            entity.setShareName(fileCatalog.getName());
        }

        FileConsole fileConsole = fileConsoleService.selectById(entity.getFileId());
        if (ObjectUtil.isNotEmpty(fileConsole) && StrUtil.isNotEmpty(fileConsole.getId())) {
            entity.setFileType(DickCloudType.FILE.getKey());
            entity.setShareName(fileConsole.getName());
        }
        if (ShareType.PRIVATE.getKey().equals(entity.getShareType())) {
            // 有提取码
            entity.setSharePassword(ToolUtil.getFourWord());
        }
        entity.setShareUrl(DiskCloudConstants.getFileShareUrl(StrUtil.EMPTY));
        entity.setShareCode(ToolUtil.randomStr(0, 20));
        entity.setState(ShareState.NORMAL.getKey());
    }

    @Override
    protected void createPostpose(FileShare entity, String userId) {
        String shareUrl = DiskCloudConstants.getFileShareUrl(entity.getId());
        UpdateWrapper<FileShare> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, entity.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(FileShare::getShareUrl), shareUrl);
        update(updateWrapper);
    }

    @Override
    public void queryShareFileMationById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        FileShare fileShare = selectById(id);
        if (ObjectUtil.isEmpty(fileShare) || StrUtil.isEmpty(fileShare.getId())) {
            throw new CustomException("该数据不存在");
        }
        fileShare.setSharePassword(StrUtil.EMPTY);
        iAuthUserService.setDataMation(fileShare, FileShare::getCreateId);
        outputObject.setBean(fileShare);
    }

    @Override
    public void checkShareFilePwdMation(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String sharePassword = inputObject.getParams().get("sharePassword").toString();
        FileShare fileShare = selectById(id);
        if (ObjectUtil.isEmpty(fileShare)) {
            throw new CustomException("该数据不存在");
        }
        if (!StrUtil.equalsIgnoreCase(fileShare.getSharePassword(), sharePassword)) {
            throw new CustomException("提取码输入错误");
        }
    }

    public boolean tenantEnable = false;

    @Override
    public void queryShareFileListByParentId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String folderId = map.get("folderId").toString();
        String id = map.get("id").toString();
        List<Map<String, Object>> beans;
        String tenantId = TenantContext.getTenantId();
        System.out.println(tenantId);
        if ("-1".equals(folderId)) {
            // 加载初始目录
            beans = skyeyeBaseMapper.queryShareFileFirstListByParentId(id, tenantId);
        } else {
            // 加载子目录
            beans = skyeyeBaseMapper.queryShareFileListByParentId(folderId, DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
        }
        for (Map<String, Object> bean : beans) {
            if (!DickCloudType.FOLDER.getKey().equals(bean.get("fileType").toString())) {
                // 不是文件夹
                String size = BytesUtil.sizeFormatNum2String(Long.parseLong(bean.get("fileSize").toString()));
                bean.put("fileSize", size);
            }
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

}
