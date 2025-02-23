package com.skyeye.eve.question.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.question.entity.DwQuestionBank;

public interface DwQuestionBankService extends SkyeyeBusinessService<DwQuestionBank> {
    
    void queryDwQuestionBankList(InputObject inputObject, OutputObject outputObject);

    void queryMyDwQuestionBankList(InputObject inputObject, OutputObject outputObject);
}
