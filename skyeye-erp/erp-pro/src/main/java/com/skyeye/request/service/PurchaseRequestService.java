/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.request.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.request.entity.PurchaseRequest;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: PurchaseRequestService
 * @Description: 采购申请服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 11:05
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PurchaseRequestService extends SkyeyeBusinessService<PurchaseRequest> {

    /**
     * 采购申请询价
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void inquiryPurchaseRequest(InputObject inputObject, OutputObject outputObject);

    /**
     * 采购申请定价
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void fixedPricePurchaseRequest(InputObject inputObject, OutputObject outputObject);

    /**
     * 采购申请转合同时获取的详情
     *
     * @param inputObject
     * @param outputObject
     */
    void queryPurchaseRequestTransferContract(InputObject inputObject, OutputObject outputObject);

    void purchaseRequestToContract(InputObject inputObject, OutputObject outputObject);

    void setRequestMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey);

    /**
     * 设置采购申请报价信息（包含供应商权限和时间段）
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void setQuoteInfo(InputObject inputObject, OutputObject outputObject);

    /**
     * 获取企业账户允许参与报价的单据信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void queryEnterpriseQuoteRequestList(InputObject inputObject, OutputObject outputObject);

}
