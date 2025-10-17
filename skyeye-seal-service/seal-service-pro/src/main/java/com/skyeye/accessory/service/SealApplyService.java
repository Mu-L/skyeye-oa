/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.accessory.service;

import com.skyeye.accessory.entity.SealApply;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: SealSeServiceApplyService
 * @Description: 配件申领单管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/11 22:42
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SealApplyService extends SkyeyeBusinessService<SealApply> {

    void editSealApplyOtherState(InputObject inputObject, OutputObject outputObject);

    void editSealApplyOutNum(InputObject inputObject, OutputObject outputObject);
}
