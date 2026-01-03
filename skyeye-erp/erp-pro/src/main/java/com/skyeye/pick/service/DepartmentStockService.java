/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pick.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.pick.entity.DepartmentStock;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DepartmentStockService
 * @Description: 部门/车间物料库存信息服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/31 16:58
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DepartmentStockService extends SkyeyeBusinessService<DepartmentStock> {

    /**
     * 修改部门/车间库存存量信息
     *
     * @param departmentId 部门id
     * @param farmId       车间id
     * @param materialId   商品id
     * @param normsId      规格id
     * @param operNumber   变化数量
     * @param type         出入库类型， {@link DepotPutOutType}
     * @param stockType    商品规格库存类型 {@link MaterialNormsStockType}
     */
    void updateDepartmentStock(String departmentId, String farmId, String materialId, String normsId, String operNumber, int type, int stockType);

    DepartmentStock queryDepartmentStock(String departmentId, String farmId, String normsId, int stockType);

    Map<String, String> queryNormsDepartmentStock(String departmentId, String farmId, List<String> normsIds);

}
