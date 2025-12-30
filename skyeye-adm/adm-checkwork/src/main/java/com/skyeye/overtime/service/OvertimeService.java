/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.overtime.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.overtime.entity.OverTime;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: OvertimeService
 * @Description: 加班申请服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/5 21:52
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface OvertimeService extends SkyeyeBusinessService<OverTime> {

    /**
     * 获取指定员工在指定月份的所有审核通过的加班申请数据
     *
     * @param userId 用户id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     */
    List<Map<String, Object>> queryStateIsSuccessWorkOvertimeDayByUserIdAndMonths(String userId, List<String> months);
}
