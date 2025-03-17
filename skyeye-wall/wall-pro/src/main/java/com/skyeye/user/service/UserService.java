/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.user.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.user.entity.User;

import java.util.List;

/**
 * @ClassName: UserService
 * @Description: 用户服务接口层
 * @author: xqz
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface UserService extends SkyeyeBusinessService<User> {

    void wallUserLogin(InputObject inputObject, OutputObject outputObject);

    void wallUserRegister(InputObject inputObject, OutputObject outputObject);

    void wallUserExit(InputObject inputObject, OutputObject outputObject);

    void editWallPassword(InputObject inputObject, OutputObject outputObject);

    void updateBackgroundImage(String id, String image);

    void setCertification(String id, String studentNumber, String realName);

    void queryUserByRealNameOrStudentNumber(InputObject inputObject, OutputObject outputObject);
}