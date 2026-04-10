/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.afterseal.classenum.AfterSealState;
import com.skyeye.afterseal.dao.SealEvaluateDao;
import com.skyeye.afterseal.entity.AfterSeal;
import com.skyeye.afterseal.entity.SealEvaluate;
import com.skyeye.afterseal.service.AfterSealService;
import com.skyeye.afterseal.service.SealEvaluateService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SealEvaluateServiceImpl
 * @Description: 工单服务评价服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/15 18:19
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "工单服务评价", groupName = "工单管理", teamAuth = true)
public class SealEvaluateServiceImpl extends SkyeyeBusinessServiceImpl<SealEvaluateDao, SealEvaluate> implements SealEvaluateService {

    @Autowired
    private AfterSealService afterSealService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.querySealEvaluateList(commonPageInfo);
        return beans;
    }

    @Override
    protected void validatorEntity(SealEvaluate entity) {
        AfterSeal afterSeal = afterSealService.selectById(entity.getObjectId());
        if (StrUtil.equals(afterSeal.getState(), AfterSealState.BE_EVALUATED.getKey())) {
            // 待评价可以进行评价
        } else {
            throw new CustomException("该工单已经评价。");
        }
    }

    @Override
    protected void createPostpose(SealEvaluate entity, String userId) {
        // 修改工单信息为【待审核】
        afterSealService.updateStateById(entity.getObjectId(), AfterSealState.AUDIT.getKey());
    }

}
