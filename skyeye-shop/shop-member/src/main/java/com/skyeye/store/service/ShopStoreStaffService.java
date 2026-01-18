/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.store.entity.ShopArea;
import com.skyeye.store.entity.ShopStoreStaff;

import java.util.List;

/**
 * @ClassName: ShopStoreStaffService
 * @Description: 门店与员工的关系服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 21:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ShopStoreStaffService extends SkyeyeBusinessService<ShopStoreStaff> {

    void insertStoreStaffMation(InputObject inputObject, OutputObject outputObject);

    void queryStaffBelongAreaList(InputObject inputObject, OutputObject outputObject);

    /**
     * 根据员工id查询员工所在的区域信息
     *
     * @param staffId 员工id
     */
    List<ShopArea> queryStaffBelongAreaListByStaffId(String staffId);

    List<ShopStoreStaff> getShopStoresByStoreId(String storeId);

    void queryStaffBelongStoreList(InputObject inputObject, OutputObject outputObject);

    void deleteStoreStaffMationByStaffId(InputObject inputObject, OutputObject outputObject);

    /**
     * 执行员工调拨：将员工从一个门店调拨到另一个门店
     *
     * @param staffId     员工ID
     * @param fromStoreId 原门店ID
     * @param toStoreId   目标门店ID
     */
    void executeStaffTransfer(String staffId, String fromStoreId, String toStoreId);
}
