package com.skyeye.depot.controller;


import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.service.DepotOutPutRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DepotOutPutRecordController
 * @Description: 仓库出入库记录管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/5 22:02
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "仓库出入库记录管理", tags = "仓库出入库记录管理", modelName = "仓库出入库记录管理")
public class DepotOutPutRecordController {

    @Autowired
    private DepotOutPutRecordService depotOutPutRecordService;

    @ApiOperation(id = "queryOutPutRecordDetailList", value = "获取借出/归还记录明细", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DepotOutPutRecordController/queryOutPutRecordDetailList")
    public void queryOutPutRecordDetailList(InputObject inputObject, OutputObject outputObject) {
        depotOutPutRecordService.queryOutPutRecordDetailList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryHolderOutPutNormsList", value = "获取(holderKey)供应商/客户的借出出库/归还入库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DepotOutPutRecordController/queryHolderOutPutNormsList")
    public void queryHolderOutPutNormsList(InputObject inputObject, OutputObject outputObject) {
        depotOutPutRecordService.queryHolderOutPutNormsList(inputObject, outputObject);
    }
}
