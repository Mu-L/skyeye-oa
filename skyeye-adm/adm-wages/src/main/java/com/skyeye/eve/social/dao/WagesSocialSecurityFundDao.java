/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.social.dao;

import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.social.entity.SocialSecurityFund;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: WagesSocialSecurityFundDao
 * @Description: 社保公积金数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/11/15 8:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface WagesSocialSecurityFundDao extends SkyeyeBaseMapper<SocialSecurityFund> {

    List<Map<String, Object>> queryWagesSocialSecurityFundList(CommonPageInfo pageInfo);

}
