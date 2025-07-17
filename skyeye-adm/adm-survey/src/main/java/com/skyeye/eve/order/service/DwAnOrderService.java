package com.skyeye.eve.order.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.order.entity.DwAnOrder;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamAnOrderService
 * @Description: 答卷 排序题接口层
 * @author: skyeye云系列--lyj
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DwAnOrderService extends SkyeyeBusinessService<DwAnOrder> {

    void queryDwAnOrderById(InputObject inputObject, OutputObject outputObject);

    List<DwAnOrder> selectBySurveyId(String surveyId);

    List<DwAnOrder> selectAnOrderByQuId(String id);

    Map<String, List<DwAnOrder>> selectByQuIdAndStuId(List<String> orderQuIds, String studentId, String id);
}
