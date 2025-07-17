package com.skyeye.eve.radio.service;


import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.radio.entity.DwAnRadio;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DwAnRadioService
 * @Description: 答卷单选题保存接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
public interface DwAnRadioService extends SkyeyeBusinessService<DwAnRadio> {

    void queryDwAnRadioListById(InputObject inputObject, OutputObject outputObject);

    List<DwAnRadio> selectRadioBySurveyId(String surveyId);

    List<DwAnRadio> selectRadioByQuId(String id);

    Map<String, List<DwAnRadio>> selectByQuIdAndStuId(List<String> radioIds, String studentId, String id);
}
