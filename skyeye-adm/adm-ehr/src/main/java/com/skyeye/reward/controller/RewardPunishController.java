/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.reward.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.reward.entity.RewardPunish;
import com.skyeye.reward.service.RewardPunishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: RewardPunishController
 * @Description: 员工奖惩信息管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/17 8:02
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "员工奖惩信息", tags = "员工奖惩信息", modelName = "员工奖惩信息")
public class RewardPunishController {

    @Autowired
    private RewardPunishService rewardPunishService;

    @ApiOperation(id = "queryRewardPunishList", value = "查询奖惩列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/RewardPunishController/queryRewardPunishList")
    public void queryRewardPunishList(InputObject inputObject, OutputObject outputObject) {
        rewardPunishService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeRewardPunish", value = "新增/编辑员工奖惩信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = RewardPunish.class)
    @RequestMapping("/post/RewardPunishController/writeRewardPunish")
    public void writeRewardPunish(InputObject inputObject, OutputObject outputObject) {
        rewardPunishService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteRewardPunishById", value = "根据id删除员工奖惩信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/RewardPunishController/deleteRewardPunishById")
    public void deleteRewardPunishById(InputObject inputObject, OutputObject outputObject) {
        rewardPunishService.deleteById(inputObject, outputObject);
    }

}
