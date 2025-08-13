package com.skyeye.eve.multifllblank.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.multifllblank.entity.DwQuMultiFillblank;
import com.skyeye.eve.question.entity.DwQuestion;

import java.util.List;
import java.util.Map;

public interface DwQuMultiFillblankService extends SkyeyeBusinessService<DwQuMultiFillblank> {
    void saveList(List<DwQuMultiFillblank> list, String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    void removeByQuId(String quId);

    List<DwQuMultiFillblank> selectQuMultiFillblank(String copyFromId);

    Map<String, List<DwQuMultiFillblank>> selectByBelongId(List<String> id);

    void updateMultiFillblanks(List<DwQuestion> dwQuestionList, String userId);

    void removeByQuIds(List<String> dwQuestionIds);

    List<DwQuMultiFillblank> createMultiFillblanks(List<DwQuestion> dwQuestionList, String userId);

    List<DwQuMultiFillblank> selectByQuId(String id);
}
