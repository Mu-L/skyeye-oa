package com.skyeye.store.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.store.service.ShopAddressHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "历史收件地址管理", tags = "历史收件地址管理", modelName = "历史收件地址管理")
public class ShopAddressHistoryController {

    @Autowired
    private ShopAddressHistoryService shopAddressHistoryService;

    @ApiOperation(id = "queryShopAddressHistoryPageList", value = "分页获取历史收件地址信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
            @ApiImplicitParam(id = "typeId", name = "typeId", value = "订单id")})
    @RequestMapping("/post/ShopAddressHistoryController/queryShopAddressHistoryPageList")
    public void queryShopAddressHistoryPageList(InputObject inputObject, OutputObject outputObject) {
        shopAddressHistoryService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyShopAddressHistoryPageList", value = "分页获取我的历史收件地址信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
            @ApiImplicitParam(id = "typeId", name = "typeId", value = "订单id")})
    @RequestMapping("/post/ShopAddressHistoryController/queryMyShopAddressHistoryPageList")
    public void queryMyShopAddressHistoryPageList(InputObject inputObject, OutputObject outputObject) {
        shopAddressHistoryService.queryMyShopAddressHistoryPageList(inputObject, outputObject);
    }
}
