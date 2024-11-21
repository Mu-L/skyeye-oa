/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.pay.core.PayClient;
import com.skyeye.pay.core.PayClientConfig;
import com.skyeye.pay.core.PayClientFactory;
import com.skyeye.pay.dao.PayChannelDao;
import com.skyeye.pay.entity.PayApp;
import com.skyeye.pay.entity.PayChannel;
import com.skyeye.pay.enums.PayType;
import com.skyeye.pay.service.PayAppService;
import com.skyeye.pay.service.PayChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: PayChannelServiceImpl
 * @Description: 支付渠道服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "支付渠道", groupName = "支付渠道")
public class PayChannelServiceImpl extends SkyeyeBusinessServiceImpl<PayChannelDao, PayChannel> implements PayChannelService {

    @Autowired
    private PayAppService payAppService;

    @Autowired
    private PayClientFactory payClientFactory;

    @Autowired
    private Validator validator;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        payAppService.setMationForMap(beans, "appId", "appMation");
        return beans;
    }

    @Override
    public PayChannel selectById(String id) {
        PayChannel payChannel = super.selectById(id);
        payAppService.setDataMation(payChannel, PayChannel::getAppId);
        payChannel.setCodeNumMation(PayType.getMation(payChannel.getCodeNum()));

        // 解析配置
        Class<? extends PayClientConfig> payClass = PayType.getByCode(payChannel.getCodeNum()).getConfigClass();
        if (ObjectUtil.isNull(payClass)) {
            throw new CustomException("支付渠道的配置不存在");
        }
        PayClientConfig config = JSONUtil.toBean(payChannel.getConfig(), payClass);
        payChannel.setConfigMation(config);

        return payChannel;
    }

    @Override
    public void validatorEntity(PayChannel entity) {
        super.validatorEntity(entity);
        // 解析配置
        Class<? extends PayClientConfig> payClass = PayType.getByCode(entity.getCodeNum()).getConfigClass();
        if (ObjectUtil.isNull(payClass)) {
            throw new CustomException("支付渠道的配置不存在");
        }
        PayClientConfig config = JSONUtil.toBean(entity.getConfig(), payClass);
        Assert.notNull(config);
        // 验证参数
        config.validate(validator);
    }

    @Override
    public void updatePrepose(PayChannel payChannel) {
        QueryWrapper<PayChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, payChannel.getId());
        PayChannel one = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("该支付应用信息不存在");
        }
    }

    @Override
    public PayClient getPayClient(String id) {
        PayChannel payChannel = selectById(id);
        if (ObjectUtil.isEmpty(payChannel)) {
            throw new CustomException("该支付渠道不存在");
        }
        payClientFactory.createOrUpdatePayClient(id, payChannel.getCodeNum(), payChannel.getConfigMation());
        return null;
    }

    @Override
    public PayChannel getPayChannelByCode(String codeNum) {
        MPJLambdaWrapper<PayChannel> queryWrapper = new MPJLambdaWrapper<PayChannel>()
            .innerJoin(PayApp.class, PayApp::getId, PayChannel::getAppId)
            .eq(PayApp::getEnabled, EnableEnum.ENABLE_USING.getKey())
            .eq(PayChannel::getEnabled, EnableEnum.ENABLE_USING.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(PayChannel::getCodeNum), codeNum);
        PayChannel one = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("该支付渠道不存在");
        }
        return one;
    }
}