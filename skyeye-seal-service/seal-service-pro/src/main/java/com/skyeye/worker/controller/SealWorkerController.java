/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worker.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.worker.entity.SealWorker;
import com.skyeye.worker.service.SealWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SealWorkerController
 * @Description: 工人管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/15 19:01
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "工人管理", tags = "工人管理", modelName = "工人管理")
public class SealWorkerController {

    @Autowired
    private SealWorkerService sealWorkerService;

    @ApiOperation(id = "sealseserviceworker001", value = "获取工人信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SealWorkerController/querySealWorkerList")
    public void querySealWorkerList(InputObject inputObject, OutputObject outputObject) {
        sealWorkerService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeSealWorker", value = "新增/编辑工人资料信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SealWorker.class)
    @RequestMapping("/post/SealWorkerController/writeSealWorker")
    public void writeSealWorker(InputObject inputObject, OutputObject outputObject) {
        sealWorkerService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSealWorkerById", value = "删除工人资料信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SealWorkerController/deleteSealWorkerById")
    public void deleteSealWorkerById(InputObject inputObject, OutputObject outputObject) {
        sealWorkerService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllSealWorkerList", value = "获取所有工人信息", method = "GET", allUse = "2")
    @RequestMapping("/post/SealWorkerController/queryAllSealWorkerList")
    public void queryAllSealWorkerList(InputObject inputObject, OutputObject outputObject) {
        sealWorkerService.queryAllSealWorkerList(inputObject, outputObject);
    }

}
