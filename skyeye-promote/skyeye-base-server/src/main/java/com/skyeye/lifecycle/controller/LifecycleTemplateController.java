/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.lifecycle.entity.LifecycleTemplate;
import com.skyeye.lifecycle.service.LifecycleTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LifecycleTemplateController
 * @Description: 生命周期模板控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/4 11:29
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "生命周期模板管理", tags = "生命周期模板管理", modelName = "生命周期管理")
public class LifecycleTemplateController {

    @Autowired
    private LifecycleTemplateService lifecycleTemplateService;

    @ApiOperation(id = "queryLifecycleTemplateList", value = "查询生命周期模板列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/LifecycleTemplateController/queryLifecycleTemplateList")
    public void queryLifecycleTemplateList(InputObject inputObject, OutputObject outputObject) {
        lifecycleTemplateService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeLifecycleTemplate", value = "新增/编辑生命周期模板", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = LifecycleTemplate.class)
    @RequestMapping("/post/LifecycleTemplateController/writeLifecycleTemplate")
    public void writeLifecycleTemplate(InputObject inputObject, OutputObject outputObject) {
        lifecycleTemplateService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLifecycleTemplateById", value = "根据id查询生命周期模板详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "生命周期模板id", required = "required")})
    @RequestMapping("/post/LifecycleTemplateController/queryLifecycleTemplateById")
    public void queryLifecycleTemplateById(InputObject inputObject, OutputObject outputObject) {
        lifecycleTemplateService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLifecycleTemplateByIds", value = "根据id批量查询生命周期模板详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "生命周期模板id，多个用逗号隔开", required = "required")})
    @RequestMapping("/post/LifecycleTemplateController/queryLifecycleTemplateByIds")
    public void queryLifecycleTemplateByIds(InputObject inputObject, OutputObject outputObject) {
        lifecycleTemplateService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCurrentLifecycleTemplateByMasterId", value = "根据主表id查询当前发布得最新得生命周期模板详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "masterId", name = "masterId", value = "主表id", required = "required")})
    @RequestMapping("/post/LifecycleTemplateController/queryCurrentLifecycleTemplateByMasterId")
    public void queryCurrentLifecycleTemplateByMasterId(InputObject inputObject, OutputObject outputObject) {
        lifecycleTemplateService.queryCurrentLifecycleTemplateByMasterId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCurrentLifecycleTemplateByAppIdAndClassName", value = "根据AppId和className查询当前发布得最新得生命周期模板详情", method = "GET", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "appId", name = "appId", value = "应用得appId", required = "required"),
        @ApiImplicitParam(id = "className", name = "className", value = "服务类的className", required = "required")})
    @RequestMapping("/post/LifecycleTemplateController/queryCurrentLifecycleTemplateByAppIdAndClassName")
    public void queryCurrentLifecycleTemplateByAppIdAndClassName(InputObject inputObject, OutputObject outputObject) {
        lifecycleTemplateService.queryCurrentLifecycleTemplateByAppIdAndClassName(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteLifecycleTemplateById", value = "根据ID删除生命周期模板信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "生命周期模板id", required = "required")})
    @RequestMapping("/post/LifecycleTemplateController/deleteLifecycleTemplateById")
    public void deleteLifecycleTemplateById(InputObject inputObject, OutputObject outputObject) {
        lifecycleTemplateService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "publishLifecycleTemplateVersionById", value = "根据id发布生命周期模板", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "生命周期模板id", required = "required")})
    @RequestMapping("/post/LifecycleTemplateController/publishLifecycleTemplateVersionById")
    public void publishLifecycleTemplateVersionById(InputObject inputObject, OutputObject outputObject) {
        lifecycleTemplateService.publishVersionById(inputObject, outputObject);
    }

}
