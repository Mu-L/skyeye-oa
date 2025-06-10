/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.diskcloud.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constans.DiskCloudConstants;
import com.skyeye.eve.diskcloud.classenum.DefaultFolder;
import com.skyeye.eve.diskcloud.dao.FileCatalogDao;
import com.skyeye.eve.diskcloud.entity.FileCatalog;
import com.skyeye.eve.diskcloud.service.FileCatalogService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: FileCatalogServiceImpl
 * @Description: 文件夹管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/17 11:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "文件夹管理", groupName = "文件夹管理")
public class FileCatalogServiceImpl extends SkyeyeBusinessServiceImpl<FileCatalogDao, FileCatalog> implements FileCatalogService {

    @Override
    public void createPrepose(FileCatalog entity) {
        String parentId = setParentId(entity.getParentId());
        entity.setParentId(parentId);
        entity.setLogoPath(DiskCloudConstants.SYS_FILE_CONSOLE_IS_FOLDER_LOGO_PATH);
    }

    @Override
    protected void deletePreExecution(FileCatalog entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (!StrUtil.equals(entity.getCreateId(), userId)) {
            throw new CustomException("无法删除不是自己创建的文件夹");
        }
    }

    @Override
    public void deleteByParentId(String parentId) {
        // 先查询，删除对应的缓存信息
        List<String> childIds = queryByParentId(parentId);

        String userId = InputObject.getLogParamsStatic().get("id").toString();
        UpdateWrapper<FileCatalog> updateWrapper = new UpdateWrapper<>();
        updateWrapper.apply("INSTR(CONCAT(',', " + MybatisPlusUtil.toColumns(FileCatalog::getParentId) + ", ','), CONCAT(',', {0}, ','))", parentId);
        updateWrapper.set(MybatisPlusUtil.toColumns(FileCatalog::getDeleteFlag), DeleteFlagEnum.DELETED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(FileCatalog::getLastUpdateId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(FileCatalog::getLastUpdateTime), DateUtil.getTimeAndToString());
        update(updateWrapper);

        refreshCache(childIds);
    }

    private List<String> queryByParentId(String parentId) {
        QueryWrapper<FileCatalog> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("INSTR(CONCAT(',', " + MybatisPlusUtil.toColumns(FileCatalog::getParentId) + ", ','), CONCAT(',', {0}, ','))", parentId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(FileCatalog::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        List<FileCatalog> fileCatalogs = list(queryWrapper);
        return fileCatalogs.stream().map(FileCatalog::getId).collect(Collectors.toList());
    }

    @Override
    public void editNameById(String id, String name, String userId) {
        UpdateWrapper<FileCatalog> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(FileCatalog::getName), name);
        updateWrapper.set(MybatisPlusUtil.toColumns(FileCatalog::getLastUpdateId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(FileCatalog::getLastUpdateTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public String setParentId(String id) {
        if (DefaultFolder.FAVORITES.getKey().equals(id)
            || DefaultFolder.FOLDER.getKey().equals(id)
            || DefaultFolder.SKYDRIVE.getKey().equals(id)) {
            // 收藏夹   我的文档   企业网盘
            return id + ",";
        } else {
            // 根据当前所属目录查询该目录的父id
            FileCatalog parentCatalog = selectById(id);
            if (ObjectUtil.isNotEmpty(parentCatalog) && StrUtil.isNotEmpty(parentCatalog.getId())) {
                return parentCatalog.getParentId() + id + ",";
            } else {
                throw new CustomException("所属文件夹不存在");
            }
        }
    }

    @Override
    public List<Map<String, Object>> queryFolderAndChildList(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return CollectionUtil.newArrayList();
        }
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        return skyeyeBaseMapper.queryFolderAndChildList(ids, DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
    }
}
