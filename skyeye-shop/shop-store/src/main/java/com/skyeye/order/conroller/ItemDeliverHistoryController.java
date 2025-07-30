package com.skyeye.order.conroller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.order.service.ItemDeliverHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @ClassName: ItemDeliverHistoryController
 * @Description: 商品订单子单据快递信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "商品订单子单项快递信息管理", tags = "商品订单子单项快递信息管理", modelName = "商品订单子单项快递信息管理")
public class ItemDeliverHistoryController {

    @Autowired
    private ItemDeliverHistoryService itemDeliverHistoryService;

    @ApiOperation(id = "queryItemDeliverHistoryPageList", value = "分页查询子单据快递信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
            @ApiImplicitParam(id = "companyId", name = "companyId", value = "快递公司id"),
            @ApiImplicitParam(id = "typeId", name = "typeId", value = "总订单id"),
            @ApiImplicitParam(id = "objectId", name = "objectId", value = "子单id")})
    @RequestMapping("/post/ItemDeliverHistoryController/queryItemDeliverHistoryPageList")
    public void queryItemDeliverHistoryPageList(InputObject inputObject, OutputObject outputObject) {
        itemDeliverHistoryService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyItemDeliverHistoryPageList", value = "分页查询我的子单据快递信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
            @ApiImplicitParam(id = "companyId", name = "companyId", value = "快递公司id"),
            @ApiImplicitParam(id = "typeId", name = "typeId", value = "总订单id"),
            @ApiImplicitParam(id = "objectId", name = "objectId", value = "子单id")})
    @RequestMapping("/post/ItemDeliverHistoryController/queryMyItemDeliverHistoryPageList")
    public void queryMyItemDeliverHistoryPageList(InputObject inputObject, OutputObject outputObject) {
        itemDeliverHistoryService.queryMyItemDeliverHistoryPageList(inputObject, outputObject);
    }

}
