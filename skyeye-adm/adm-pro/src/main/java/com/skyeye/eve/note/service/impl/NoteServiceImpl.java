/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.note.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.folder.entity.Folder;
import com.skyeye.eve.folder.service.FolderService;
import com.skyeye.eve.note.classenum.FileFolderType;
import com.skyeye.eve.note.classenum.NoteType;
import com.skyeye.eve.note.dao.NoteDao;
import com.skyeye.eve.note.entity.Note;
import com.skyeye.eve.note.service.NoteService;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MyNoteServiceImpl
 * @Description: 笔记管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:56
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "笔记管理", groupName = "笔记管理")
public class NoteServiceImpl extends SkyeyeBusinessServiceImpl<NoteDao, Note> implements NoteService {

    @Autowired
    private FolderService folderService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    /**
     * 删除文件夹或文件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteFileFolderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        if (FileFolderType.FOLDER.getKey().equals(map.get("fileType").toString())) {
            // 操作文件夹表  删除自身文件夹
            folderService.deleteById(id);
            // 删除子文件夹
            folderService.deleteByParentId(id);
            // 删除子文件
            deleteByParentId(id);
        } else {
            // 操作笔记内容表  删除自身文件
            deleteById(id);
        }
    }

    private void deleteByParentId(String parentId) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        UpdateWrapper<Note> updateWrapper = new UpdateWrapper<>();
        updateWrapper.apply("INSTR(CONCAT(',', " + MybatisPlusUtil.toColumns(Note::getParentId) + ", ','), CONCAT(',', {0}, ','))", parentId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Note::getDeleteFlag), DeleteFlagEnum.DELETED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(Note::getLastUpdateId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Note::getLastUpdateTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
    }

    /**
     * 编辑文件夹或者文件的名称
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editFileFolderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String name = map.get("name").toString();
        String fileType = map.get("fileType").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (FileFolderType.FOLDER.getKey().equals(fileType)) {
            // 操作文件夹表
            folderService.editNameById(id, name, userId);
        } else {
            // 操作笔记表
            editNameById(id, name, userId);
        }
    }

    private void editNameById(String id, String name, String userId) {
        UpdateWrapper<Note> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Note::getName), name);
        updateWrapper.set(MybatisPlusUtil.toColumns(Note::getLastUpdateId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Note::getLastUpdateTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getKey());
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryNewNoteListByUserId(pageInfo);
        return beans;
    }

    @Override
    public void createPrepose(Note entity) {
        String parentId = folderService.setParentId(entity.getParentId());
        entity.setParentId(parentId);
        entity.setIconLogo(NoteType.getIconPathByType(entity.getType()));
        if (StrUtil.isNotEmpty(entity.getRemark()) && entity.getRemark().length() > 100) {
            entity.setRemark(entity.getRemark().substring(0, 99));
        }
    }

    @Override
    public void updatePrepose(Note entity) {
        Note note = selectById(entity.getId());
        entity.setParentId(note.getParentId());
        if (entity.getRemark().length() > 100) {
            entity.setRemark(entity.getRemark().substring(0, 99));
        }
    }

    @Override
    public Note selectById(String id) {
        Note note = super.selectById(id);
        iAuthUserService.setName(note, "createId", "createName");
        return note;
    }

    /**
     * 根据文件夹id获取文件夹下的文件夹和笔记列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryFileAndContentListByFolderId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("userId", user.get("id"));
        map.put("deleteFlag", DeleteFlagEnum.NOT_DELETE.getKey());
        map.put("tenant_id", PutObject.getRequest().getHeader("tenantId"));
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryFileAndContentListByFolderId(map);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 保存文件夹拖拽后的信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editFileToDragById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String newParentId;
        String targetId = map.get("targetId").toString();
        // 拖拽文件夹新的父id
        if (targetId.equals("2")) {
            newParentId = "2" + ",";
        } else {
            Folder folder = folderService.selectById(targetId);
            newParentId = folder.getParentId() + targetId + ",";
        }
        String arrId = map.get("arrId").toString();
        // 拖拽文件夹的id数组
        List<String> arr = Arrays.asList(arrId.split(","));
        if (CollectionUtil.isNotEmpty(arr)) {
            // 选择保存的文件夹不为空
            List<Map<String, Object>> folderList = folderService.queryFolderAndChildList(arr);
            if (CollectionUtil.isNotEmpty(folderList)) {
                // 删除之前的信息
                List<String> ids = folderList.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
                folderService.deleteById(ids);
            }
            List<Map<String, Object>> fileList = skyeyeBaseMapper.queryFileList(folderList, DeleteFlagEnum.NOT_DELETE.getKey());
            if (CollectionUtil.isNotEmpty(fileList)) {
                // 删除之前的信息
                List<String> ids = fileList.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
                deleteById(ids);
            }
            for (Map<String, Object> folder : folderList) {//重置父id
                String[] str = folder.get("parentId").toString().split(",");
                folder.put("directParentId", str[str.length - 1]);
                folder.put("newId", ToolUtil.getSurFaceId());
            }
            // 将数据转化为树的形式，方便进行父id重新赋值
            folderList = ToolUtil.listToTree(folderList, "id", "directParentId", "children");
            ToolUtil.FileListParentISEdit(folderList, newParentId);// 替换父id
            folderList = ToolUtil.FileTreeTransList(folderList);// 将树转为list
            // 为文件重置新parentId参数
            for (Map<String, Object> folder : folderList) {
                String parentId = folder.get("parentId").toString() + folder.get("id").toString() + ",";
                String nParentId = folder.get("newParentId").toString() + folder.get("newId").toString() + ",";
                // 重置文件的参数
                for (Map<String, Object> file : fileList) {
                    if (file.get("parentId").toString().equals(parentId)) {
                        file.put("newParentId", nParentId);
                        file.put("newId", ToolUtil.getSurFaceId());
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(folderList)) {
                folderService.insertFileFolderList(folderList);
            }
            if (CollectionUtil.isNotEmpty(fileList)) {
                skyeyeBaseMapper.insertFileListByList(fileList);
            }
        }
    }

    /**
     * 保存笔记移动后的信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editNoteToMoveById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 要移动的笔记id
        String rowId = map.get("moveId").toString();
        // 移动后的目录id
        String toId = map.get("toId").toString();
        String parentId = folderService.setParentId(toId);
        UpdateWrapper<Note> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, rowId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Note::getParentId), parentId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Note::getLastUpdateId), inputObject.getLogParams().get("id").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(Note::getLastUpdateTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
    }

    /**
     * 根据id(文件夹或者笔记id)将笔记输出为压缩包
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void outputNoteIsZipJob(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String type = map.get("type").toString();
        Map<String, Object> mation;
        if (FileFolderType.FOLDER.getKey().equals(type)) {
            // 获取文件夹信息
            mation = folderService.selectMapById(id);
        } else {
            // 获取文件信息
            mation = selectMapById(id);
        }
        if (CollectionUtil.isEmpty(mation)) {
            throw new CustomException("该信息不存在");
        }
        String userId = inputObject.getLogParams().get("id").toString();
        Map<String, Object> json = new HashMap<>();
        json.put("title", mation.get("name").toString());
        json.put("noteType", type);
        json.put("rowId", id);
        json.put("userId", userId);
        json.put("type", MqConstants.JobMateMationJobType.OUTPUT_NOTES_IS_ZIP.getJobType());
        // 启动任务
        JobMateMation jobMateMation = new JobMateMation();
        jobMateMation.setJsonStr(JSONUtil.toJsonStr(json));
        jobMateMation.setUserId(userId);
        iJobMateMationService.sendMQProducer(jobMateMation);
    }

}
