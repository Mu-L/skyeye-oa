package com.skyeye.eve.servey.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.servey.entity.DwSurveyDirectory;

import java.util.List;
import java.util.Map;

public interface DwSurveyDirectoryService extends SkyeyeBusinessService<DwSurveyDirectory> {
    void setUpDwDirectory(InputObject inputObject, OutputObject outputObject);

    void takeExam(InputObject inputObject, OutputObject outputObject);

    void copyDwDirectory(InputObject inputObject, OutputObject outputObject);

    void changeWhetherDeleteById(InputObject inputObject, OutputObject outputObject);

    void updateDwMationEndById(InputObject inputObject, OutputObject outputObject);

    void queryFilterDwLists(InputObject inputObject, OutputObject outputObject);

    void queryMyDwurvey(InputObject inputObject, OutputObject outputObject);

    void queryDwurveyMationById(InputObject inputObject, OutputObject outputObject);

    void queryDwSurveyDirectoryMationByIdToHTML(InputObject inputObject, OutputObject outputObject);

    Map<String, DwSurveyDirectory> selectMapBydwSurveyIds(List<String> dwSurveyIds);


    DwSurveyDirectory selectBySurAndStuIds(String surveyId, String createId, String id);
}
