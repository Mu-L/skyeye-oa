package com.skyeye.eve.yesno.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.yesno.entity.DwAnYesno;

import java.util.List;

public interface DwAnYesnoService extends SkyeyeBusinessService<DwAnYesno> {
    void queryDwAnYesnoListById(InputObject inputObject, OutputObject outputObject);

    List<DwAnYesno> selectBySurveyId(String surveyId);
}
