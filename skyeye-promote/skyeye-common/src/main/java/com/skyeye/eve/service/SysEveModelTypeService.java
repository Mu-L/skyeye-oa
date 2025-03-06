/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.entity.model.SysEveModelType;

/**
 * @ClassName: SysEveModelTypeService
 * @Description: 系统模板分类业务层
 * @author: skyeye云系列
 * @date: 2021/11/13 11:13
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveModelTypeService extends SkyeyeBusinessService<SysEveModelType> {

    void querySysEveModelTypeByParentId(InputObject inputObject, OutputObject outputObject);

}
