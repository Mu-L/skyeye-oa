/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.ObjectConstant;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.personnel.dao.SysEveUserDao;
import com.skyeye.win.dao.SysEveWinDragDropDao;
import com.skyeye.win.entity.SysEveUserCustomParent;
import com.skyeye.win.service.SysEveUserCustomMenuService;
import com.skyeye.win.service.SysEveUserCustomMenuboxService;
import com.skyeye.win.service.SysEveUserCustomParentService;
import com.skyeye.win.service.SysEveWinDragDropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SysEveWinDragDropServiceImpl implements SysEveWinDragDropService {

    @Autowired
    private SysEveWinDragDropDao sysEveWinDragDropDao;

    @Autowired
    private SysEveUserCustomParentService sysEveUserCustomParentService;

    @Autowired
    private SysEveUserCustomMenuService sysEveUserCustomMenuService;

    @Autowired
    private SysEveUserCustomMenuboxService sysEveUserCustomMenuboxService;

    @Autowired
    public SysEveUserDao sysEveUserDao;

    @Autowired
    public JedisClientService jedisClient;

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void deleteWinMenuOrBoxById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        map.put("userId", userId);
        Map<String, Object> bean = sysEveWinDragDropDao.queryMenuMationFromSysById(map);//查询菜单
        if (CollectionUtil.isNotEmpty(bean)) {
            // 菜单存在
            if ("2".equals(bean.get("type").toString())) {
                // 要删除的菜单是菜单文件夹（菜单盒子）
                // 1. 删除自定义菜单下的所有菜单的关联关系
                sysEveUserCustomParentService.deleteByParentId(id);
                // 2. 删除自定义菜单文件夹下的所有自定义菜单
                sysEveUserCustomMenuService.deleteByParentId(id);
                // 3. 删除自定义文件夹
                sysEveUserCustomMenuboxService.deleteById(id);
            } else if ("3".equals(bean.get("type").toString())) {
                // 要删除的菜单是自定义菜单,直接删除
                sysEveUserCustomMenuService.deleteById(id);
            }
            // 桌面菜单列表
            List<Map<String, Object>> deskTops = sysEveUserDao.queryDeskTopsMenuByUserId(userId);
            deskTops = ToolUtil.listToTree(deskTops, "id", "parentId", "childs");
            jedisClient.set(ObjectConstant.getDeskTopsCacheKey(userId), JSONUtil.toJsonStr(deskTops));
        } else {
            outputObject.setreturnMessage("该菜单不存在，请刷新页面");
        }
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void editMenuParentIdById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String parentId = map.get("parentId").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        // 删除原有父级菜单的关联关系
        sysEveUserCustomParentService.deleteByMenuId(id, userId);
        SysEveUserCustomParent sysEveUserCustomParent = new SysEveUserCustomParent();
        sysEveUserCustomParent.setMenuId(id);
        sysEveUserCustomParent.setParentId(parentId);
        sysEveUserCustomParentService.createEntity(sysEveUserCustomParent, userId);
    }

    @Override
    public void queryMenuMationTypeById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = sysEveWinDragDropDao.queryMenuMationTypeById(map);
        outputObject.setBean(bean);
    }

    @Override
    public List<Map<String, Object>> queryCustomDeskTopsMenuByUserId(String userId) {
        return sysEveWinDragDropDao.queryCustomDeskTopsMenuByUserId(userId);
    }

}
