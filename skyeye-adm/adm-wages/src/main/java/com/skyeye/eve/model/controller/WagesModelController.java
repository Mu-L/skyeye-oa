/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.model.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.model.entity.WagesModel;
import com.skyeye.eve.model.service.WagesModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: WagesModelController
 * @Description: 薪资模板控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/21 11:19
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "薪资模板", tags = "薪资模板", modelName = "薪资模板")
public class WagesModelController {

    @Autowired
    private WagesModelService wagesModelService;

    /**
     * 获取薪资模板列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "wagesmodel001", value = "获取薪资模板列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/WagesModelController/queryWagesModelList")
    public void queryWagesModelList(InputObject inputObject, OutputObject outputObject) {
        wagesModelService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑薪资模板信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeWagesModel", value = "新增/编辑薪资模板信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = WagesModel.class)
    @RequestMapping("/post/WagesModelController/writeWagesModel")
    public void writeWagesModel(InputObject inputObject, OutputObject outputObject) {
        wagesModelService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除薪资模板信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteWagesModelById", value = "删除薪资模板信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/WagesModelController/deleteWagesModelById")
    public void deleteWagesModelById(InputObject inputObject, OutputObject outputObject) {
        wagesModelService.deleteById(inputObject, outputObject);
    }

}
