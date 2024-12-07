package com.skyeye.school.route.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.entity.School;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.eve.service.SchoolService;
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

import java.util.ArrayList;
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
    private TeachBuildingService teachBuildingService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private SchoolService schoolService;

    @Override
    @Transactional
    public void deleteById(String id) {
        super.deleteById(id);
        QueryWrapper<RouteStop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), id);
        routeStopService.remove(queryWrapper);
    }

    @Override
    public void selectById(InputObject inputObject, OutputObject outputObject) {
        Map params = inputObject.getParams();
        String id = (String) params.get("id");
        Routes routes = selectById(id);
        School schoolMation = schoolService.selectById(routes.getSchoolId());
        QueryWrapper<RouteStop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), id);
        List<RouteStop> routeStops = routeStopService.list(queryWrapper);
        routes.setStartMation(teachBuildingService.selectById(routes.getStartId()));
        routes.setEndMation(teachBuildingService.selectById(routes.getEndId()));
        routes.setSchoolMation(schoolMation);
        routes.setRouteStopList(routeStops);
        iAuthUserService.setName(routes,"createId","createName");
        iAuthUserService.setName(routes,"lastUpdateId","lastUpdateName");
        outputObject.setBean(routes);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryRoutesByStartAndEnd(InputObject inputObject, OutputObject outputObject) {
        Map params = inputObject.getParams();
        String startId = (String) params.get("startId");
        String endId = (String) params.get("endId");
        String schoolId = (String) params.get("schoolId");
        QueryWrapper<Routes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Routes::getStartId), startId)
                .eq(MybatisPlusUtil.toColumns(Routes::getEndId), endId)
                .eq(MybatisPlusUtil.toColumns(Routes::getSchoolId),schoolId)
                .orderByAsc(MybatisPlusUtil.toColumns(Routes::getRouteLength));
        List<Routes> bean = setBaseMation(queryWrapper);
        outputObject.setBeans(bean);
        outputObject.settotal(bean.size());
    }

    private List<Routes> setBaseMation(QueryWrapper<Routes> queryWrapper) {
        List<Routes> bean = list(queryWrapper);
        for (Routes routes : bean) {
            QueryWrapper<RouteStop> routeStopQueryWrapper = new QueryWrapper<>();
            routeStopQueryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), routes.getId())
                    .orderByAsc(MybatisPlusUtil.toColumns(RouteStop::getStopOrder));
            List<RouteStop> routeStopList = routeStopService.list(routeStopQueryWrapper);
            routes.setRouteStopList(routeStopList);
            routes.setStartMation(teachBuildingService.selectById(routes.getStartId()));
            routes.setEndMation(teachBuildingService.selectById(routes.getEndId()));
        }
        iAuthUserService.setName(bean,"createId","createName");
        iAuthUserService.setName(bean,"lastUpdateId","lastUpdateName");
        return bean;
    }

    @Override
    public void queryPageListBySchoolId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String schoolId = commonPageInfo.getHolderId();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Routes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Routes::getSchoolId), schoolId);
        List<Routes> routes = setBaseMation(queryWrapper);
        for (Routes route: routes){
            route.setSchoolMation(schoolService.selectById(route.getSchoolId()));
        }
        outputObject.setBeans(routes);
        outputObject.settotal(page.getTotal());
    }

    @Transactional
    @Override
    public void createPostpose(Routes entity, String userId) {
        List<RouteStop> routeStopList = entity.getRouteStopList();
        for (RouteStop routeStop : routeStopList) {
            routeStop.setRouteId(entity.getId());
        }
        routeStopService.createEntity(routeStopList, userId);
    }

    @Override
    public void updatePostpose(Routes entity, String userId) {
        String routeId = entity.getId();
        List<RouteStop> routeStops = entity.getRouteStopList();
        QueryWrapper<RouteStop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), routeId);
        routeStopService.remove(queryWrapper);
        for (RouteStop routeStop : routeStops) {
            routeStop.setRouteId(routeId);
        }
        routeStopService.createEntity(routeStops, userId);
    }
}
