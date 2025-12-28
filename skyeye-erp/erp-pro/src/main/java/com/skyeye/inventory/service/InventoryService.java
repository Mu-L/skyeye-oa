/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.inventory.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.inventory.entity.Inventory;

/**
 * @ClassName: InventoryService
 * @Description: 盘点任务单据服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/18 15:41
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface InventoryService extends SkyeyeBusinessService<Inventory> {

    /**
     * 设置已盘点数量
     *
     * @param id     盘点单据id
     * @param addNum 新增的盘点数量
     */
    void setInventoriedNum(String id, String addNum);

}
