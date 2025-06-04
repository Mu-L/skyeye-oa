/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.task.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.task.entity.Task;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName: ProTaskDao
 * @Description: 项目任务相关数据交互层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/1 12:32
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProTaskDao extends SkyeyeBaseMapper<Task> {

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
