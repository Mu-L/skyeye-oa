package com.skyeye.school.route.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.school.route.dao.RoutesDao;
import com.skyeye.school.route.entity.RouteStop;
import com.skyeye.school.route.entity.Routes;
import com.skyeye.school.route.service.RouteStopService;
import com.skyeye.school.route.service.RoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private SchoolService schoolService;

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
        schoolService.setDataMation(routes, Routes::getSchoolMation);
        routes.setRouteStopList(routeStops);
        iAuthUserService.setName(routes, "createId", "createName");
        iAuthUserService.setName(routes, "lastUpdateId", "lastUpdateName");
        return routes;
    }

    @Override
    protected QueryWrapper<Routes> getQueryWrapper(TableSelectInfo tableSelectInfo) {
        QueryWrapper<Routes> queryWrapper = super.getQueryWrapper(tableSelectInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Routes::getSchoolId), tableSelectInfo.getHolderId());
        return queryWrapper;
    }

    @Override
    protected List<Map<String, Object>> queryDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryDataList(inputObject);
        List<String> ids = beans.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        Map<String, List<RouteStop>> routeStopMap = routeStopService.queryStopListGroupByRoteIds(ids);
        for (Map<String, Object> bean : beans) {
            String id = bean.get("id").toString();
            List<RouteStop> routeStops = routeStopMap.get(id);
            bean.put("routeStopList", routeStops);
        }
        return beans;
    }

    @Override
    public void writeRouteStopList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        List<RouteStop> routeStopList = JSONUtil.toList(params.get("routeStopList").toString(), RouteStop.class);
        double totalLength = 0;
        for (int i = 0; i < routeStopList.size() - 1; i++) {
            RouteStop currentStop = routeStopList.get(i);
            RouteStop nextStop = routeStopList.get(i + 1);
            double distance = ToolUtil.haversine(
                Double.parseDouble(currentStop.getLatitude()),
                Double.parseDouble(currentStop.getLongitude()),
                Double.parseDouble(nextStop.getLatitude()),
                Double.parseDouble(nextStop.getLongitude())
            );
            totalLength += distance;
        }
        UpdateWrapper<Routes> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Routes::getRouteLength), totalLength);
        update(updateWrapper);

        // 删除原有路线站点信息
        QueryWrapper<RouteStop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(RouteStop::getRouteId), id);
        routeStopService.remove(queryWrapper);
        // 新增路线站点信息
        Integer stopOrder = CommonNumConstants.NUM_ONE;
        for (RouteStop routeStop : routeStopList) {
            routeStop.setRouteId(id);
            routeStop.setStopOrder(stopOrder);
            stopOrder++;
        }
        String userId = inputObject.getLogParams().get("id").toString();
        routeStopService.createEntity(routeStopList, userId);
        // 刷新缓存
        refreshCache(id);
    }
}
