/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.cancleleave.dao;

import com.skyeye.cancleleave.entity.CancelLeave;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @ClassName: CancelLeaveDao
 * @Description: 销假申请数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/11 9:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface CancelLeaveDao extends SkyeyeBaseMapper<CancelLeave> {

    /**
     * 获取指定日期已经审核通过的信息
     *
     * @param createId   创建人
     * @param cancelDay  指定日期
     * @param childState 子对象状态
     * @return
     */
    Map<String, Object> queryCheckWorkCancelLeaveByMation(@Param("createId") String createId,
                                                          @Param("cancelDay") String cancelDay,
                                                          @Param("childState") String childState,
                                                          @Param("tenantId") String tenantId);
}
