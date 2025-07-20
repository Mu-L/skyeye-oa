package com.skyeye.piecework.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.piecework.service.PieceworkSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "计件数量或工时统计信息", tags = "计件数量或工时统计信息", modelName = "计件数量或工时统计信息")
public class PieceworkSystemController {

    @Autowired
    private PieceworkSystemService pieceworkSystemService;

    /**
     * 新增计件数量或工时统计信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writePieceworkSystem", value = "新增计件数量或工时统计信息", method = "POST", allUse = "2")
    @RequestMapping("/post/PieceworkSystemController/writePieceworkSystem")
    public void writePieceworkSystem(InputObject inputObject, OutputObject outputObject) {
        pieceworkSystemService.writePieceworkSystem(inputObject, outputObject);
    }


    /**
     * 查询计件数量或工时统计信息根据Id
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPieceworkSystemById", value = "查询计件数量或工时统计信息根据Id", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PieceworkSystemController/queryPieceworkSystemById")
    public void queryPieceworkSystemById(InputObject inputObject, OutputObject outputObject) {
        pieceworkSystemService.selectById(inputObject, outputObject);
    }

    /**
     * 查询当前账号员工计件数量或工时统计信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPieceworkSystemByUserId", value = "查询当前账号员工计件数量或工时统计信息", method = "POST", allUse = "2")
    @RequestMapping("/post/PieceworkSystemController/queryPieceworkSystemByUserId")
    public void queryPieceworkSystemByUserId(InputObject inputObject, OutputObject outputObject) {
        pieceworkSystemService.queryPieceworkSystemByUserId(inputObject, outputObject);
    }

    /**
     * 删除员工计件数量或工时统计信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deletePieceworkSystemById", value = "删除员工计件数量或工时统计信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PieceworkSystemController/deletePieceworkSystemById")
    public void deletePieceworkSystemById(InputObject inputObject, OutputObject outputObject) {
        pieceworkSystemService.deleteById(inputObject, outputObject);
    }
}
