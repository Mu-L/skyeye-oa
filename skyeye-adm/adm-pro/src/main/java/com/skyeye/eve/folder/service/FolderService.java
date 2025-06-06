/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.folder.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.folder.entity.Folder;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FolderService
 * @Description: 笔记文件夹管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/25 19:26
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface FolderService extends SkyeyeBusinessService<Folder> {

    String setParentId(String id);

    void deleteByParentId(String parentId);

    void editNameById(String id, String name, String userId);

    void queryFolderByUserId(InputObject inputObject, OutputObject outputObject);

    List<Map<String, Object>> queryFolderAndChildList(List<String> ids);

    int insertFileFolderList(List<Map<String, Object>> folderList, String tenantId);

}
