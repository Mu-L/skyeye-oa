/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.contract.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.contract.entity.CrmContract;

import java.util.List;

/**
 * @ClassName: CrmContractService
 * @Description: 客户合同管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/25 22:17
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CrmContractService extends SkyeyeBusinessService<CrmContract> {

    void queryCrmContractListByObjectId(InputObject inputObject, OutputObject outputObject);

    void performCrmContract(InputObject inputObject, OutputObject outputObject);

    void closeCrmContract(InputObject inputObject, OutputObject outputObject);

    void shelveCrmContract(InputObject inputObject, OutputObject outputObject);

    void recoveryCrmContract(InputObject inputObject, OutputObject outputObject);

    /**
     * 根据所属第三方业务数据id查询合同信息
     *
     * @param objectId 所属第三方业务数据id
     * @return
     */
    List<CrmContract> queryCrmContractListByObjectId(String objectId);

    /**
     * 修改已回款金额
     *
     * @param id
     * @param paymentPrice
     */
    void updatePaymentPrice(String id, String paymentPrice);

    /**
     * 修改已开票金额
     *
     * @param id
     * @param invoicePrice
     */
    void updateInvoicePrice(String id, String invoicePrice);

    void editChildState(InputObject inputObject, OutputObject outputObject);

}
