/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.ObjectConstant;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.personnel.dao.SysEveUserDao;
import com.skyeye.win.dao.SysEveUserCustomMenuboxDao;
import com.skyeye.win.entity.SysEveUserCustomMenu;
import com.skyeye.win.entity.SysEveUserCustomMenubox;
import com.skyeye.win.service.SysEveUserCustomMenuboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveUserCustomMenuboxServiceImpl
 * @Description: 用户自定义菜单盒子服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/5 20:18
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用户自定义菜单盒子", groupName = "用户自定义菜单盒子")
public class SysEveUserCustomMenuboxServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserCustomMenuboxDao, SysEveUserCustomMenubox> implements SysEveUserCustomMenuboxService {

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Autowired
    public SysEveUserDao sysEveUserDao;

    @Override
    protected void validatorEntity(SysEveUserCustomMenubox entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<SysEveUserCustomMenubox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserCustomMenubox::getCreateId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserCustomMenubox::getName), entity.getName());
        // 更新时排除本身
        if (StrUtil.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        SysEveUserCustomMenubox one = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(one)) {
            throw new CustomException("名称已存在，请重新输入！");
        }
    }

    @Override
    protected void createPrepose(SysEveUserCustomMenubox entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<SysEveUserCustomMenubox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserCustomMenubox::getCreateId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SysEveUserCustomMenubox::getOrderBy));
        SysEveUserCustomMenubox one = getOne(queryWrapper, false);
        int order = 1;
        if (ObjectUtil.isNotEmpty(one)) {
            order = one.getOrderBy() + 1;
        }
        entity.setOrderBy(order);
    }

    @Override
    protected void updatePrepose(SysEveUserCustomMenubox entity) {
        SysEveUserCustomMenubox old = selectById(entity.getId());
        entity.setDesktopId(old.getDesktopId());
        entity.setOrderBy(old.getOrderBy());
    }

    @Override
    protected void writePostpose(SysEveUserCustomMenubox entity, String userId) {
        super.writePostpose(entity, userId);
        if (!tenantEnable) {
            // 单租户模式
            // 桌面菜单列表
            List<Map<String, Object>> deskTops = sysEveUserDao.queryDeskTopsMenuByUserId(userId);
            deskTops = ToolUtil.listToTree(deskTops, "id", "parentId", "childs");
            jedisClientService.set(ObjectConstant.getDeskTopsCacheKey(userId), JSONUtil.toJsonStr(deskTops));
        }
    }
}
