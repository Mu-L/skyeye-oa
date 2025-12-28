/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: ErpCommonService
 * @Description: ERP公共服务
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 21:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ErpCommonService {

    void queryDepotHeadDetailsMationById(InputObject inputObject, OutputObject outputObject);

    void deleteErpOrderById(InputObject inputObject, OutputObject outputObject);

    /**
     * 修改商品规格库存
     *
     * @param depotId    仓库id
     * @param materialId 商品id
     * @param normsId    规格id
     * @param operNumber 变化数量
     * @param type       出入库类型，参考#DepotPutOutType
     */
    void editMaterialNormsDepotStock(String depotId, String materialId, String normsId, String operNumber, int type);

    void editDepotHeadToRevoke(InputObject inputObject, OutputObject outputObject);

    void orderSubmitToApproval(InputObject inputObject, OutputObject outputObject);

}
