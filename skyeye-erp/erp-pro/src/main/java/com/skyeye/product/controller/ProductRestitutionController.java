package com.skyeye.product.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.product.service.ProductRestitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "归还入库", tags = "归还入库", modelName = "归还入库")
public class ProductRestitutionController {

    @Autowired
    private ProductRestitutionService productRestitutionService;

    /**
     * 查询归还入库信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductRestitutionList", value = "查询归还入库信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProductRestitutionController/queryProductRestitutionList")
    public void queryProductRestitutionList(InputObject inputObject, OutputObject outputObject) {
        productRestitutionService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除归还入库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteProductRestitutionById", value = "删除归还入库单", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductRestitutionController/deleteProductRestitutionById")
    public void deleteProductRestitutionById(InputObject inputObject, OutputObject outputObject) {
        productRestitutionService.deleteById(inputObject, outputObject);
    }


}
