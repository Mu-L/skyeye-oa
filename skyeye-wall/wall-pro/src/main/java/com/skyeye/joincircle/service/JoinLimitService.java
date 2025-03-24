package com.skyeye.joincircle.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.joincircle.entity.JoinLimit;

/**
 * @ClassName: JoinLimitService
 * @Description: 加入圈子限制服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface JoinLimitService extends SkyeyeBusinessService<JoinLimit> {
    boolean checkIsAllowJoin(String circleId, String userId);
}
