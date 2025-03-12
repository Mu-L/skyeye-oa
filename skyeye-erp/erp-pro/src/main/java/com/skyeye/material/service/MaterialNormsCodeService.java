/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.material.entity.MaterialNormsCode;

import java.util.List;

/**
 * @ClassName: MaterialNormsCodeService
 * @Description: 商品条形码服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/21 8:25
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MaterialNormsCodeService extends SkyeyeBusinessService<MaterialNormsCode> {

    void insertMaterialNormsCode(InputObject inputObject, OutputObject outputObject);

    /**
     * 根据编码获取未入库的编码信息
     *
     * @param depotId     仓库id
     * @param codeNumList 编码
     * @param indepot     库存状态
     * @return
     */
    List<MaterialNormsCode> queryMaterialNormsCodeByCodeNum(String depotId, List<String> codeNumList, Integer... indepot);

    /**
     * 获取仓库中指定商品规格的编码信息
     *
     * @param depotId 仓库id
     * @param normsId 商品id
     * @param type    类型
     * @param indepot 库存状态
     * @return
     */
    List<MaterialNormsCode> queryMaterialNormsCode(String depotId, String normsId, Integer type, Integer indepot);

    void queryNormsBarCodeList(InputObject inputObject, OutputObject outputObject);

    /**
     * 修改条形码物料状态信息
     *
     * @param materialNormsCodeList
     */
    void updateEntityPick(List<MaterialNormsCode> materialNormsCodeList);

    void queryNormsStockDetailList(InputObject inputObject, OutputObject outputObject);

    void queryStoreNormsStockDetailList(InputObject inputObject, OutputObject outputObject);

    void queryMaterialNormsCode(InputObject inputObject, OutputObject outputObject);

    void editStoreMaterialNormsCodeUseState(InputObject inputObject, OutputObject outputObject);
}
