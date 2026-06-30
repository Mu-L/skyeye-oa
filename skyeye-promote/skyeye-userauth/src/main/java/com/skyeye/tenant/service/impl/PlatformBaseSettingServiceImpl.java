/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.exception.CustomException;
import com.skyeye.tenant.classenum.PlatformBaseSettingGroup;
import com.skyeye.tenant.constans.PlatformBaseSettingConst;
import com.skyeye.tenant.dao.PlatformBaseSettingDao;
import com.skyeye.tenant.entity.PlatformBaseSetting;
import com.skyeye.tenant.service.PlatformBaseSettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: PlatformBaseSettingServiceImpl
 * @Description: 平台基础信息设置服务层（多租户模式下，仅平台租户可维护）
 * <p>
 * settingData 采用「分组 -> 设置项」的 JSON 结构，全局仅一条记录（单例配置）。
 * 新增设置项时：在 {@link PlatformBaseSettingGroup}、{@link PlatformBaseSettingConst} 中扩展，
 * 并在 {@link #validateSettingData} 中补充校验逻辑。
 */
@Service
@SkyeyeService(name = "平台基础信息设置", groupName = "租户管理", tenant = TenantEnum.PLATE)
public class PlatformBaseSettingServiceImpl extends SkyeyeBusinessServiceImpl<PlatformBaseSettingDao, PlatformBaseSetting> implements PlatformBaseSettingService {

    /**
     * 未配置时的默认席位单价（元/席位）
     */
    private static final String DEFAULT_ACCOUNT_UNIT_PRICE = "0.00";

    /**
     * 查询平台基础信息（管理端使用，需平台租户身份）
     */
    @Override
    public void queryPlatformBaseSetting(InputObject inputObject, OutputObject outputObject) {
        assertPlatformTenant();
        PlatformBaseSetting platformBaseSetting = getSingletonSetting();
        outputObject.setBean(platformBaseSetting);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 保存平台基础信息（增量合并：仅覆盖本次提交的分组字段，不影响其他分组）
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void updatePlatformBaseSetting(InputObject inputObject, OutputObject outputObject) {
        assertPlatformTenant();
        PlatformBaseSetting entity = inputObject.getParams(PlatformBaseSetting.class);
        validateSettingData(entity.getSettingData());
        PlatformBaseSetting existing = getOne(new QueryWrapper<>(), false);
        String userId = inputObject.getLogParams().get("id").toString();
        String settingId;
        if (ObjectUtil.isEmpty(existing)) {
            // 首次保存：以默认值打底，再覆盖本次提交内容
            entity.setSettingData(mergeSettingData(buildDefaultSettingData(), entity.getSettingData()));
            settingId = createEntity(entity, userId);
        } else {
            // 已有记录：在现有数据上增量合并，避免丢失未提交的分组
            entity.setId(existing.getId());
            entity.setSettingData(mergeSettingData(existing.getSettingData(), entity.getSettingData()));
            settingId = updateEntity(entity, userId);
        }
        outputObject.setBean(selectById(settingId));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 对外查询席位单价（普通租户可读，供购买订单等场景使用）
     */
    @Override
    @IgnoreTenant
    public void queryPlatformAccountUnitPrice(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> data = new HashMap<>();
        data.put(PlatformBaseSettingConst.KEY_ACCOUNT_UNIT_PRICE, getAccountUnitPrice());
        outputObject.setBean(data);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 获取平台配置的租户成员席位单价，供内部业务调用（如创建购买订单时默认带出单价）
     */
    @Override
    @IgnoreTenant
    public String getAccountUnitPrice() {
        PlatformBaseSetting setting = getOne(new QueryWrapper<>(), false);
        if (ObjectUtil.isEmpty(setting) || MapUtil.isEmpty(setting.getSettingData())) {
            return DEFAULT_ACCOUNT_UNIT_PRICE;
        }
        Map<String, Object> tenantGroup = setting.getSettingData().get(PlatformBaseSettingGroup.TENANT.getKey());
        if (MapUtil.isEmpty(tenantGroup)) {
            return DEFAULT_ACCOUNT_UNIT_PRICE;
        }
        Object price = tenantGroup.get(PlatformBaseSettingConst.KEY_ACCOUNT_UNIT_PRICE);
        return ObjectUtil.isEmpty(price) ? DEFAULT_ACCOUNT_UNIT_PRICE : price.toString();
    }

    @Override
    public void validatorEntity(PlatformBaseSetting entity) {
        validateSettingData(entity.getSettingData());
    }

    /**
     * 获取单例配置；库中无记录时返回带默认值的对象（不落库）
     */
    private PlatformBaseSetting getSingletonSetting() {
        PlatformBaseSetting setting = getOne(new QueryWrapper<>(), false);
        if (ObjectUtil.isEmpty(setting)) {
            setting = new PlatformBaseSetting();
            setting.setSettingData(buildDefaultSettingData());
            return setting;
        }
        if (MapUtil.isEmpty(setting.getSettingData())) {
            setting.setSettingData(buildDefaultSettingData());
        } else {
            setting.setSettingData(mergeSettingData(buildDefaultSettingData(), setting.getSettingData()));
        }
        return setting;
    }

    /**
     * 构建各分组的默认配置，确保前端展示与合并时有完整结构
     */
    private Map<String, Map<String, Object>> buildDefaultSettingData() {
        Map<String, Map<String, Object>> settingData = new HashMap<>();
        Map<String, Object> tenantGroup = new HashMap<>();
        tenantGroup.put(PlatformBaseSettingConst.KEY_ACCOUNT_UNIT_PRICE, DEFAULT_ACCOUNT_UNIT_PRICE);
        settingData.put(PlatformBaseSettingGroup.TENANT.getKey(), tenantGroup);
        return settingData;
    }

    /**
     * 增量合并设置数据：incoming 中的分组/字段覆盖 base 对应项，未提交的分组保持不变
     */
    private Map<String, Map<String, Object>> mergeSettingData(Map<String, Map<String, Object>> base,
                                                              Map<String, Map<String, Object>> incoming) {
        Map<String, Map<String, Object>> merged = new HashMap<>();
        if (MapUtil.isNotEmpty(base)) {
            base.forEach((groupKey, groupValue) -> merged.put(groupKey, new HashMap<>(groupValue)));
        }
        if (MapUtil.isEmpty(incoming)) {
            return merged;
        }
        incoming.forEach((groupKey, groupValue) -> {
            if (MapUtil.isEmpty(groupValue)) {
                return;
            }
            merged.computeIfAbsent(groupKey, key -> new HashMap<>()).putAll(groupValue);
        });
        return merged;
    }

    /**
     * 按分组校验设置项；新增分组时在此扩展校验规则
     */
    private void validateSettingData(Map<String, Map<String, Object>> settingData) {
        if (MapUtil.isEmpty(settingData)) {
            return;
        }
        Map<String, Object> tenantGroup = settingData.get(PlatformBaseSettingGroup.TENANT.getKey());
        if (MapUtil.isEmpty(tenantGroup)) {
            return;
        }
        Object accountUnitPrice = tenantGroup.get(PlatformBaseSettingConst.KEY_ACCOUNT_UNIT_PRICE);
        if (ObjectUtil.isEmpty(accountUnitPrice) || StrUtil.isBlank(accountUnitPrice.toString())) {
            throw new CustomException("成员席位单价不能为空");
        }
        validatePrice(accountUnitPrice.toString(), "成员席位单价");
    }

    /**
     * 校验金额格式：非负，最多两位小数
     */
    private void validatePrice(String price, String label) {
        if (!price.matches("^\\d+(\\.\\d{1,2})?$")) {
            throw new CustomException(label + "格式不正确，最多保留两位小数");
        }
        BigDecimal amount = NumberUtil.toBigDecimal(price);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(label + "不能小于0");
        }
    }

    /**
     * 断言当前为平台租户，管理类接口调用前校验
     */
    private void assertPlatformTenant() {
        if (!tenantEnable) {
            throw new IllegalArgumentException("租户功能未开启");
        }
        String tenantId = TenantContext.getTenantId();
        if (!StrUtil.equals(tenantId, TenantTypeEnum.PLATFORM.getCode())) {
            throw new IllegalArgumentException("非平台租户不能访问");
        }
    }

}
