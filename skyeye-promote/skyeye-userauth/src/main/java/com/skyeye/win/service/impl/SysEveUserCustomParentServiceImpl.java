/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.ObjectConstant;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.personnel.dao.SysEveUserDao;
import com.skyeye.win.dao.SysEveUserCustomParentDao;
import com.skyeye.win.entity.SysEveUserCustomParent;
import com.skyeye.win.service.SysEveUserCustomParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveUserCustomParentServiceImpl
 * @Description: 用户菜单自定义拖拽组合服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/5 20:44
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用户菜单自定义拖拽组合", groupName = "用户菜单自定义拖拽组合")
public class SysEveUserCustomParentServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserCustomParentDao, SysEveUserCustomParent> implements SysEveUserCustomParentService {

    @Autowired
    public SysEveUserDao sysEveUserDao;

    @Override
    protected void createPrepose(SysEveUserCustomParent entity) {
        if (StrUtil.isNotEmpty(entity.getParentId())) {
            entity.setParentId(entity.getParentId() + ",");
            entity.setLevel(1);
        } else {
            entity.setParentId("0");
            entity.setLevel(0);
        }
    }

    @Override
    protected void writePostpose(SysEveUserCustomParent entity, String userId) {
        super.writePostpose(entity, userId);
        if (!tenantEnable) {
            // 单租户模式
            // 桌面菜单列表
            List<Map<String, Object>> deskTops = sysEveUserDao.queryDeskTopsMenuByUserId(userId);
            deskTops = ToolUtil.listToTree(deskTops, "id", "parentId", "childs");
            jedisClientService.set(ObjectConstant.getDeskTopsCacheKey(userId), JSONUtil.toJsonStr(deskTops));
        }
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<SysEveUserCustomParent> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("INSTR(CONCAT(',', " + MybatisPlusUtil.toColumns(SysEveUserCustomParent::getParentId) + ", ','), CONCAT(',', {0}, ','))", parentId);
        remove(queryWrapper);
    }

    @Override
    public void deleteByMenuId(String menuId, String userId) {
        QueryWrapper<SysEveUserCustomParent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserCustomParent::getMenuId), menuId)
            .eq(MybatisPlusUtil.toColumns(SysEveUserCustomParent::getCreateId), userId);
        remove(queryWrapper);
    }

    @Override
    public List<SysEveUserCustomParent> querySysEveUserCustomParentByUserId(String userId) {
        QueryWrapper<SysEveUserCustomParent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserCustomParent::getCreateId), userId);
        return list(queryWrapper);
    }

}
