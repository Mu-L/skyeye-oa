package com.skyeye.eve.question.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.question.entity.DwQuestion;

import java.util.List;

public interface DwQuestionService extends SkyeyeBusinessService<DwQuestion> {
    void queryDwQuestionList(InputObject inputObject, OutputObject outputObject);

    List<DwQuestion> QueryQuestionByBelongId(String belongId);

    void copyQuestionListMation(DwQuestion question);

    void queryMyDwQuestionList(InputObject inputObject, OutputObject outputObject);

    void queryPageDwQuestionList(InputObject inputObject, OutputObject outputObject);

}
