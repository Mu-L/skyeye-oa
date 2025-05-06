package com.skyeye.school.route.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.exception.CustomException;
import com.skyeye.school.building.service.TeachBuildingService;
import com.skyeye.school.route.dao.RoutesDao;
import com.skyeye.school.route.entity.RouteStop;
import com.skyeye.school.route.entity.Routes;
import com.skyeye.school.route.service.RouteStopService;
import com.skyeye.school.route.service.RoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
    private SchoolService schoolService;

    @Override
    public void validatorEntity(Routes entity) {
        super.validatorEntity(entity);
        if (entity.getStartId().equals(entity.getEndId())) {
            throw new CustomException("起始地点和终点地点不能相同");
        }
    }

    @Override
    public void deleteById(String id) {
        super.deleteById(id);
        QueryWrapper<RouteStop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), id);
        routeStopService.remove(queryWrapper);
    }

    @Override
    public Routes selectById(String id) {
        Routes routes = super.selectById(id);
        QueryWrapper<RouteStop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), id)
            .orderByAsc(MybatisPlusUtil.toColumns(RouteStop::getStopOrder));
        List<RouteStop> routeStops = routeStopService.list(queryWrapper);
        teachBuildingService.setDataMation(routes, Routes::getStartId);
        teachBuildingService.setDataMation(routes, Routes::getEndId);
        schoolService.setDataMation(routes, Routes::getSchoolMation);
        routes.setRouteStopList(routeStops);
        iAuthUserService.setName(routes, "createId", "createName");
        iAuthUserService.setName(routes, "lastUpdateId", "lastUpdateName");
        return routes;
    }

    @Override
    public void queryRoutesByStartAndEnd(InputObject inputObject, OutputObject outputObject) {
        Map params = inputObject.getParams();
        String startId = (String) params.get("startId");
        String endId = (String) params.get("endId");
        String schoolId = (String) params.get("schoolId");
        String typeId = (String) params.get("typeId");
        int routeType = CommonNumConstants.NUM_ONE;
        if (StrUtil.isNotEmpty(typeId)) {
            routeType = Integer.parseInt(typeId);
        }
        QueryWrapper<Routes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Routes::getStartId), startId)
            .eq(MybatisPlusUtil.toColumns(Routes::getEndId), endId)
            .eq(MybatisPlusUtil.toColumns(Routes::getSchoolId), schoolId)
            .eq(MybatisPlusUtil.toColumns(Routes::getEnabled), EnableEnum.ENABLE_USING.getKey())
            .eq(MybatisPlusUtil.toColumns(Routes::getRouteType), routeType)
            .orderByAsc(MybatisPlusUtil.toColumns(Routes::getRouteLength));
        List<Routes> bean = setBaseMation(queryWrapper);
        outputObject.setBeans(bean);
        outputObject.settotal(bean.size());
    }

    private List<Routes> setBaseMation(QueryWrapper<Routes> queryWrapper) {
        List<Routes> bean = list(queryWrapper);
        List<String> routeIds = bean.stream().map(Routes::getId).collect(Collectors.toList());
        Map<String, List<RouteStop>> routeStopListMap = routeStopService.queryStopListGroupByRoteIds(routeIds);
        for (Routes routes : bean) {
            List<RouteStop> routeStopList = routeStopListMap.get(routes.getId());
            routes.setRouteStopList(routeStopList);
        }
        teachBuildingService.setDataMation(bean, Routes::getStartId);
        teachBuildingService.setDataMation(bean, Routes::getEndId);
        iAuthUserService.setName(bean, "createId", "createName");
        iAuthUserService.setName(bean, "lastUpdateId", "lastUpdateName");
        return bean;
    }

    @Override
    public void queryPageListBySchoolId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String schoolId = commonPageInfo.getHolderId();
        String keyword = commonPageInfo.getKeyword();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Routes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Routes::getSchoolId), schoolId)
            .orderByDesc(MybatisPlusUtil.toColumns(Routes::getCreateTime));
        List<Routes> routes = setBaseMation(queryWrapper);
        if (StrUtil.isNotEmpty(keyword)) {
            routes = routes.stream()
                .filter(route -> route.getStartMation().get("name").toString().contains(keyword) ||
                    route.getEndMation().get("name").toString().contains(keyword))
                .collect(Collectors.toList());
        }
        schoolService.setDataMation(routes, Routes::getSchoolId);
        outputObject.setBeans(routes);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryRoutesNavigationLists(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String schoolId = (String) params.get("schoolId");
        String endId = (String) params.get("endId");
        String typeId = (String) params.get("typeId");
        int routeType = CommonNumConstants.NUM_ONE;
        if (StrUtil.isNotEmpty(typeId)) {
            routeType = Integer.parseInt(typeId);
        }
        double latitude = Double.parseDouble(params.get("latitude").toString());
        double longitude = Double.parseDouble(params.get("longitude").toString());
        QueryWrapper<Routes> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(MybatisPlusUtil.toColumns(Routes::getSchoolId), schoolId)
            .eq(MybatisPlusUtil.toColumns(Routes::getEndId), endId)
            .eq(MybatisPlusUtil.toColumns(Routes::getRouteType), routeType)
            .eq(MybatisPlusUtil.toColumns(Routes::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<Routes> routesList = list(queryWrapper);
        if (CollectionUtil.isEmpty(routesList)) {
            throw new CustomException("暂无去无改地点的路线");
        }
        Map<String, Double> map = new HashMap<>();
        for (Routes route : routesList) {
            QueryWrapper<RouteStop> routeStopQueryWrapper = new QueryWrapper<>();
            routeStopQueryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), route.getId());
            routeStopQueryWrapper.orderByAsc(MybatisPlusUtil.toColumns(RouteStop::getStopOrder));
            List<RouteStop> routeStopList = routeStopService.list(routeStopQueryWrapper);
            Double start = ToolUtil.haversine(latitude, longitude,
                Double.parseDouble(routeStopList.get(CommonNumConstants.NUM_ZERO).getLatitude()),
                Double.parseDouble(routeStopList.get(CommonNumConstants.NUM_ZERO).getLongitude()));
            Double end = ToolUtil.haversine(latitude, longitude,
                Double.parseDouble(routeStopList.get(routeStopList.size() - 1).getLatitude()),
                Double.parseDouble(routeStopList.get(routeStopList.size() - 1).getLongitude()));
            // 将当前位置作为停靠点的第一个点
            RouteStop routeStop = new RouteStop();
            routeStop.setLatitude(String.valueOf(latitude));
            routeStop.setLongitude(String.valueOf(longitude));
            routeStop.setStopOrder(CommonNumConstants.NUM_ZERO);
            routeStopList.add(CommonNumConstants.NUM_ZERO, routeStop);

            route.setRouteStopList(routeStopList);
            map.put(route.getId(), start + end);
        }
        if (map.size() <= CommonNumConstants.NUM_THREE) {
            schoolService.setDataMation(routesList, Routes::getSchoolId);
            outputObject.setBeans(routesList);
            outputObject.settotal(routesList.size());
        } else {
            // 根据距离排序
            List<Routes> beans = new ArrayList<>();
            List<Map.Entry<String, Double>> list = new ArrayList<>(map.entrySet());
            list.sort(Map.Entry.comparingByValue());
            for (Map.Entry<String, Double> entry : list) {
                for (Routes route : routesList) {
                    if (entry.getKey().equals(route.getId())) {
                        beans.add(route);
                        break;
                    }
                }
                if (beans.size() == CommonNumConstants.NUM_THREE) break;
            }
            schoolService.setDataMation(beans, Routes::getSchoolId);
            outputObject.setBeans(beans);
            outputObject.settotal(beans.size());
        }

    }

    @Override
    public void createPostpose(Routes entity, String userId) {
        Integer stopOrder = CommonNumConstants.NUM_ONE;
        List<RouteStop> routeStopList = entity.getRouteStopList();
        for (RouteStop routeStop : routeStopList) {
            routeStop.setRouteId(entity.getId());
            routeStop.setStopOrder(stopOrder);
            stopOrder++;
        }
        routeStopService.createEntity(routeStopList, userId);
    }

    @Override
    public void updatePostpose(Routes entity, String userId) {
        Integer stopOrder = CommonNumConstants.NUM_ONE;
        String routeId = entity.getId();
        List<RouteStop> routeStops = entity.getRouteStopList();
        QueryWrapper<RouteStop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), routeId);
        routeStopService.remove(queryWrapper);
        for (RouteStop routeStop : routeStops) {
            routeStop.setRouteId(routeId);
            routeStop.setStopOrder(stopOrder);
            stopOrder++;
        }
        routeStopService.createEntity(routeStops, userId);
    }
}
