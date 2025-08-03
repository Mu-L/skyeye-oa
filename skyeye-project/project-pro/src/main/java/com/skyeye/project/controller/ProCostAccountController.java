package com.skyeye.project.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.project.entity.CostAccount;
import com.skyeye.project.service.ProCostAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ProCostAccountController
 * @Description: 成本核算管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2023/7/24 8:01
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "成本核算管理", tags = "成本核算管理", modelName = "成本核算管理")
public class ProCostAccountController {

    @Autowired
    private ProCostAccountService proCostAccountService;

    @ApiOperation(id = "queryProCostAccountList", value = "根据成本类型type，项目objectId获取成本核算列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProCostAccountController/queryProCostAccountList")
    public void queryProCostAccountList(InputObject inputObject, OutputObject outputObject) {
        proCostAccountService.queryProCostAccountList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeProCostAccount", value = "新增/编辑成本核算", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CostAccount.class)
    @RequestMapping("/post/ProCostAccountController/writeProCostAccount")
    public void writeProCostAccount(InputObject inputObject, OutputObject outputObject) {
        proCostAccountService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryProCostAccountById", value = "根据id获取成本信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id",name = "id", value = "成本id", required = "required")
    })
    @RequestMapping("/post/ProCostAccountController/queryProCostAccountById")
    public void queryProCostAccountById(InputObject inputObject, OutputObject outputObject) {
        proCostAccountService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteProCostAccountById", value = "根据id删除成本信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id",name = "id", value = "成本id", required = "required")
    })
    @RequestMapping("/post/ProCostAccountController/deleteProCostAccountById")
    public void deleteProCostAccountById(InputObject inputObject, OutputObject outputObject) {
        proCostAccountService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCostAccountViews", value = "成本概览", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "startTime",name = "startTime", value = "开始日期",required = "required"),
            @ApiImplicitParam(id = "endTime",name = "endTime", value = "结束日期",required = "required"),
            @ApiImplicitParam(id = "projectId",name = "projectId", value = "项目id",required = "required")
    })
    @RequestMapping("/post/ProCostAccountController/queryCostAccountViews")
    public void queryCostView(InputObject inputObject, OutputObject outputObject) {
        proCostAccountService.queryCostAccountViews(inputObject, outputObject);
    }
}
