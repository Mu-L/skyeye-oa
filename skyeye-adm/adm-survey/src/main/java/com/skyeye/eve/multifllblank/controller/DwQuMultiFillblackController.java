package com.skyeye.eve.multifllblank.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.multifllblank.service.DwQuMultiFillblankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "多行填空题选项管理", tags = "多行填空题选项管理", modelName = "多行填空题选项管理")
public class DwQuMultiFillblackController {

    @Autowired
    private DwQuMultiFillblankService dwQuMultiFllblankService;

    /**
     * 分页获取多行填空题选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwQuMultiFllblackList", value = "分页获取多行填空题选项表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwQuMultiFllblackController/queryDwQuMultiFllblackList")
    public void queryDwQuMultiFllblackList(InputObject inputObject, OutputObject outputObject) {
        dwQuMultiFllblankService.queryPageList(inputObject, outputObject);
    }

    /**
     * 根据ID物理删除多行填空题选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwQuMultiFllblackById", value = "根据ID物理删除多行填空题选项表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwQuMultiFllblackController/deleteDwQuMultiFllblackById")
    public void deleteDwQuMultiFllblackById(InputObject inputObject, OutputObject outputObject) {
        dwQuMultiFllblankService.deleteById(inputObject, outputObject);
    }

    /**
     * 逻辑删除多行填空题选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "changeVisibility", value = "逻辑删除多行填空题选项表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwQuMultiFllblackController/changeVisibility")
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        dwQuMultiFllblankService.changeVisibility(inputObject, outputObject);
    }
}
