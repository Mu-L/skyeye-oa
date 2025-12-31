/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.social.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.social.entity.ApplicableObjects;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ApplicableObjectsService
 * @Description: 社保公积金适用对象服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/11/15 8:45
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ApplicableObjectsService extends SkyeyeBusinessService<ApplicableObjects> {

    void deleteApplicableObjectsByPId(String securityFundId);

    void saveApplicableObjects(String securityFundId, List<ApplicableObjects> applicableObjectsList);

    List<ApplicableObjects> queryApplicableObjectsByPId(String securityFundId);

    Map<String, List<ApplicableObjects>> queryApplicableObjectsByPId(List<String> securityFundId);

}
