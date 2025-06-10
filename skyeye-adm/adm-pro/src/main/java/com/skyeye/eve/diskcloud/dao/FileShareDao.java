/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.diskcloud.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.diskcloud.entity.FileShare;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FileShareDao
 * @Description: 文件分享数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/18 11:41
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface FileShareDao extends SkyeyeBaseMapper<FileShare> {

    @IgnoreTenant
    List<Map<String, Object>> queryShareFileList(CommonPageInfo pageInfo);

    @IgnoreTenant
    List<Map<String, Object>> queryShareFileFirstListByParentId(@Param("id") String id, @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryShareFileListByParentId(@Param("folderId") String folderId,
                                                           @Param("deleteFlag") Integer deleteFlag,
                                                           @Param("tenantId") String tenantId);
}
