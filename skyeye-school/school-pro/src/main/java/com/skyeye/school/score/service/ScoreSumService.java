/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.score.entity.ScorePart;
import com.skyeye.school.score.entity.ScoreSum;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ScoreSumService
 * @Description: 总成绩管理服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/29 10:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ScoreSumService extends SkyeyeBusinessService<ScoreSum> {
    List<ScoreSum> queryByObjectIdList(List<String> scoreTypeIdList);

    void updateScoreByObjectIdAndStuNo(String objectId, double sumScore, String stuNo);

    List<ScoreSum> queryByObjectIdListAndStuNo(List<String> objectIdList, String stuNo);

    void deleteByObjectId(String objectId);

    void updateProportionByObjectId(String objectId, String proportion);

    Map<String, String> getStuNoScoreSumMap(Map<String, List<ScoreSum>> collect);

    void deleteByObjectIdList(List<String> objectIdList);
}
