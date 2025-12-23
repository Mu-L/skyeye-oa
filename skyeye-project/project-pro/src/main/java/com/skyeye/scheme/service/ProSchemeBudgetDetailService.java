/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.scheme.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.scheme.entity.ProSchemeBudgetDetail;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ProSchemeBudgetDetailService
 * @Description: 项目方案预算明细服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProSchemeBudgetDetailService extends SkyeyeBusinessService<ProSchemeBudgetDetail> {

    void saveList(String schemeId, List<ProSchemeBudgetDetail> beans);

    /**
     * 根据方案id查询预算明细列表
     *
     * @param schemeId 方案id
     * @return 预算明细列表
     */
    List<ProSchemeBudgetDetail> queryBudgetDetailBySchemeId(String schemeId);

    /**
     * 根据方案id批量查询预算明细列表
     *
     * @param schemeIds 方案id列表
     * @return 预算明细列表（按方案id分组）
     */
    Map<String, List<ProSchemeBudgetDetail>> queryBudgetDetailBySchemeIds(List<String> schemeIds);

    /**
     * 根据方案id删除预算明细
     *
     * @param schemeId 方案id
     */
    void deleteBudgetDetailBySchemeId(String schemeId);

}

