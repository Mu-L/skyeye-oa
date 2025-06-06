/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.note.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.note.entity.Note;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: NoteDao
 * @Description: 笔记管理数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/25 19:20
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface NoteDao extends SkyeyeBaseMapper<Note> {

    @IgnoreTenant
    List<Map<String, Object>> queryNewNoteListByUserId(CommonPageInfo pageInfo);

    @IgnoreTenant
    List<Map<String, Object>> queryFileAndContentListByFolderId(Map<String, Object> map);

    @IgnoreTenant
    List<Map<String, Object>> queryFileList(@Param("folderList") List<Map<String, Object>> folderList,
                                            @Param("deleteFlag") Integer deleteFlag,
                                            @Param("tenantId") String tenantId);

    @IgnoreTenant
    int insertFileListByList(@Param("fileList") List<Map<String, Object>> fileList,
                             @Param("tenantId") String tenantId);

}
