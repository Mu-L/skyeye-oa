/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.contract.service;

import com.skyeye.base.business.service.SkyeyeFlowableService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.contract.entity.SupplierContract;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SupplierContractService
 * @Description: 供应商合同管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/25 22:17
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SupplierContractService extends SkyeyeFlowableService<SupplierContract> {

    void querySupplierContractListByObjectId(InputObject inputObject, OutputObject outputObject);

    void performSupplierContract(InputObject inputObject, OutputObject outputObject);

    void closeSupplierContract(InputObject inputObject, OutputObject outputObject);

    void shelveSupplierContract(InputObject inputObject, OutputObject outputObject);

    void recoverySupplierContract(InputObject inputObject, OutputObject outputObject);

    /**
     * 根据所属第三方业务数据id查询合同信息
     *
     * @param objectId 所属第三方业务数据id
     * @return
     */
    List<SupplierContract> querySupplierContractListByObjectId(String objectId);

    /**
     * 根据单据来源id计算已经签订合同的商品数量
     *
     * @param fromId 单据来源id
     * @return
     */
    Map<String, Integer> calcMaterialNormsNumByFromId(String fromId);

    void querySupplierContractTransById(InputObject inputObject, OutputObject outputObject);

    void supplierContractToOrder(InputObject inputObject, OutputObject outputObject);

    void setContractMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey);

    /**
     * 修改合同下达状态
     *
     * @param id         合同id
     * @param childState 到货状态
     */
    void editChildState(String id, String childState);

    void updatePaymentPrice(String contractId, String price);

    void updateInvoicePrice(String contractId, String invoicePrice);
}
