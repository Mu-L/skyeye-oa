package com.skyeye.school.route.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.building.entity.TeachBuilding;
import com.skyeye.school.building.service.TeachBuildingService;
import com.skyeye.school.building.service.impl.TeachBuildingServiceImpl;
import com.skyeye.school.route.dao.RoutesDao;
import com.skyeye.school.route.entity.RouteStop;
import com.skyeye.school.route.entity.Routes;
import com.skyeye.school.route.service.RouteStopService;
import com.skyeye.school.route.service.RoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 * @ClassName: RouteServiceImpl
 * @Description: 路线服务管理实现类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "路线管理", groupName = "路线管理")
public class RouteServiceImpl extends SkyeyeBusinessServiceImpl<RoutesDao, Routes> implements RoutesService {

    @Autowired
    private RouteStopService routeStopService;

    @Autowired
    private IUserService userService;

    @Autowired
    private TeachBuildingService teachBuildingService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        userService.setMationForMap(beans, "createId", "createMation");
        return beans;
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        super.deleteById(id);
        QueryWrapper<RouteStop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), id);
        routeStopService.remove(queryWrapper);
    }

    @Override
    public void queryRoutesByStartAndEnd(InputObject inputObject, OutputObject outputObject) {
        Map params = inputObject.getParams();
        String startId = (String) params.get("startId");
        String endId = (String) params.get("endId");
        QueryWrapper<Routes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Routes::getStartId), startId)
                .eq(MybatisPlusUtil.toColumns(Routes::getEndId), endId)
                .orderByAsc(MybatisPlusUtil.toColumns(Routes::getRouteLength));
        List<Routes> bean = list(queryWrapper);
        for (Routes routes : bean) {
            QueryWrapper<RouteStop> routeStopQueryWrapper = new QueryWrapper<>();
            routeStopQueryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), routes.getId())
                    .orderByAsc(MybatisPlusUtil.toColumns(RouteStop::getStopOrder));
            List<RouteStop> routeStopList = routeStopService.list(routeStopQueryWrapper);
            routes.setRouteStopList(routeStopList);
        }
        outputObject.setBeans(bean);
        outputObject.settotal(bean.size());
    }

    @Override
    public void createPostpose(Routes entity, String userId) {
        String startName= teachBuildingService.selectById(entity.getStartId()).getName();
        String endName= teachBuildingService.selectById(entity.getEndId()).getName();
        entity.setStartName(startName);
        entity.setEndName(endName);
        List<RouteStop> routeStopList = entity.getRouteStopList();
        for (RouteStop routeStop : routeStopList) {
            routeStop.setRouteId(entity.getId());
        }
        routeStopService.createEntity(routeStopList, userId);
    }

    @Override
    protected void updatePrepose(Routes entity) {
        String startName= teachBuildingService.selectById(entity.getStartId()).getName();
        String endName= teachBuildingService.selectById(entity.getEndId()).getName();
        entity.setStartName(startName);
        entity.setEndName(endName);
    }

    @Override
    public void updatePostpose(Routes entity, String userId) {
        String routeId = entity.getId();
        List<RouteStop> routeStops = entity.getRouteStopList();
        QueryWrapper<RouteStop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), routeId);
        List<RouteStop> list = routeStopService.list(queryWrapper);
        for (RouteStop routeStop : list) {
            for (RouteStop middleRouteStop : routeStops) {
                if (routeStop.getStopOrder() == middleRouteStop.getStopOrder()) {
                    routeStop.setLatitude(middleRouteStop.getLatitude());
                    routeStop.setLongitude(middleRouteStop.getLongitude());
                    routeStopService.updateEntity(routeStop, userId);
                    break;
                }
            }
        }
    }
}
