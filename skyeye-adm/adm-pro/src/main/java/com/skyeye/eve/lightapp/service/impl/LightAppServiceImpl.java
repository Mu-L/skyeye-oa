/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.lightapp.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DataCommonUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.lightapp.dao.LightAppDao;
import com.skyeye.eve.lightapp.entity.LightApp;
import com.skyeye.eve.lightapp.service.LightAppService;
import com.skyeye.eve.lightapp.service.LightAppTypeService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.pro.win.SysEveWinDragDropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: LightAppServiceImpl
 * @Description: 轻应用管理服务类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:54
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "轻应用管理", groupName = "轻应用管理")
public class LightAppServiceImpl extends SkyeyeBusinessServiceImpl<LightAppDao, LightApp> implements LightAppService {

    @Autowired
    private SysEveWinDragDropService sysEveWinDragDropService;

    @Autowired
    private LightAppTypeService lightAppTypeService;

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        lightAppTypeService.setMationForMap(beans, "typeId", "typeMation");
        return beans;
    }

    /**
     * 获取启用的轻应用列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryLightAppUpList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String typeId = map.get("typeId").toString();
        QueryWrapper<LightApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LightApp::getEnabled), EnableEnum.ENABLE_USING.getKey());
        if (StrUtil.isNotEmpty(typeId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(LightApp::getTypeId), typeId);
        }
        List<LightApp> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public LightApp selectById(String id) {
        LightApp lightApp = super.selectById(id);
        lightAppTypeService.setDataMation(lightApp, LightApp::getTypeId);
        return lightApp;
    }

    /**
     * 添加轻应用到桌面
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertLightAppToWin(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        LightApp lightApp = selectById(id);
        if (ObjectUtil.isEmpty(lightApp)) {
            throw new CustomException("该应用不存在，无法进行添加！");
        }

        if (EnableEnum.ENABLE_USING.getKey().equals(lightApp.getEnabled())) {
            // 启用状态可以添加
            map.put("menuName", lightApp.getName());
            map.put("menuNameEn", lightApp.getName());
            map.put("menuIconType", 2);
            map.put("menuIconPic", lightApp.getLogo());
            map.put("menuUrl", lightApp.getName());
            map.put("desktopId", "winfixedpage00000000");
            map.put("lightAppId", id);
            DataCommonUtil.setCommonData(map, inputObject.getLogParams().get("id").toString());
            ExecuteFeignClient.get(() -> sysEveWinDragDropService.insertWinCustomMenu(map));
            outputObject.setBean(map);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

}
