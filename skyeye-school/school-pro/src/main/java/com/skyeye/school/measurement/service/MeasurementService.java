package com.skyeye.school.measurement.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.measurement.entity.Measurement;

import java.util.List;
import java.util.Map;


/**
 * @ClassName: MeasurementService
 * @Description: 测试管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 10:45
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MeasurementService extends SkyeyeBusinessService<Measurement> {

    void queryMeasurementListBySubjectClassesId(InputObject inputObject, OutputObject outputObject);

    Map<String, Double> queryTestByChapterId(Long classNum,String... id);

    Long queryClassMeasurementNum(String id, String stuId, String chapterId);

    List<String> queryMeasurementIdsBySubjectClassId(String id);

    Long queryStuMeasurementNum(String id, String stuId, String chapterId);
}
