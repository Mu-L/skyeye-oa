package com.skyeye.eve.chen.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.chen.entity.DwAnChenFbk;
import com.skyeye.eve.chen.service.DwAnChenFbkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "答卷 矩阵填空题", tags = "答卷 矩阵填空题", modelName = "答卷 矩阵填空题")
public class DwAnChenFbkController {

    @Autowired
    private DwAnChenFbkService dwAnChenFbkService;

    /**
     * 新增/编辑矩阵填空题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwAnChenFbk", value = "新增/编辑矩阵填空题", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwAnChenFbk.class)
    @RequestMapping("/post/DwAnChenFbkController/writeDwAnChenFbk")
    public void writeDwAnChenFbk(InputObject inputObject, OutputObject outputObject) {
        dwAnChenFbkService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取矩阵填空题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnChenFbkList", value = "获取矩阵填空题信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwAnChenFbkController/queryDwAnChenFbkList")
    public void queryDwAnChenFbkList(InputObject inputObject, OutputObject outputObject) {
        dwAnChenFbkService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除矩阵填空题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwAnChenFbkById", value = "删除矩阵填空题信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnChenFbkController/deleteDwAnChenFbkById")
    public void deleteDwAnChenFbkById(InputObject inputObject, OutputObject outputObject) {
        dwAnChenFbkService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取矩阵填空题列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnChenFbkListById", value = "根据id获取矩阵填空题列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnChenFbkController/queryDwAnChenFbkListById")
    public void queryDwAnChenFbkListById(InputObject inputObject, OutputObject outputObject) {
        dwAnChenFbkService.queryDwAnChenFbkListById(inputObject, outputObject);
    }
}
