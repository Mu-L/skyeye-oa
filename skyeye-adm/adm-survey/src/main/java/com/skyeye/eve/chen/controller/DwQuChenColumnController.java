package com.skyeye.eve.chen.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.chen.service.DwQuChenColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "矩阵题-列选项管理", tags = "矩阵题-列选项管理", modelName = "矩阵题-列选项管理")
public class DwQuChenColumnController {

    @Autowired
    private DwQuChenColumnService dwQuChenColumnService;

    /**
     * 分页获取矩阵列和行选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwQuChenColumnAndRowList", value = "分页获取矩阵列和行选项表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwQuChenColumnController/queryDwQuChenColumnAndRowList")
    public void queryDwQuChenColumnAndRowList(InputObject inputObject, OutputObject outputObject) {
        dwQuChenColumnService.queryPageList(inputObject, outputObject);
    }

    /**
     * 根据ID物理删除矩阵题-列-行选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwQuChenColumnAndRowById", value = "根据ID物理删除矩阵题-列-行选项表信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwQuChenColumnController/deleteDwQuChenColumnAndRowById")
    public void deleteDwQuChenColumnAndRowById(InputObject inputObject, OutputObject outputObject) {
        dwQuChenColumnService.deleteById(inputObject, outputObject);
    }

    /**
     * 逻辑删除矩阵题-列-行选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "changeVisibility", value = "逻辑删除矩阵题-列-行选项表信息", method = "POST", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
            @ApiImplicitParam(id = "quId", name = "quId", value = "所属题Id", required = "required"),
            @ApiImplicitParam(id = "createId", name = "createId", value = "创建人Id", required = "required")})
    @RequestMapping("/post/DwQuChenColumnController/changeVisibility")
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        dwQuChenColumnService.changeVisibility(inputObject, outputObject);
    }

}
