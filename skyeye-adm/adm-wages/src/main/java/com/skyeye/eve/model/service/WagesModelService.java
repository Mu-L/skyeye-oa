/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.model.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.model.entity.WagesModel;

import java.util.List;

/**
 * @ClassName: WagesModelService
 * @Description: 薪资模板服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/21 11:19
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface WagesModelService extends SkyeyeBusinessService<WagesModel> {

    /**
     * 根据日期查找已经启用&&指定日期有效的薪资模板信息
     *
     * @param date
     * @return
     */
    List<WagesModel> queryWagesModelByDate(String date);

}
