package com.skyeye.eve.order.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.order.entity.DwAnOrder;
import com.skyeye.eve.order.service.DwAnOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DwAnOrderController
 * @Description: 答卷 评分题控制层
 * @author: skyeye云系列--lyj
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "答卷 排序题", tags = "答卷 排序题", modelName = "答卷 排序题")
public class DwAnOrderController {

    @Autowired
    private DwAnOrderService dwAnOrderService;

    /**
     * 新增/编辑评分题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwAnOrder", value = "新增/编辑评分题", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwAnOrder.class)
    @RequestMapping("/post/DwAnOrderController/writeDwAnOrder")
    public void writeDwAnOrder(InputObject inputObject, OutputObject outputObject) {
        dwAnOrderService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取评分题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnOrderList", value = "获取评分题信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwAnOrderController/queryDwAnOrderList")
    public void queryDwAnOrderList(InputObject inputObject, OutputObject outputObject) {
        dwAnOrderService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除评分题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwAnOrderById", value = "删除评分题信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnOrderController/deleteDwAnOrderById")
    public void deleteDwAnOrderById(InputObject inputObject, OutputObject outputObject) {
        dwAnOrderService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取评分题列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnOrderById", value = "根据id获取填空题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnOrderController/queryDwAnOrderById")
    public void queryDwAnOrderById(InputObject inputObject, OutputObject outputObject) {
        dwAnOrderService.queryDwAnOrderById(inputObject, outputObject);
    }

}
