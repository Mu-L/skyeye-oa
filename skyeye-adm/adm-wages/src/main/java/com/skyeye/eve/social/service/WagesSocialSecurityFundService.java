/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.social.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.social.entity.SocialSecurityFund;

import java.util.List;

/**
 * @ClassName: WagesSocialSecurityFundService
 * @Description: 社保公积金服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/11/15 8:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface WagesSocialSecurityFundService extends SkyeyeBusinessService<SocialSecurityFund> {

    /**
     * 根据日期查找已经启用&&指定日期有效的社保公积金信息
     *
     * @param date 指定日期的年月，例如：2023-11
     * @return
     */
    List<SocialSecurityFund> querySocialSecurityFundByDate(String date);

}
