package com.skyeye.evaluation.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.evaluation.entity.ProEvaluation;
import com.skyeye.evaluation.entity.ProEvaluationDetail;

import java.util.List;

/**
 * @ClassName: ProEvaluationDetailService
 * @Description: 项目评估明细Service层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProEvaluationDetailService extends SkyeyeBusinessService<ProEvaluationDetail> {

    /**
     * 保存评估明细
     *
     * @param userId     用户ID
     * @param evaluation 评估实体
     */
    void saveEvaluationDetail(String userId, ProEvaluation evaluation);

    /**
     * 删除评估明细
     *
     * @param evaluationId 评估ID
     */
    void deleteEvaluationDetail(String evaluationId);

    /**
     * 根据评估ID查询评估明细列表
     *
     * @param evaluationId 评估ID
     * @return 评估明细列表
     */
    List<ProEvaluationDetail> queryDetailListByEvaluationId(String evaluationId);

}