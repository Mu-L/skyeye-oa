package com.skyeye.construction.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.construction.dao.ProConstructionStepDao;
import com.skyeye.construction.entity.ProConstructionStep;
import com.skyeye.construction.service.ProConstructionStepService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: ProConstructionStepServiceImpl
 * @Description: 施工步骤Service实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "施工步骤", groupName = "项目施工管理", manageShow = false)
public class ProConstructionStepServiceImpl extends SkyeyeBusinessServiceImpl<ProConstructionStepDao, ProConstructionStep> implements ProConstructionStepService {

    /**
     * 根据施工方案ID查询施工步骤列表
     */
    public List<ProConstructionStep> queryListByParentId(String parentId) {
        QueryWrapper<ProConstructionStep> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProConstructionStep::getConstructionId), parentId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ProConstructionStep::getStepOrder));
        return list(queryWrapper);
    }

    /**
     * 根据施工方案ID删除所有关联的施工步骤
     */
    public void deleteByParentId(String parentId) {
        QueryWrapper<ProConstructionStep> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProConstructionStep::getConstructionId), parentId);
        remove(queryWrapper);
    }

    /**
     * 保存施工步骤（先删除后新增）
     */
    @Override
    public void saveConstructionSteps(String constructionId, List<ProConstructionStep> stepList, String userId) {
        // 先删除该施工方案下的所有步骤，再重新插入
        deleteByParentId(constructionId);

        if (CollectionUtil.isEmpty(stepList)) {
            return;
        }

        // 保存新的施工步骤
        for (ProConstructionStep step : stepList) {
            step.setConstructionId(constructionId);
        }
        createEntity(stepList, userId);
    }

}