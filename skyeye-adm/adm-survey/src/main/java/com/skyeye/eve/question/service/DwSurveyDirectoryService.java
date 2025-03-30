package com.skyeye.eve.question.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.question.entity.DwSurveyDirectory;

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
}
