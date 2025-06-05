/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.entity.ActGroupUser;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ActGroupUserDao
 * @Description: 用户组关联用户管理数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/3 22:38
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ActGroupUserDao extends SkyeyeBaseMapper<ActGroupUser> {

    @IgnoreTenant
    List<Map<String, Object>> queryUserListToActiviti(Map<String, Object> map);

    @IgnoreTenant
    List<Map<String, Object>> queryUserListToActivitiByGroup(Map<String, Object> parmter);

    @IgnoreTenant
    List<Map<String, Object>> queryUserInfoOnActGroup(CommonPageInfo commonPageInfo);

}
