/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service.impl;

import com.skyeye.afterseal.dao.SealFeedBackDao;
import com.skyeye.afterseal.entity.SealFeedBack;
import com.skyeye.afterseal.service.SealFeedBackService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SealFeedBackServiceImpl
 * @Description: 工单情况反馈信息服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/15 15:21
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "工单情况反馈信息", groupName = "工单管理", teamAuth = true)
public class SealFeedBackServiceImpl extends SkyeyeBusinessServiceImpl<SealFeedBackDao, SealFeedBack> implements SealFeedBackService {

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryFeedBackList(commonPageInfo);
        return beans;
    }

}
