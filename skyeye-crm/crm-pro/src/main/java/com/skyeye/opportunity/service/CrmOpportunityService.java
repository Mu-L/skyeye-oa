/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.opportunity.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.opportunity.entity.CrmOpportunity;

import java.util.List;

/**
 * @ClassName: CrmOpportunityService
 * @Description: 商机管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/26 12:18
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CrmOpportunityService extends SkyeyeBusinessService<CrmOpportunity> {

    void conmunicateOpportunity(InputObject inputObject, OutputObject outputObject);

    void quotedPriceOpportunity(InputObject inputObject, OutputObject outputObject);

    void tenderOpportunity(InputObject inputObject, OutputObject outputObject);

    void negotiateOpportunity(InputObject inputObject, OutputObject outputObject);

    void turnoverOpportunity(InputObject inputObject, OutputObject outputObject);

    void losingTableOpportunity(InputObject inputObject, OutputObject outputObject);

    void layAsideOpportunity(InputObject inputObject, OutputObject outputObject);

    List<CrmOpportunity> queryCrmOpportunityListByObjectId(String objectId);

    void queryCrmOpportunityListByObjectId(InputObject inputObject, OutputObject outputObject);
}
