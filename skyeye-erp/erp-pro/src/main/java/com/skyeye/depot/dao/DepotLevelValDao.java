/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.depot.entity.DepotLevelVal;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName: DepotLevelValDao
 * @Description: 仓库级别的值数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/6 10:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DepotLevelValDao extends SkyeyeBaseMapper<DepotLevelVal> {

    /**
     * 根据父id查询所有的子节点信息(包含父id)，如果是多个
     *
     * @param ids 父id
     * @return
     */
    @IgnoreTenant
    List<String> queryAllChildIdsByParentId(@Param("ids") List<String> ids,
                                            @Param("tenantId") String tenantId);

}
