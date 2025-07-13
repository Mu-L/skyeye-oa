package com.skyeye.product.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.question.QuType;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.product.entity.ProductLeadOutStock;
import com.skyeye.product.entity.ProductReturnInStock;
import com.skyeye.product.service.ProductReturnInStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "归还入库单", tags = "归还入库单", modelName = "归还入库")
public class ProductReturnInStockController {

    @Autowired
    private ProductReturnInStockService productReturnInStockService;

    /**
     * 获取归还入库单列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductReturnInStockList", value = "获取归还入库单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProductReturnInStockController/queryProductReturnInStockList")
    public void queryProductReturnInStockList(InputObject inputObject, OutputObject outputObject) {
        productReturnInStockService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑归还入库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeProductReturnInStock", value = "新增/编辑归还入库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ProductReturnInStock.class)
    @RequestMapping("/post/ProductReturnInStockController/writeProductReturnInStock")
    public void writeProductReturnInStock(InputObject inputObject, OutputObject outputObject) {
        productReturnInStockService.saveOrUpdateEntity(inputObject, outputObject);
    }


    /**
     * 转仓库入库单时，根据id查询归还入库单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductReturnInStockById", value = "转仓库入库单时，根据id查询归还入库单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductReturnInStockController/queryProductReturnInStockById")
    public void queryProductReturnInStockById(InputObject inputObject, OutputObject outputObject) {
        productReturnInStockService.selectById(inputObject, outputObject);
    }

    /**
     * 归还入库提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitProductReturnInStockToApproval", value = "归还入库提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ProductReturnInStockController/submitProductReturnInStockToApproval")
    public void submitProductReturnInStockToApproval(InputObject inputObject, OutputObject outputObject) {
        productReturnInStockService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 删除归还入库
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteProductReturnInStock", value = "删除归还入库", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductReturnInStockController/deleteProductReturnInStock")
    public void deleteProductReturnInStock(InputObject inputObject, OutputObject outputObject) {
        productReturnInStockService.deleteById(inputObject, outputObject);
    }

    /**
     * 撤销归还入库
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeProductReturnInStock", value = "撤销归还入库", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ProductLeadOutStockController/revokeProductReturnInStock")
    public void revokeProductReturnInStock(InputObject inputObject, OutputObject outputObject) {
        productReturnInStockService.revoke(inputObject, outputObject);
    }

    /**
     * 归还入库单信息转仓库入库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertProductReturnInStockToInDepot", value = "归还入库单信息转仓库入库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotPut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductLeadOutStockController/insertProductReturnInStockToInDepot")
    public void insertProductReturnInStockToInDepot(InputObject inputObject, OutputObject outputObject) {
        productReturnInStockService.insertProductReturnInStockToInDepot(inputObject, outputObject);
    }
}
