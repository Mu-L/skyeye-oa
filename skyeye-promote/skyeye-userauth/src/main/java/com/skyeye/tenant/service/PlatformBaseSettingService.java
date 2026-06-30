/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.entity.PlatformBaseSetting;

/**
 * @ClassName: PlatformBaseSettingService
 * @Description: 平台基础信息设置服务接口类
 */
public interface PlatformBaseSettingService extends SkyeyeBusinessService<PlatformBaseSetting> {

    /**
     * 查询平台基础信息（平台租户）
     */
    void queryPlatformBaseSetting(InputObject inputObject, OutputObject outputObject);

    /**
     * 保存平台基础信息，按分组增量合并（平台租户）
     */
    void updatePlatformBaseSetting(InputObject inputObject, OutputObject outputObject);

    /**
     * 查询席位单价（所有租户可读）
     */
    void queryPlatformAccountUnitPrice(InputObject inputObject, OutputObject outputObject);

    /**
     * 按组织类型查询席位计费规则（单价、初始化席位数、最低购买席位数）
     */
    void queryPlatformTenantOrgSeatConfig(InputObject inputObject, OutputObject outputObject);

    /**
     * 获取平台配置的租户成员席位单价（元/席位），供内部业务调用
     */
    String getAccountUnitPrice();

    /**
     * 获取指定组织类型的初始化席位数
     */
    Integer getInitAccountNum(Integer orgType);

    /**
     * 获取指定组织类型的每次最低购买席位数
     */
    Integer getMinBuyAccountNum(Integer orgType);

}
