/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.menu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.menu.classenum.MenuType;
import com.skyeye.menu.dao.AppWorkPageDao;
import com.skyeye.menu.entity.AppWorkPage;
import com.skyeye.menu.service.AppWorkPageService;
import com.skyeye.win.service.SysEveDesktopService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AppWorkPageServiceImpl
 * @Description: 手机端菜单以及目录功能服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/10 23:18
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "手机端菜单管理", groupName = "菜单管理", tenant = TenantEnum.PLATE)
public class AppWorkPageServiceImpl extends SkyeyeBusinessServiceImpl<AppWorkPageDao, AppWorkPage> implements AppWorkPageService {

    @Autowired
    private SysEveDesktopService sysEveDesktopService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryAppWorkPageList(commonPageInfo);
        List<String> ids = beans.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return beans;
        }
        // 查询子节点信息(包含当前节点)
        List<String> childIds = skyeyeBaseMapper.queryAllChildIdsByParentId(ids);
        beans = selectMapByIds(childIds).values().stream().map(bean -> BeanUtil.beanToMap(bean)).collect(Collectors.toList());
        beans = beans.stream()
            .sorted(Comparator.comparing(bean -> Integer.parseInt(bean.get("orderBy").toString()))).collect(Collectors.toList());
        beans.forEach(bean -> {
            bean.put("lay_is_open", true);
        });
        return beans;
    }

    @Override
    protected void createPrepose(AppWorkPage entity) {
        if (StrUtil.isNotEmpty(entity.getUrl())) {
            entity.setType(MenuType.PAGE.getKey());
        } else {
            entity.setType(MenuType.FOLDER.getKey());
        }
    }

    @Override
    public AppWorkPage selectById(String id) {
        AppWorkPage appWorkPage = super.selectById(id);
        sysEveDesktopService.setDataMation(appWorkPage, AppWorkPage::getDesktopId);
        if (!appWorkPage.getParentId().equals("0")) {
            appWorkPage.setParentMation(selectById(appWorkPage.getParentId()));
        }
        return appWorkPage;
    }

    @Override
    public List<AppWorkPage> selectByIds(String... ids) {
        List<AppWorkPage> appWorkPages = super.selectByIds(ids);
        // 桌面信息
        sysEveDesktopService.setDataMation(appWorkPages, AppWorkPage::getDesktopId);
        return appWorkPages;
    }

    @Override
    public void deletePostpose(String id) {
        // 删除子菜单
        deleteByParentId(id);
    }

    private void deleteByParentId(String parentId) {
        QueryWrapper<AppWorkPage> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(AppWorkPage::getParentId), parentId);
        remove(queryWrapper);
    }

    /**
     * 根据父目录id获取子目录集合
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAppWorkPageListByDesktopId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String desktopId = map.get("desktopId").toString();
        if (StringUtils.isEmpty(desktopId)) {
            return;
        }
        QueryWrapper<AppWorkPage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppWorkPage::getDesktopId), desktopId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppWorkPage::getType), MenuType.FOLDER.getKey());
        List<AppWorkPage> appWorkPageMationList = list(queryWrapper);
        outputObject.setBeans(appWorkPageMationList);
        outputObject.settotal(appWorkPageMationList.size());
    }

}
