/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.common.service;

import com.skyeye.school.common.entity.UserOrStudent;

import java.util.Map;

/**
 * @ClassName: SchoolCommonService
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2025/3/23 22:16
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SchoolCommonService {

    UserOrStudent queryUserOrStudent(String userId);

    void checkUserCertification(Map<String, Object> certification);

}
