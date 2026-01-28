/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.enterprise.entity.EnterpriseProduct;
import com.skyeye.enterprise.entity.EnterpriseProductNorms;

import java.util.List;

/**
 * @ClassName: EnterpriseProductNormsService
 * @Description: 企业商品规格参数服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/21
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface EnterpriseProductNormsService extends SkyeyeBusinessService<EnterpriseProductNorms> {

    void saveEnterpriseProductNorms(String userId, EnterpriseProduct enterpriseProduct);

    /**
     * 根据企业商品id删除规格信息
     *
     * @param enterpriseProductId 企业商品id
     */
    void deleteEnterpriseProductNormsByEnterpriseProductId(String enterpriseProductId);

    /**
     * 根据企业商品id获取规格信息
     *
     * @param enterpriseProductId 企业商品id
     * @return
     */
    List<EnterpriseProductNorms> queryNormsListByEnterpriseProductId(String enterpriseProductId);

}