/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.adsense.service.Impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.adsense.dao.AdsenseDao;
import com.skyeye.adsense.entity.Adsense;
import com.skyeye.adsense.service.AdsenseService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AdsenseServiceImpl
 * @Description: 广告位管理服务层--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "广告位管理", groupName = "广告位管理", tenant = TenantEnum.NO_ISOLATION)
public class AdsenseServiceImpl extends SkyeyeBusinessServiceImpl<AdsenseDao, Adsense> implements AdsenseService {

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        QueryWrapper<Adsense> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Adsense::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<Adsense> beans = list(queryWrapper);
        return JSONUtil.toList(JSONUtil.toJsonStr(beans), null);
    }

    /**
     * 重写新增编辑前置条件广告位管理
     */
    @Override
    public void validatorEntity(Adsense adsense) {
        super.validatorEntity(adsense);
        if (StrUtil.isNotEmpty(adsense.getName()) && adsense.getName().length() > 100) {
            throw new CustomException("广告位名称过长");
        }
        if (adsense.getOrderBy() < -128 || adsense.getOrderBy() > 127) {
            throw new CustomException("广告位排序值超出范围");
        }
    }
}
