package com.skyeye.construction.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.construction.entity.ProConstructionStep;

import java.util.List;

/**
 * @ClassName: ProConstructionStepService
 * @Description: 施工步骤Service层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProConstructionStepService extends SkyeyeBusinessService<ProConstructionStep> {

    /**
     * 根据施工方案ID查询施工步骤列表
     */
    List<ProConstructionStep> queryListByParentId(String parentId);

    /**
     * 根据施工方案ID删除所有关联的施工步骤
     */
    void deleteByParentId(String parentId);

    /**
     * 保存施工步骤（先删除后新增）
     */
    void saveConstructionSteps(String constructionId, List<ProConstructionStep> stepList, String userId);

}