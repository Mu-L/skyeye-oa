/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.folder.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.folder.entity.Folder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FolderDao
 * @Description: 笔记文件夹管理数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/25 19:27
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface FolderDao extends SkyeyeBaseMapper<Folder> {

    @IgnoreTenant
    List<Map<String, Object>> queryFolderByUserId(@Param("parentId") String parentId,
                                                  @Param("createId") String createId,
                                                  @Param("moveId") String moveId,
                                                  @Param("deleteFlag") Integer deleteFlag,
                                                  @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryFolderAndChildList(@Param("ids") List<String> ids,
                                                      @Param("deleteFlag") Integer deleteFlag,
                                                      @Param("tenantId") String tenantId);

    @IgnoreTenant
    int insertFileFolderList(@Param("folderList") List<Map<String, Object>> folderList,
                             @Param("tenantId") String tenantId);

}
