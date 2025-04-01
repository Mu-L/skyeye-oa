/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.key.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.key.dao.AiApiKeyDao;
import com.skyeye.key.entity.AiApiKey;
import com.skyeye.key.service.AiApiKeyService;
import com.skyeye.role.entity.Role;
import com.skyeye.role.service.RoleService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ShopDeliveryCompanyController
 * @Description: ai配置服务类
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/8 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "API配置", groupName = "API配置")
public class AiApiKeyServiceImpl extends SkyeyeBusinessServiceImpl<AiApiKeyDao, AiApiKey> implements AiApiKeyService {

    @Autowired
    private RoleService roleService;

    /**
     * 重写新增编辑前置条件API配置
     */
    @Override
    public void validatorEntity(AiApiKey aiApiKey) {
        super.validatorEntity(aiApiKey);
        //判断RoleId是否存在
        if (StrUtil.isNotEmpty(aiApiKey.getRoleId())) {
            Role role = roleService.selectById(aiApiKey.getRoleId());
            //判断RoleId是否为空，如果为空，则抛出异常
            if (role.getId() == null) {
                throw new CustomException("角色不存在: " + aiApiKey.getRoleId());
            }
        }
    }

    /**
     * 获取全部API配置
     *
     * @param inputObject 入参以及用户信息等获取对象
     * @return
     */
    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        QueryWrapper<AiApiKey> queryWrapper = new QueryWrapper<>();
        List<AiApiKey> beans = list(queryWrapper);
        return JSONUtil.toList(JSONUtil.toJsonStr(beans), null);
    }
}
