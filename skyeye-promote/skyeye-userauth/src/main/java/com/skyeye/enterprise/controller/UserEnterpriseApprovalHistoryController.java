/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.enterprise.entity.UserEnterpriseApprovalHistory;
import com.skyeye.enterprise.service.UserEnterpriseApprovalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: UserEnterpriseApprovalHistoryController
 * @Description: 企业账号审批历史控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/16 9:03
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "企业账号审批历史", tags = "企业账号审批历史", modelName = "企业账户")
public class UserEnterpriseApprovalHistoryController {

    @Autowired
    private UserEnterpriseApprovalHistoryService userEnterpriseApprovalHistoryService;

    @ApiOperation(id = "createUserEnterpriseApprovalHistory", value = "提交审批历史", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = UserEnterpriseApprovalHistory.class)
    @RequestMapping("/post/UserEnterpriseApprovalHistoryController/createUserEnterpriseApprovalHistory")
    public void createUserEnterpriseApprovalHistory(InputObject inputObject, OutputObject outputObject) {
        userEnterpriseApprovalHistoryService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryApprovalHistoryListByUserEnterpriseId", value = "根据企业账户id查询审批历史", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "userEnterpriseId", name = "userEnterpriseId", value = "企业账户id", required = "required")})
    @RequestMapping("/post/UserEnterpriseApprovalHistoryController/queryApprovalHistoryListByUserEnterpriseId")
    public void queryApprovalHistoryListByUserEnterpriseId(InputObject inputObject, OutputObject outputObject) {
        userEnterpriseApprovalHistoryService.queryApprovalHistoryListByUserEnterpriseId(inputObject, outputObject);
    }

}
