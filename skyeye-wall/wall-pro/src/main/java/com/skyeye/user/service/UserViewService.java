package com.skyeye.user.service;


import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.user.entity.UserView;

/**
 * @ClassName: UserViewService
 * @Description: 用户访客记录服务层
 * @author: lqy
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface UserViewService extends SkyeyeBusinessService<UserView> {
    void queryUserVisitors(InputObject inputObject, OutputObject outputObject);

    void deleteAllUserVisitors(InputObject inputObject, OutputObject outputObject);
}
