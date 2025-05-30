/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.diskcloud.dao;

import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.diskcloud.entity.FileConsole;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FileConsoleDao
 * @Description: 文件管库数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/17 17:26
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface FileConsoleDao extends SkyeyeBaseMapper<FileConsole> {

    List<Map<String, Object>> queryFileFolderByUserIdAndParentId(Map<String, Object> map);

    List<Map<String, Object>> queryFilesListByFolderId(Map<String, Object> map);

    List<Map<String, Object>> queryChildFileListByFolder(@Param("list") List<Map<String, Object>> folderNew,
                                                         @Param("deleteFlag") Integer deleteFlag);

    int insertFolderList(List<Map<String, Object>> folderNew);

    int insertShareFileListByList(List<Map<String, Object>> fileNew);

    List<Map<String, Object>> queryShareFileListByFileList(@Param("ids") List<String> ids,
                                                           @Param("deleteFlag") Integer deleteFlag);

    Map<String, Object> queryAllNumFile(@Param("deleteFlag") Integer deleteFlag,
                                        @Param("tenantId") String tenantId);

    Map<String, Object> queryAllNumFileToday(@Param("deleteFlag") Integer deleteFlag,
                                             @Param("tenantId") String tenantId);

    Map<String, Object> queryAllNumFileThisWeek(@Param("deleteFlag") Integer deleteFlag,
                                                @Param("tenantId") String tenantId);

    List<Map<String, Object>> queryFileTypeNum(@Param("deleteFlag") Integer deleteFlag,
                                               @Param("tenantId") String tenantId);

    List<Map<String, Object>> queryFileStorageNum(@Param("deleteFlag") Integer deleteFlag,
                                                  @Param("tenantId") String tenantId);

    List<Map<String, Object>> queryNewFileNum(@Param("deleteFlag") Integer deleteFlag,
                                              @Param("tenantId") String tenantId);

    List<Map<String, Object>> queryFileTypeNumSevenDay(@Param("deleteFlag") Integer deleteFlag,
                                                       @Param("tenantId") String tenantId);

}
