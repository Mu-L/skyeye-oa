package com.skyeye.eve.orderby.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.orderby.service.DwQuOrderbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "排序题行选项管理", tags = "排序题行选项管理", modelName = "排序题行选项管理")
public class DwQuOrderbyController {

    @Autowired
    private DwQuOrderbyService dwQuOrderbyService;

    /**
     * 分页获取排序题行选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwQuOrderbyList", value = "分页获取排序题行选项表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwQuOrderbyController/queryDwQuOrderbyList")
    public void queryDwQuOrderbyList(InputObject inputObject, OutputObject outputObject) {
        dwQuOrderbyService.queryPageList(inputObject, outputObject);
    }

    /**
     * 根据ID物理删除排序题行选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwQuOrderbyById", value = "根据ID物理删除排序题行选项表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwQuOrderbyController/deleteDwQuOrderbyById")
    public void deleteDwQuOrderbyById(InputObject inputObject, OutputObject outputObject) {
        dwQuOrderbyService.deleteById(inputObject, outputObject);
    }

    /**
     * 逻辑删除排序题行选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "changeVisibility", value = "逻辑删除排序题行选项表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwQuOrderbyController/changeVisibility")
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        dwQuOrderbyService.changeVisibility(inputObject, outputObject);
    }

}
