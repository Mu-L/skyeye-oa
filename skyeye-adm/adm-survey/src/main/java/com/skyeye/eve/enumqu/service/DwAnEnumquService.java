package com.skyeye.eve.enumqu.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.enumqu.entity.DwAnEnumqu;

import java.util.List;

/**
 * @ClassName: DwAnEnumquService
 * @Description: 答卷 枚举题答案实体类
 * @author: skyeye云系列--lyj
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DwAnEnumquService extends SkyeyeBusinessService<DwAnEnumqu> {

    void queryDwAnEnumquListById(InputObject inputObject, OutputObject outputObject);

    List<DwAnEnumqu> selectBySurveyId(String surveyId);

    List<DwAnEnumqu> selectAnEnumByQuId(String id);
}
