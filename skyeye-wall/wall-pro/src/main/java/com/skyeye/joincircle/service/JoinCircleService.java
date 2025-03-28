/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.joincircle.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.joincircle.entity.JoinCircle;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: JoinCircleService
 * @Description: 加入圈子服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface JoinCircleService extends SkyeyeBusinessService<JoinCircle> {
    JoinCircle selectByCircleId(String circleId, String userId);

    void deleteJoinByCircleId(String circleId);

    Boolean checkIsJoinCircle(String circleId, String userId);

    List<JoinCircle> queryMyJoinCircle(String userId);

    void deleteJoinCircleByCircleId(InputObject inputObject, OutputObject outputObject);

    Map<String, Boolean> checkIsJoinCircle(List<String> circleIds, String userId);
}
