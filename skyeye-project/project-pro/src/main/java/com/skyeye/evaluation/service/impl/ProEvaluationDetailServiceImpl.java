package com.skyeye.evaluation.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.evaluation.dao.ProEvaluationDetailDao;
import com.skyeye.evaluation.entity.ProEvaluation;
import com.skyeye.evaluation.entity.ProEvaluationDetail;
import com.skyeye.evaluation.service.ProEvaluationDetailService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @ClassName: ProEvaluationDetailServiceImpl
 * @Description: 项目评估明细Service实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "项目评估明细", groupName = "项目评估管理", manageShow = false)
public class ProEvaluationDetailServiceImpl extends SkyeyeBusinessServiceImpl<ProEvaluationDetailDao, ProEvaluationDetail> implements ProEvaluationDetailService {

    @Override
    public void saveEvaluationDetail(String userId, ProEvaluation evaluation) {
        // 先删除该评估下的所有明细，再重新插入
        deleteEvaluationDetail(evaluation.getId());

        if (CollectionUtil.isNotEmpty(evaluation.getEvaluationDetailList())) {
            evaluation.getEvaluationDetailList().forEach(evaluationDetail -> {
                evaluationDetail.setEvaluationId(evaluation.getId());
            });
            // 计算加权得分
            calculateWeightedScores(evaluation.getEvaluationDetailList());

            // 保存新的评估明细
            createEntity(evaluation.getEvaluationDetailList(), userId);
        }
    }

    @Override
    public void deleteEvaluationDetail(String evaluationId) {
        QueryWrapper<ProEvaluationDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProEvaluationDetail::getEvaluationId), evaluationId);
        remove(queryWrapper);
    }

    @Override
    public List<ProEvaluationDetail> queryDetailListByEvaluationId(String evaluationId) {
        QueryWrapper<ProEvaluationDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProEvaluationDetail::getEvaluationId), evaluationId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ProEvaluationDetail::getOrderBy));
        return list(queryWrapper);
    }

    /**
     * 计算加权得分
     *
     * @param detailList 评估明细列表
     */
    private void calculateWeightedScores(List<ProEvaluationDetail> detailList) {
        for (ProEvaluationDetail detail : detailList) {
            BigDecimal weightedScore = detail.getWeight().multiply(BigDecimal.valueOf(detail.getScore()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            detail.setWeightedScore(weightedScore);
        }
    }

}