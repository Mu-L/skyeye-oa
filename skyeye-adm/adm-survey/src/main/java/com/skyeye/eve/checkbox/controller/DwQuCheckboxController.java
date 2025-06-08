package com.skyeye.eve.checkbox.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.checkbox.service.DwQuCheckboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DocumentAuthController
 * @Description: 多选题选项管理控制层
 * @author: skyeye云系列--卢雨佳
 * @date: 2025/2/21
 */
@RestController
@Api(value = "多选题选项表管理", tags = "多选题选项表管理", modelName = "多选题选项表管理")
public class DwQuCheckboxController {

    @Autowired
    private DwQuCheckboxService dwQuCheckboxService;

    /**
     * 分页获取多选题选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwQuCheckboxList", value = "分页获取多选题选项表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwQuCheckboxController/queryDwQuCheckboxList")
    public void queryDwQuCheckboxList(InputObject inputObject, OutputObject outputObject) {
        dwQuCheckboxService.queryPageList(inputObject, outputObject);
    }

    /**
     * 根据ID物理删除多选题选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwQuCheckboxById", value = "根据ID物理删除多选题选项表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwQuCheckboxController/deleteDwQuCheckboxById")
    public void deleteDwQuCheckboxById(InputObject inputObject, OutputObject outputObject) {
        dwQuCheckboxService.deleteById(inputObject, outputObject);
    }

    /**
     * 逻辑删除多选题选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "changeVisibility", value = "逻辑删除多选题选项表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwQuCheckboxController/changeVisibility")
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        dwQuCheckboxService.changeVisibility(inputObject, outputObject);
    }

}
