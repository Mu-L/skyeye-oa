/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.vehicle.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.vehicle.entity.Oiling;
import com.skyeye.eve.vehicle.service.OilingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: OilingController
 * @Description: 车辆加油管理
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/9 10:15
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "车辆加油管理", tags = "车辆加油管理", modelName = "车辆模块")
public class OilingController {

    @Autowired
    private OilingService oilingService;

    /**
     * 遍历车辆加油列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "oiling001", value = "遍历车辆加油列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/OilingController/queryOilingList")
    public void queryOilingList(InputObject inputObject, OutputObject outputObject) {
        oilingService.queryPageList(inputObject, outputObject);
    }

    /**
     * 不分页查询车辆加油列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryNoPageOilingList", value = "不分页查询车辆加油列表", method = "POST", allUse = "1")
    @RequestMapping("/post/OilingController/queryNoPageOilingList")
    public void queryNoPageOilingList(InputObject inputObject, OutputObject outputObject) {
        oilingService.queryNoPageOilingList(inputObject, outputObject);
    }

    /**
     * 新增/修改加油信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeOiling", value = "新增/修改加油信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Oiling.class)
    @RequestMapping("/post/OilingController/writeOiling")
    public void writeOiling(InputObject inputObject, OutputObject outputObject) {
        oilingService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id删除加油信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "oiling003", value = "根据id删除加油信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OilingController/deleteOilingById")
    public void deleteOilingById(InputObject inputObject, OutputObject outputObject) {
        oilingService.deleteById(inputObject, outputObject);
    }

}
