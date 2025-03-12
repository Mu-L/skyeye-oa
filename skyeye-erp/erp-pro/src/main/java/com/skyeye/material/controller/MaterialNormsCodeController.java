/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.material.entity.MaterialNormsCodeQueryDo;
import com.skyeye.material.service.MaterialNormsCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MaterialNormsCodeController
 * @Description: 商品条形码控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/21 14:44
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "商品条形码", tags = "商品条形码", modelName = "商品管理")
public class MaterialNormsCodeController {

    @Autowired
    private MaterialNormsCodeService materialNormsCodeService;

    @ApiOperation(id = "queryMaterialNormsCodeList", value = "获取商品规格一物一码信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = MaterialNormsCodeQueryDo.class)
    @RequestMapping("/post/MaterialNormsCodeController/queryMaterialNormsCodeList")
    public void queryMaterialNormsCodeList(InputObject inputObject, OutputObject outputObject) {
        materialNormsCodeService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "insertMaterialNormsCode", value = "生成条形码，自动过滤不需要生成条形码的商品", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "list", name = "list", value = "商品信息，必须包含materialId,normsId,operNumber", required = "required,json")})
    @RequestMapping("/post/MaterialNormsCodeController/insertMaterialNormsCode")
    public void insertMaterialNormsCode(InputObject inputObject, OutputObject outputObject) {
        materialNormsCodeService.insertMaterialNormsCode(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteMaterialNormsCodeById", value = "删除商品条形码", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MaterialNormsCodeController/deleteMaterialNormsCodeById")
    public void deleteMaterialNormsCodeById(InputObject inputObject, OutputObject outputObject) {
        materialNormsCodeService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryNormsBarCodeList", value = "根据条件获取条形码信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = MaterialNormsCodeQueryDo.class)
    @RequestMapping("/post/MaterialNormsCodeController/queryNormsBarCodeList")
    public void queryNormsBarCodeList(InputObject inputObject, OutputObject outputObject) {
        materialNormsCodeService.queryNormsBarCodeList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryNormsStockDetailList", value = "获取部门/车间物料库存明细信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = MaterialNormsCodeQueryDo.class)
    @RequestMapping("/post/MaterialNormsCodeController/queryNormsStockDetailList")
    public void queryNormsStockDetailList(InputObject inputObject, OutputObject outputObject) {
        materialNormsCodeService.queryNormsStockDetailList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStoreNormsStockDetailList", value = "获取门店物料库存明细信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = MaterialNormsCodeQueryDo.class)
    @RequestMapping("/post/MaterialNormsCodeController/queryStoreNormsStockDetailList")
    public void queryStoreNormsStockDetailList(InputObject inputObject, OutputObject outputObject) {
        materialNormsCodeService.queryStoreNormsStockDetailList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMaterialNormsCode", value = "根据编码等信息查询门店规格条形码信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "normsCodeList", name = "normsCodeList", value = "规格编码，多个用逗号隔开", required = "required"),
        @ApiImplicitParam(id = "storeUseState", name = "storeUseState", value = "门店使用状态")})
    @RequestMapping("/post/MaterialNormsCodeController/queryMaterialNormsCode")
    public void queryMaterialNormsCode(InputObject inputObject, OutputObject outputObject) {
        materialNormsCodeService.queryMaterialNormsCode(inputObject, outputObject);
    }

    @ApiOperation(id = "editStoreMaterialNormsCodeUseState", value = "根据编码等信息修改门店规格条形码使用状态", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "规格条形码信息id，多个逗号隔开", required = "required"),
        @ApiImplicitParam(id = "storeUseState", name = "storeUseState", value = "门店使用状态", required = "required")})
    @RequestMapping("/post/MaterialNormsCodeController/editStoreMaterialNormsCodeUseState")
    public void editStoreMaterialNormsCodeUseState(InputObject inputObject, OutputObject outputObject) {
        materialNormsCodeService.editStoreMaterialNormsCodeUseState(inputObject, outputObject);
    }

}
