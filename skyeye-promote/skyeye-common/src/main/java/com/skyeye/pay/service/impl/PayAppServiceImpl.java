/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.pay.dao.PayAppDao;
import com.skyeye.pay.entity.PayApp;
import com.skyeye.pay.service.PayAppService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: PayAppServiceImpl
 * @Description: 支付应用服务层--平台隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "支付应用管理", groupName = "支付应用管理", tenant = TenantEnum.PLATE)
public class PayAppServiceImpl extends SkyeyeBusinessServiceImpl<PayAppDao, PayApp> implements PayAppService {

    @Override
    public void updatePrepose(PayApp payApp) {
        verify(payApp.getId());
    }

    private void verify(String id) {
        QueryWrapper<PayApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        PayApp one = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("该支付应用信息不存在");
        }
    }

    @Override
    protected void writePostpose(PayApp entity, String userId) {
        super.writePostpose(entity, userId);
        if (entity.getEnabled().equals(EnableEnum.ENABLE_USING.getKey())) {
            // 如果将当前数据修改为启动数据，则需要修改之前的数据为禁用
            UpdateWrapper<PayApp> updateWrapper = new UpdateWrapper<>();
            updateWrapper.ne(CommonConstants.ID, entity.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(PayApp::getEnabled), EnableEnum.DISABLE_USING.getKey());
            update(updateWrapper);
        }
    }

    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        QueryWrapper<PayApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PayApp::getEnabled), CommonNumConstants.NUM_ONE);
        List<PayApp> list = list(queryWrapper);
        return JSONUtil.toList(JSONUtil.toJsonStr(list), null);
    }

    @Override
    @IgnoreTenant
    public <M> void setDataMation(M bean, SFunction<M, ?> sFunction) {
        super.setDataMation(bean, sFunction);
    }
}
