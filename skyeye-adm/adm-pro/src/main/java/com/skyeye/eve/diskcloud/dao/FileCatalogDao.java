/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.diskcloud.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.diskcloud.entity.FileCatalog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FileCatalogDao
 * @Description: 文件夹管理数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/17 11:28
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface FileCatalogDao extends SkyeyeBaseMapper<FileCatalog> {

    @IgnoreTenant
    List<Map<String, Object>> queryFolderAndChildList(@Param("ids") List<String> ids,
                                                      @Param("deleteFlag") Integer deleteFlag,
                                                      @Param("tenantId") String tenantId);

}
