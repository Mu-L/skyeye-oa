/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.user.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.user.entity.ChooseUser;

/**
 * @ClassName: ChooseUserService
 * @Description: 用户服务接口层
 * @author: xqz
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ChooseUserService extends SkyeyeBusinessService<ChooseUser> {

    void chooseUserLogin(InputObject inputObject, OutputObject outputObject);

    void chooseUserExit(InputObject inputObject, OutputObject outputObject);

    void editChoosePassword(InputObject inputObject, OutputObject outputObject);

    void importChooseUser(InputObject inputObject, OutputObject outputObject);

    void importTeacherChooseUser(InputObject inputObject, OutputObject outputObject);
}