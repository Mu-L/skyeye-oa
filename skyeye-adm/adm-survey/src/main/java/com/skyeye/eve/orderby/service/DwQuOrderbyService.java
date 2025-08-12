package com.skyeye.eve.orderby.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.orderby.entity.DwQuOrderby;
import com.skyeye.eve.question.entity.DwQuestion;

import java.util.List;
import java.util.Map;

public interface DwQuOrderbyService extends SkyeyeBusinessService<DwQuOrderby> {

    void saveList(List<DwQuOrderby> orderby, String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    void removeByQuId(String quId);

    List<DwQuOrderby> selectQuOrderby(String copyFromId);

    Map<String, List<DwQuOrderby>> selectByBelongId(List<String> id);

    List<DwQuOrderby> createOrderbys(List<DwQuestion> dwQuestionList, String userId);

    void updateOrderbys(List<DwQuestion> dwQuestionList, String userId);

    void removeByQuIds(List<String> dwQuestionIds);
}
