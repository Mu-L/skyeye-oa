package com.skyeye.eve.radio.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.radio.entity.DwQuRadio;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DwQuRadioService
 * @Description: 单选题保存接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
public interface DwQuRadioService extends SkyeyeBusinessService<DwQuRadio> {
    void saveList(List<DwQuRadio> list, String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    void removeByQuId(String quId);

    List<DwQuRadio> selectQuRadio(String copyFromId);

    Map<String, List<DwQuRadio>> selectByBelongId(List<String> id);

    void updateRadios(List<DwQuestion> dwQuestionList, String userId);

    void removeByQuIds(List<String> dwQuestionIds);

    void createRadios(List<DwQuestion> dwQuestionList, String userId);
}
