package com.skyeye.school.measurement.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.measurement.entity.MeasurementSub;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MeasurementSubService
 * @Description: 测试提交服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 10:45
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MeasurementSubService extends SkyeyeBusinessService<MeasurementSub> {

    void queryMeasurementSubListByMeasurementId(InputObject inputObject, OutputObject outputObject);

    void queryMeasurementNotSubListByMeasurementId(InputObject inputObject, OutputObject outputObject);

    void readOverMeasurementSubById(InputObject inputObject, OutputObject outputObject);

    Map<String, Long> querySubResult(String... testId);

    Map<String, Long> querySubCorrectResult(String... testId);

    Map<String, String> querySubResult(String userId, String... testId);

    void queryMeasurementStuSubListByMeasurementId(InputObject inputObject, OutputObject outputObject);

    double queryMeasurementFinshRate(List<String> ids, Long classNum);

    Long queryClassMeasurementJoinNum(String id);
}
