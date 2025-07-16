/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.checkbox.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.checkbox.entity.DwAnCheckbox;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DwAnCheckboxService
 * @Description: 答卷多选题保存接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
public interface DwAnCheckboxService extends SkyeyeBusinessService<DwAnCheckbox> {

    List<DwAnCheckbox> selectAnCheckBoxByQuId(String id);

    void queryDwAnCheckboxListById(InputObject inputObject, OutputObject outputObject);

    List<DwAnCheckbox> slectBySurveyId(String surveyId);


    void deleteBySurveyId(String surveyId);

    Map<String, List<DwAnCheckbox>> selectByQuId(List<String> id);

    Map<String, List<DwAnCheckbox>> selectByQuIdAndStuId(List<String> cheankboxIds, String studentId);
}


