/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.patrol.entity.PatrolTeamMember;

/**
 * @ClassName: PatrolTeamMemberService
 * @Description: 巡检班组人员服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/XX XX:XX
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PatrolTeamMemberService extends SkyeyeBusinessService<PatrolTeamMember> {

    /**
     * 根据班组ID删除所有班组人员
     *
     * @param teamId 班组id
     */
    void deleteMemberListByTeamId(String teamId);
}

