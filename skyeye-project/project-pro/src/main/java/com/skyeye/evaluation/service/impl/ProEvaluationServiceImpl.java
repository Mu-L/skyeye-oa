package com.skyeye.evaluation.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.evaluation.classenum.EvaluationItemTypeEnum;
import com.skyeye.evaluation.dao.ProEvaluationDao;
import com.skyeye.evaluation.entity.ProEvaluation;
import com.skyeye.evaluation.service.ProEvaluationDetailService;
import com.skyeye.evaluation.service.ProEvaluationService;
import com.skyeye.exception.CustomException;
import com.skyeye.scheme.service.ProSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ProEvaluationServiceImpl
 * @Description: 项目评估Service实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "项目评估", groupName = "项目评估管理", flowable = true)
public class ProEvaluationServiceImpl extends SkyeyeBusinessServiceImpl<ProEvaluationDao, ProEvaluation> implements ProEvaluationService {

    @Autowired
    private ProEvaluationDetailService proEvaluationDetailService;

    @Autowired
    private ProSchemeService proSchemeService;

    @Override
    protected QueryWrapper<ProEvaluation> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ProEvaluation> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("projectId"))) {
            // 根据项目ID过滤
            queryWrapper.eq(MybatisPlusUtil.toColumns(ProEvaluation::getProjectId), commonPageInfo.getCustomParamsMapStr("projectId"));
        }
        return queryWrapper;
    }

    @Override
    public void validatorEntity(ProEvaluation entity) {
        // 验证评估名称唯一性
        QueryWrapper<ProEvaluation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProEvaluation::getName), entity.getName());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProEvaluation::getProjectId), entity.getProjectId());
        if (StrUtil.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        ProEvaluation checkEvaluation = getOne(queryWrapper, false);
        if (checkEvaluation != null) {
            throw new CustomException("评估名称已存在");
        }
    }

    @Override
    public void writePostpose(ProEvaluation entity, String userId) {
        // 保存评估明细
        proEvaluationDetailService.saveEvaluationDetail(userId, entity);
        super.writePostpose(entity, userId);
    }

    @Override
    public ProEvaluation getDataFromDb(String id) {
        ProEvaluation evaluation = super.getDataFromDb(id);
        if (evaluation != null) {
            // 设置评估明细
            evaluation.setEvaluationDetailList(proEvaluationDetailService.queryDetailListByEvaluationId(id));
        }
        return evaluation;
    }

    @Override
    public ProEvaluation selectById(String id) {
        ProEvaluation proEvaluation = super.selectById(id);
        proSchemeService.setDataMation(proEvaluation, ProEvaluation::getSchemeId);
        if (CollectionUtil.isNotEmpty(proEvaluation.getEvaluationDetailList())) {
            proEvaluation.getEvaluationDetailList().forEach(proEvaluationDetail -> {
                proEvaluationDetail.setEvaluationTypeMation(EvaluationItemTypeEnum.getMation(proEvaluationDetail.getEvaluationType()));
            });
        }
        return proEvaluation;
    }

    @Override
    public void deletePostpose(String id) {
        // 删除评估明细
        proEvaluationDetailService.deleteEvaluationDetail(id);
    }

}