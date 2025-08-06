package com.skyeye.cost.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.cost.entity.ProUserCost;
import com.skyeye.cost.service.ProUserCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ProUserCostController
 * @Description: 人力成本管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2023/7/24 8:01
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "人力成本管理", tags = "人力成本管理", modelName = "人力成本管理")
public class ProUserCostController {

    @Autowired
    private ProUserCostService proUserCostService;

    @ApiOperation(id = "writeProUserCosts", value = "新增/编辑人力成本", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ProUserCost.class)
    @RequestMapping("/post/ProUserCostController/writeProUserCosts")
    public void writeProUserCosts(InputObject inputObject, OutputObject outputObject) {
        proUserCostService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryProUserCostsList", value = "获取人力成本列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProUserCostController/queryProUserCostsList")
    public void queryProUserCostsList(InputObject inputObject, OutputObject outputObject) {
        proUserCostService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryProUserCostsById", value = "获取人力成本详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")
    })
    @RequestMapping("/post/ProUserCostController/queryProUserCostsById")
    public void queryProUserCostsById(InputObject inputObject, OutputObject outputObject) {
        proUserCostService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteProUserCostsById", value = "删除人力成本", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")
    })
    @RequestMapping("/post/ProUserCostController/deleteProUserCostsById")
    public void deleteProUserCostsById(InputObject inputObject, OutputObject outputObject) {
        proUserCostService.deleteById(inputObject, outputObject);
    }
}
