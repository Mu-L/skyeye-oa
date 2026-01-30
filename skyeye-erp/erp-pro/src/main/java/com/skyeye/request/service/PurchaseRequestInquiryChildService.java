/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.request.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.request.entity.PurchaseRequestInquiryChild;

import java.util.List;

/**
 * @ClassName: PurchaseRequestInquiryChildService
 * @Description: 采购申请询价明细服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 11:32
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PurchaseRequestInquiryChildService extends SkyeyeBusinessService<PurchaseRequestInquiryChild> {

    void saveList(String parentId, List<PurchaseRequestInquiryChild> beans);

    void deleteByParentId(String parentId, String quoteSource);

    List<PurchaseRequestInquiryChild> selectByParentId(String parentId);

    void queryPurchaseRequestInquiryChildList(InputObject inputObject, OutputObject outputObject);

    void queryEnterpriseQuoteByItemAndNorms(InputObject inputObject, OutputObject outputObject);
}
