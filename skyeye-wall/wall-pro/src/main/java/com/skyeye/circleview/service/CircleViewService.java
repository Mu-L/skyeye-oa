/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.circleview.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.circleview.entity.CircleView;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: CircleService
 * @Description: 圈子服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CircleViewService extends SkyeyeBusinessService<CircleView> {
    /**
     * 根据圈子id删除圈子浏览记录
     * */
    void deleteCircleViewByCircleId(String circleId);

    void queryUserViewCircleList(InputObject inputObject, OutputObject outputObject);
}
