/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.assets.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.assets.entity.AssetReport;

import java.util.List;

/**
 * @ClassName: AssetReportService
 * @Description: 资产明细服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/9 14:49
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetReportService extends SkyeyeBusinessService<AssetReport> {

    /**
     * 设置资产领用信息
     *
     * @param id        资产明细id
     * @param useId     领用单id
     * @param useUserId 领用人id
     */
    void setAssetReportEmployee(String id, String useId, String useUserId);

    /**
     * 设置资产归还信息
     *
     * @param id           资产明细id
     * @param revertId     归还单id
     * @param revertUserId 归还人id
     */
    void setAssetReportRevert(String id, String revertId, String revertUserId);

    /**
     * 根据资产id获取资产明细总数
     *
     * @param assetId 资产id
     * @return 资产明细总数
     */
    Long getAssetReportNumByAssetId(String assetId);

    /**
     * 根据编码获取未入库的编码信息
     *
     * @param codeNumList 编码
     * @param depotState  是否入库  true:已入库  false:未入库
     * @return
     */
    List<AssetReport> queryAssetReportListByCodeNum(List<String> codeNumList, Boolean depotState);


    void insertAssetReport(InputObject inputObject, OutputObject outputObject);

    void queryAssetReportCodeList(InputObject inputObject, OutputObject outputObject);

    void updateAssetReportById(InputObject inputObject, OutputObject outputObject);
}
