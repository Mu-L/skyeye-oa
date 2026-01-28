package com.skyeye.followup.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.followup.entity.ProFollowup;

/**
 * @ClassName: ProFollowupService
 * @Description: 项目跟进Service层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProFollowupService extends SkyeyeBusinessService<ProFollowup> {

    /**
     * 更新跟进状态
     *
     * @param inputObject  输入对象
     * @param outputObject 输出对象
     */
    void updateFollowupState(InputObject inputObject, OutputObject outputObject);

    /**
     * 获取项目跟进统计信息
     *
     * @param inputObject  输入对象
     * @param outputObject 输出对象
     */
    void queryFollowupStatistics(InputObject inputObject, OutputObject outputObject);

}