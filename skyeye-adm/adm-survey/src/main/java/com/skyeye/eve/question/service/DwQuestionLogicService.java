package com.skyeye.eve.question.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.question.entity.DwQuestionLogic;

import java.util.List;
import java.util.Map;

public interface DwQuestionLogicService extends SkyeyeBusinessService<DwQuestionLogic> {
    List<DwQuestionLogic> setLogics(String quId, List<DwQuestionLogic> questionLogic, String userId);

    Map<String, List<DwQuestionLogic>> selectByQuestionIds(List<String> questionIds);

    void queryDwQuestionLogicList(InputObject inputObject, OutputObject outputObject);

    void queryMyDwQuestionLogicList(InputObject inputObject, OutputObject outputObject);

    void createLogics(List<DwQuestion> dwQuestionList, String userId);

    List<DwQuestionLogic> selectByDwQuestionIdList(List<String> dwQuestionIdList);

    DwQuestionLogic selectLogicByIdByQuId(String id);

    void removeByQuIds(List<String> ids);

    void deleteByCkQuId(List<String> ids);

}
