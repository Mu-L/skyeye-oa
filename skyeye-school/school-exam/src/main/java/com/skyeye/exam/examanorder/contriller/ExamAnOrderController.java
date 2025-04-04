package com.skyeye.exam.examanorder.contriller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanorder.entity.ExamAnOrder;
import com.skyeye.exam.examanorder.service.ExamAnOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnOrderController
 * @Description: 答卷 评分题控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "答卷 排序题", tags = "答卷 排序题", modelName = "答卷 排序题")
public class ExamAnOrderController {

    @Autowired
    private ExamAnOrderService examAnOrderService;

    /**
     * 新增/编辑评分题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnOrder", value = "新增/编辑评分题", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ExamAnOrder.class)
    @RequestMapping("/post/ExamAnOrderController/writeExamAnOrder")
    public void writeExamAnOrder(InputObject inputObject, OutputObject outputObject) {
        examAnOrderService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取评分题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnOrderList", value = "获取评分题信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnOrderController/queryExamAnOrderList")
    public void queryExamAnOrderList(InputObject inputObject, OutputObject outputObject) {
        examAnOrderService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除评分题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnOrderById", value = "删除评分题信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnOrderController/deleteExamAnOrderById")
    public void deleteExamAnOrderById(InputObject inputObject, OutputObject outputObject) {
        examAnOrderService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取评分题列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnOrderById", value = "根据id获取填空题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnOrderController/queryExamAnOrderById")
    public void queryExamAnOrderById(InputObject inputObject, OutputObject outputObject) {
        examAnOrderService.queryExamAnOrderById(inputObject, outputObject);
    }

}
