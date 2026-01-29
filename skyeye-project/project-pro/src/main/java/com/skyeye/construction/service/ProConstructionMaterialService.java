package com.skyeye.construction.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.construction.entity.ProConstructionMaterial;

import java.util.List;

/**
 * @ClassName: ProConstructionMaterialService
 * @Description: 施工材料清单Service层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProConstructionMaterialService extends SkyeyeBusinessService<ProConstructionMaterial> {

    /**
     * 根据施工方案ID查询材料清单列表
     */
    List<ProConstructionMaterial> queryListByParentId(String parentId);

    /**
     * 根据施工方案ID删除所有关联的材料清单
     */
    void deleteByParentId(String parentId);

    /**
     * 保存施工材料清单（先删除后新增）
     */
    void saveConstructionMaterials(String constructionId, List<ProConstructionMaterial> materialList, String userId);

}