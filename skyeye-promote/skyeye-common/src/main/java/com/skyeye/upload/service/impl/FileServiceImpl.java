/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.upload.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.upload.dao.FileDao;
import com.skyeye.upload.entity.File;
import com.skyeye.upload.service.FileService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: FileServiceImpl
 * @Description: 文件服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 19:56
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "文件", groupName = "文件")
public class FileServiceImpl extends SkyeyeBusinessServiceImpl<FileDao, File> implements FileService {

    // 忽略多租户的文件类型
    private String[] IGNORE_TENANT_FILE_SAVEPATH = {FileConstants.FileUploadPath.USERPHOTO.getSavePath()};

    @Override
    @IgnoreTenant
    public File queryByPath(String path) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(File::getPath), path);
        boolean isIgnoreTenant = isIgnoreTenant(Arrays.asList(path));
        if (isIgnoreTenant && tenantEnable) {
            wrapper.eq(CommonConstants.TENANT_ID_FIELD, TenantContext.getTenantId());
        }
        File file = getOne(wrapper, false);
        return file;
    }

    @Override
    @IgnoreTenant
    public void queryFileListByPath(InputObject inputObject, OutputObject outputObject) {
        String path = inputObject.getParams().get("path").toString();
        if (StrUtil.isEmpty(path)) {
            return;
        }
        List<String> pathList = Arrays.asList(path.split(CommonCharConstants.COMMA_MARK)).stream()
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(pathList)) {
            return;
        }
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.in(MybatisPlusUtil.toColumns(File::getPath), pathList);
        boolean isIgnoreTenant = isIgnoreTenant(pathList);
        if (isIgnoreTenant && tenantEnable) {
            wrapper.eq(CommonConstants.TENANT_ID_FIELD, TenantContext.getTenantId());
        }
        List<File> fileList = list(wrapper);
        outputObject.setBeans(fileList);
        outputObject.settotal(fileList.size());
    }

    private boolean isIgnoreTenant(List<String> pathList) {
        if (CollectionUtil.isEmpty(pathList)) {
            return false;
        }

        // 检查是否有任何一个路径不在忽略多租户的列表中
        for (String path : pathList) {
            if (StrUtil.isNotEmpty(path)) {
                boolean isIgnore = false;
                // 检查当前路径是否在忽略多租户的路径列表中
                for (String ignorePath : IGNORE_TENANT_FILE_SAVEPATH) {
                    if (path.contains(ignorePath)) {
                        isIgnore = true;
                        break;
                    }
                }
                // 如果当前路径不在忽略列表中，说明需要多租户隔离，返回 true
                if (!isIgnore) {
                    return true;
                }
            }
        }

        // 所有路径都在忽略列表中，返回 false
        return false;
    }

}
