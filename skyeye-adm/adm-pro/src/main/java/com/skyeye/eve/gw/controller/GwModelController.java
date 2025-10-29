/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.gw.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.gw.entity.GwModel;
import com.skyeye.eve.gw.service.GwModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: GwModelController
 * @Description: 公文模版控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/29 9:33
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "公文模版", tags = "公文模版", modelName = "公文模版")
public class GwModelController {

    @Autowired
    private GwModelService gwModelService;

    @ApiOperation(id = "queryGwModelList", value = "查询公文模版列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/GwModelController/queryGwModelList")
    public void queryGwModelList(InputObject inputObject, OutputObject outputObject) {
        gwModelService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeGwModel", value = "新增/修改公文模版信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = GwModel.class)
    @RequestMapping("/post/GwModelController/writeGwModel")
    public void writeGwModel(InputObject inputObject, OutputObject outputObject) {
        gwModelService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteGwModelById", value = "根据id删除公文模版信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/GwModelController/deleteGwModelById")
    public void deleteGwModelById(InputObject inputObject, OutputObject outputObject) {
        gwModelService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledGwModelList", value = "获取所有启用的公文模版列表", method = "GET", allUse = "2")
    @RequestMapping("/post/GwModelController/queryEnabledGwModelList")
    public void queryAllGwModelList(InputObject inputObject, OutputObject outputObject) {
        gwModelService.queryEnabledGwModelList(inputObject, outputObject);
    }

}
