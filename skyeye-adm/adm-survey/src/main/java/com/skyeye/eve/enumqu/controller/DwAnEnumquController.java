package com.skyeye.eve.enumqu.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.enumqu.entity.DwAnEnumqu;
import com.skyeye.eve.enumqu.service.DwAnEnumquService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DwAnEnumquController
 * @Description: 答卷 枚举题答案控制层
 * @author: skyeye云系列--lyj
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "答卷 枚举题答案", tags = "答卷 枚举题答案", modelName = "答卷 枚举题答案")
public class DwAnEnumquController {

    @Autowired
    private DwAnEnumquService dwAnEnumquService;

    /**
     * 新增/编辑枚举题答案
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwAnEnumqu", value = "新增/编辑枚举题答案", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = DwAnEnumqu.class)
    @RequestMapping("/post/DwAnEnumquController/writeDwAnEnumqu")
    public void writeDwAnEnumqu(InputObject inputObject, OutputObject outputObject) {
        dwAnEnumquService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取枚举题答案信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnEnumquList", value = "获取枚举题答案信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwAnEnumquController/queryDwAnEnumquList")
    public void queryDwAnEnumquList(InputObject inputObject, OutputObject outputObject) {
        dwAnEnumquService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除枚举题答案信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwAnEnumquById", value = "删除枚举题答案信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnEnumquController/deleteDwAnEnumquById")
    public void deleteDwAnEnumquById(InputObject inputObject, OutputObject outputObject) {
        dwAnEnumquService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取枚举题答案列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnEnumquListById", value = "根据id获取枚举题答案列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnEnumquController/queryDwAnEnumquListById")
    public void queryDwAnEnumquListById(InputObject inputObject, OutputObject outputObject) {
        dwAnEnumquService.queryDwAnEnumquListById(inputObject, outputObject);
    }

}
