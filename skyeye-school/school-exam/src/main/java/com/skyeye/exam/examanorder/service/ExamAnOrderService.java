package com.skyeye.exam.examanorder.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanorder.entity.ExamAnOrder;

/**
 * @ClassName: ExamAnOrderService
 * @Description: 答卷 评分题接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ExamAnOrderService extends SkyeyeBusinessService<ExamAnOrder> {
    void queryExamAnOrderById(InputObject inputObject, OutputObject outputObject);
}
