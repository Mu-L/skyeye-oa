/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.model.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveUserStaffDao
 * @Description: 员工信息类----后期改为调rest服务
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/8 22:12
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveUserStaffDao {

    @IgnoreTenant
    List<Map<String, Object>> queryStaffCheckWorkTimeRelationByStaffId(@Param("staffId") String staffId,
                                                                       @Param("tenantId") String tenantId);

}
