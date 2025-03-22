/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.controller;

import com.skyeye.afterseal.entity.SealFault;
import com.skyeye.afterseal.service.SealFaultService;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SealFaultController
 * @Description: 售后服务故障信息控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/10 13:14
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "售后服务故障信息", tags = "售后服务故障信息", modelName = "售后工单")
public class SealFaultController {

    @Autowired
    private SealFaultService sealFaultService;

    @ApiOperation(id = "querySealFaultList", value = "获取故障信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SealFaultController/querySealFaultList")
    public void querySealFaultList(InputObject inputObject, OutputObject outputObject) {
        sealFaultService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeSealFault", value = "新增/编辑故障信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SealFault.class)
    @RequestMapping("/post/SealFaultController/writeSealFault")
    public void writeSealFault(InputObject inputObject, OutputObject outputObject) {
        sealFaultService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSealFaultById", value = "删除故障信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SealFaultController/deleteSealFaultById")
    public void deleteSealFaultById(InputObject inputObject, OutputObject outputObject) {
        sealFaultService.deleteById(inputObject, outputObject);
    }

}
