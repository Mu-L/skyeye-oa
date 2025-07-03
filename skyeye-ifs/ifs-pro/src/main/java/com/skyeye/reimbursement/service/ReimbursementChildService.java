/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.reimbursement.service;

import com.skyeye.base.business.service.SkyeyeLinkDataService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.reimbursement.entity.ReimbursementChild;

import java.util.List;

/**
 * @ClassName: ReimbursementChildService
 * @Description: 报销订单子内容服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:21
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ReimbursementChildService extends SkyeyeLinkDataService<ReimbursementChild> {

    /**
     * 计算单据信息的总价
     *
     * @param orderItemList
     * @return
     */
    String calcOrderAllTotalPrice(List<ReimbursementChild> orderItemList);

    void queryReimbursementAnalysis(InputObject inputObject, OutputObject outputObject);
}
