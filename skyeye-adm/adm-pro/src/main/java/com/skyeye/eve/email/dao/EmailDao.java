/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.email.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.email.entity.Email;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EmailDao
 * @Description: 邮件管理数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/8 9:13
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface EmailDao extends SkyeyeBaseMapper<Email> {

    @IgnoreTenant
    List<Map<String, Object>> queryEmailListByEmailId(CommonPageInfo commonPageInfo);

    @IgnoreTenant
    List<Map<String, Object>> queryEmailListByEmailAddress(@Param("userAddress") String userAddress,
                                                           @Param("state") Integer state,
                                                           @Param("tenantId") String tenantId);

    @IgnoreTenant
    int insertEmailListToServer(@Param("list") List<Map<String, Object>> enclosureBeans,
                                @Param("tenantId") String tenantId);

    @IgnoreTenant
    int insertEmailEnclosureListToServer(@Param("list") List<Map<String, Object>> beans,
                                         @Param("tenantId") String tenantId);

    @IgnoreTenant
    int editEmailMessageIdByEmailId(Map<String, Object> emailEditMessageId);

    @IgnoreTenant
    List<Map<String, Object>> queryEmailListByEmailFromAddress(@Param("userAddress") String userAddress,
                                                               @Param("state") Integer state,
                                                               @Param("tenantId") String tenantId);


}
