/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.worktime.entity.CheckWorkTimeWeek;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CheckWorkTimeWeekService
 * @Description: 考勤班次里的具体时间服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/3 15:09
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CheckWorkTimeWeekService extends SkyeyeBusinessService<CheckWorkTimeWeek> {

    void saveCheckWorkTimeWeekList(String timeId, List<CheckWorkTimeWeek> beans, String userId);

    void deleteByTimeId(String timeId);

    List<CheckWorkTimeWeek> selectByTimeId(String timeId);

    Map<String, List<CheckWorkTimeWeek>> selectByTimeId(List<String> timeIds);

}
