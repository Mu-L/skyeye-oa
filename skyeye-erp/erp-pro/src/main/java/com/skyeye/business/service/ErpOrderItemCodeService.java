/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.business.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.entity.ErpOrderItemCode;

import java.util.List;

/**
 * @ClassName: ErpOrderItemCodeService
 * @Description: 单据子表关联的条形码编号服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/5 19:03
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ErpOrderItemCodeService extends SkyeyeBusinessService<ErpOrderItemCode> {

    void saveList(String parentId, List<ErpOrderItemCode> beans);

    void deleteByParentId(String parentId);

    List<ErpOrderItemCode> selectByParentId(String... parentId);

    List<ErpOrderItemCode> selectByNormsCode(String ...normsCodes);
}
