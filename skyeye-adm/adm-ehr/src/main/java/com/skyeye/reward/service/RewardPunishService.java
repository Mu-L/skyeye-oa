/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.reward.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.reward.entity.RewardPunish;

import java.util.List;

/**
 * @ClassName: RewardPunishService
 * @Description: 员工奖惩管理服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:41
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface RewardPunishService extends SkyeyeBusinessService<RewardPunish> {

    /**
     * 查询员工在指定月份未计入薪资的奖惩记录
     * @param staffId 员工ID
     * @param accountMonth 年月（如：2025-01）
     * @return 奖惩记录列表
     */
    List<RewardPunish> queryUnAccountedByStaffIdAndMonth(String staffId, String accountMonth);

    /**
     * 批量标记奖惩记录为已计入薪资
     * @param rewardPunishIds 奖惩记录ID列表
     * @param accountMonth 计入薪资的年月（如：2025-01）
     */
    void markAsAccountedBatch(List<String> rewardPunishIds, String accountMonth);
}
