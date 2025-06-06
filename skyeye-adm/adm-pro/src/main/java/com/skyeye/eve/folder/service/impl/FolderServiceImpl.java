/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.folder.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constans.NoteConstants;
import com.skyeye.eve.folder.dao.FolderDao;
import com.skyeye.eve.folder.entity.Folder;
import com.skyeye.eve.folder.service.FolderService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FolderServiceImpl
 * @Description: 笔记文件夹管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/25 19:27
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "笔记文件夹管理", groupName = "笔记管理")
public class FolderServiceImpl extends SkyeyeBusinessServiceImpl<FolderDao, Folder> implements FolderService {

    @Override
    public void createPrepose(Folder entity) {
        String parentId = setParentId(entity.getParentId());
        entity.setParentId(parentId);
    }

    /**
     * 根据节点id设置ParentId
     *
     * @param id
     * @return
     */
    @Override
    public String setParentId(String id) {
        if ("2".equals(id)) {
            return id + ",";
        } else {
            Folder folder = selectById(id);
            if (ObjectUtil.isNotEmpty(folder)) {
                return folder.getParentId() + id + ",";
            }
            throw new CustomException("错误的文件夹编码！");
        }
    }

    @Override
    public void deleteByParentId(String parentId) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        UpdateWrapper<Folder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.apply("INSTR(CONCAT(',', " + MybatisPlusUtil.toColumns(Folder::getParentId) + ", ','), CONCAT(',', {0}, ','))", parentId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Folder::getDeleteFlag), DeleteFlagEnum.DELETED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(Folder::getLastUpdateId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Folder::getLastUpdateTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
    }

    @Override
    public void editNameById(String id, String name, String userId) {
        UpdateWrapper<Folder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Folder::getName), name);
        updateWrapper.set(MybatisPlusUtil.toColumns(Folder::getLastUpdateId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Folder::getLastUpdateTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
    }

    @Override
    public void queryFolderByUserId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String parentId = map.get("parentId").toString();
        String moveId = map.get("moveId").toString();
        List<Map<String, Object>> beans;
        if (ToolUtil.isBlank(parentId) || "0".equals(parentId)) {
            // 加载一级文件夹
            beans = NoteConstants.getFileMyNoteDefaultFolder();
        } else {
            // 加载子文件夹
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
            beans = skyeyeBaseMapper.queryFolderByUserId(parentId, userId, moveId, DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
        }
        outputObject.setBeans(beans);
    }

    @Override
    public List<Map<String, Object>> queryFolderAndChildList(List<String> ids) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        return skyeyeBaseMapper.queryFolderAndChildList(ids, DeleteFlagEnum.NOT_DELETE.getKey(), tenantId);
    }

    @Override
    public int insertFileFolderList(List<Map<String, Object>> folderList, String tenantId) {
        return skyeyeBaseMapper.insertFileFolderList(folderList, tenantId);
    }


}
