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
        // 根据路线中的途径点信息计算总长度
        List<RouteStop> routeStops = entity.getRouteStopList();
        double totalLength = 0;
        for (int i = 0; i < routeStops.size() - 1; i++) {
            RouteStop currentStop = routeStops.get(i);
            RouteStop nextStop = routeStops.get(i + 1);
            double distance = ToolUtil.haversine(
                Double.parseDouble(currentStop.getLatitude()),
                Double.parseDouble(currentStop.getLongitude()),
                Double.parseDouble(nextStop.getLatitude()),
                Double.parseDouble(nextStop.getLongitude())
            );
            totalLength += distance;
        }
        entity.setRouteLength(totalLength);
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
            .and(wrapper ->
                wrapper.eq(MybatisPlusUtil.toColumns(Routes::getEndId), endId)
                    .or().eq(MybatisPlusUtil.toColumns(Routes::getStartId), endId))
            .eq(MybatisPlusUtil.toColumns(Routes::getRouteType), routeType)
            .eq(MybatisPlusUtil.toColumns(Routes::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<Routes> routesList = list(queryWrapper);
        if (CollectionUtil.isEmpty(routesList)) {
            throw new CustomException("暂无去该地点的路线");
        }

        // 获取路线ID列表
        List<String> routeIds = routesList.stream().map(Routes::getId).collect(Collectors.toList());

        // 获取所有路线的站点信息
        Map<String, List<RouteStop>> routeStopListMap = routeStopService.queryStopListGroupByRoteIds(routeIds);

        // 获取终点地点信息
        teachBuildingService.setDataMation(routesList, Routes::getEndId);

        // 计算每条路线的最优路径并排序
        List<RouteRecommendation> recommendations = new ArrayList<>();

        for (Routes route : routesList) {
            List<RouteStop> stops = routeStopListMap.get(route.getId());
            if (CollectionUtil.isEmpty(stops)) {
                continue;
            }

            // 计算从当前位置到该路线的最短路径
            RoutePathInfo optimalPath = calculateOptimalPath(latitude, longitude, stops);

            if (optimalPath == null) {
                continue;
            }

            // 计算综合评分
            double score = calculateRouteScore(optimalPath.getTotalDistance(), optimalPath.getOptimizedStops().size(), route.getRouteType(), optimalPath.getDistanceToFirstStop());

            // 创建推荐对象
            RouteRecommendation recommendation = new RouteRecommendation();
            recommendation.setRoute(route);
            recommendation.setStops(optimalPath.getOptimizedStops());
            recommendation.setDistanceToFirstStop(optimalPath.getDistanceToFirstStop());
            recommendation.setTotalDistance(optimalPath.getTotalDistance());
            recommendation.setRouteDistance(optimalPath.getRouteDistance());
            recommendation.setScore(score);
            recommendation.setStopCount(optimalPath.getOptimizedStops().size());
            recommendation.setStartStopIndex(optimalPath.getStartStopIndex());

            recommendations.add(recommendation);
        }

        // 按综合评分排序，取前三个最优路线
        recommendations.sort((r1, r2) -> Double.compare(r2.getScore(), r1.getScore())); // 评分越高越好

        List<RouteRecommendation> topThreeRoutes = recommendations.stream()
            .limit(3)
            .collect(Collectors.toList());

        // 设置路线信息
        for (RouteRecommendation rec : topThreeRoutes) {
            Routes route = rec.getRoute();
            route.setRouteStopList(rec.getStops());
            teachBuildingService.setDataMation(route, Routes::getStartId);
            iAuthUserService.setName(route, "createId", "createName");
            iAuthUserService.setName(route, "lastUpdateId", "lastUpdateName");
        }

        // 构建返回结果
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < topThreeRoutes.size(); i++) {
            RouteRecommendation rec = topThreeRoutes.get(i);
            Map<String, Object> routeInfo = new HashMap<>();
            routeInfo.put("route", rec.getRoute());
            routeInfo.put("rank", i + 1);
            routeInfo.put("distanceToFirstStop", Math.round(rec.getDistanceToFirstStop() * 100.0) / 100.0);
            routeInfo.put("routeDistance", rec.getRouteDistance());
            routeInfo.put("totalDistance", Math.round(rec.getTotalDistance() * 100.0) / 100.0);
            routeInfo.put("score", Math.round(rec.getScore() * 100.0) / 100.0);
            routeInfo.put("stopCount", rec.getStopCount());
            routeInfo.put("startStopIndex", rec.getStartStopIndex());
            routeInfo.put("recommendationReason", getRecommendationReason(rec, i + 1));
            resultList.add(routeInfo);
        }

        outputObject.setBeans(resultList);
        outputObject.settotal(resultList.size());
    }

    /**
     * 计算路线综合评分
     *
     * @param totalDistance       总距离
     * @param stopCount           站点数量
     * @param routeType           路线类型
     * @param distanceToFirstStop 到第一个站点的距离
     * @return 综合评分
     */
    private double calculateRouteScore(double totalDistance, int stopCount, Integer routeType, double distanceToFirstStop) {
        // 基础评分（距离越短评分越高）
        double distanceScore = 100.0 / (1 + totalDistance);

        // 站点数量评分（站点越少评分越高，避免过多转乘）
        double stopScore = 100.0 / (1 + stopCount * 0.5);

        // 到第一个站点的距离评分（距离越近评分越高）
        double firstStopScore = 100.0 / (1 + distanceToFirstStop * 2);

        // 路线类型评分（可以根据实际需求调整）
        double typeScore = 100.0;
        if (routeType != null) {
            switch (routeType) {
                case 1: // 步行路线
                    typeScore = 90.0;
                    break;
                case 2: // 自行车路线
                    typeScore = 85.0;
                    break;
                case 3: // 公交路线
                    typeScore = 80.0;
                    break;
                default:
                    typeScore = 70.0;
            }
        }

        // 综合评分权重
        double finalScore = distanceScore * 0.4 + stopScore * 0.3 + firstStopScore * 0.2 + typeScore * 0.1;

        return finalScore;
    }

    /**
     * 获取推荐理由
     *
     * @param recommendation 路线推荐
     * @param rank           排名
     * @return 推荐理由
     */
    private String getRecommendationReason(RouteRecommendation recommendation, int rank) {
        List<String> reasons = new ArrayList<>();

        if (rank == 1) {
            reasons.add("综合评分最高");
        }

        // 根据起始站点位置给出建议
        if (recommendation.getStartStopIndex() == 0) {
            reasons.add("从路线起点开始");
        } else if (recommendation.getStartStopIndex() > 0) {
            reasons.add("从路线中途站点开始");
        }

        if (recommendation.getDistanceToFirstStop() < 0.3) {
            reasons.add("距离路线很近");
        } else if (recommendation.getDistanceToFirstStop() < 0.8) {
            reasons.add("距离路线较近");
        }

        if (recommendation.getStopCount() <= 3) {
            reasons.add("站点较少，路径简洁");
        } else if (recommendation.getStopCount() <= 5) {
            reasons.add("站点适中");
        }

        if (recommendation.getTotalDistance() < 1.5) {
            reasons.add("总距离很短");
        } else if (recommendation.getTotalDistance() < 3.0) {
            reasons.add("总距离较短");
        }

        // 根据路线类型给出建议
        if (recommendation.getRoute().getRouteType() != null) {
            switch (recommendation.getRoute().getRouteType()) {
                case 1:
                    reasons.add("步行路线，健康环保");
                    break;
                case 2:
                    reasons.add("自行车路线，快速便捷");
                    break;
                case 3:
                    reasons.add("公交路线，舒适省力");
                    break;
            }
        }

        if (reasons.isEmpty()) {
            reasons.add("路线合理");
        }

        return String.join("，", reasons);
    }

    /**
     * 路线推荐内部类
     */
    private static class RouteRecommendation {
        private Routes route;
        private List<RouteStop> stops;
        private double distanceToFirstStop;
        private double totalDistance;
        private double routeDistance;
        private double score;
        private int stopCount;
        private int startStopIndex; // 新增：记录最优起始站点在原始列表中的索引

        // Getters and Setters
        public Routes getRoute() {
            return route;
        }

        public void setRoute(Routes route) {
            this.route = route;
        }

        public List<RouteStop> getStops() {
            return stops;
        }

        public void setStops(List<RouteStop> stops) {
            this.stops = stops;
        }

        public double getDistanceToFirstStop() {
            return distanceToFirstStop;
        }

        public void setDistanceToFirstStop(double distanceToFirstStop) {
            this.distanceToFirstStop = distanceToFirstStop;
        }

        public double getTotalDistance() {
            return totalDistance;
        }

        public void setTotalDistance(double totalDistance) {
            this.totalDistance = totalDistance;
        }

        public double getRouteDistance() {
            return routeDistance;
        }

        public void setRouteDistance(double routeDistance) {
            this.routeDistance = routeDistance;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getStopCount() {
            return stopCount;
        }

        public void setStopCount(int stopCount) {
            this.stopCount = stopCount;
        }

        public int getStartStopIndex() {
            return startStopIndex;
        }

        public void setStartStopIndex(int startStopIndex) {
            this.startStopIndex = startStopIndex;
        }
    }

    /**
     * 路线路径信息内部类
     */
    private static class RoutePathInfo {
        private double totalDistance;
        private double distanceToFirstStop;
        private double routeDistance;
        private List<RouteStop> optimizedStops;
        private int startStopIndex;

        // Getters and Setters
        public double getTotalDistance() {
            return totalDistance;
        }

        public void setTotalDistance(double totalDistance) {
            this.totalDistance = totalDistance;
        }

        public double getDistanceToFirstStop() {
            return distanceToFirstStop;
        }

        public void setDistanceToFirstStop(double distanceToFirstStop) {
            this.distanceToFirstStop = distanceToFirstStop;
        }

        public double getRouteDistance() {
            return routeDistance;
        }

        public void setRouteDistance(double routeDistance) {
            this.routeDistance = routeDistance;
        }

        public List<RouteStop> getOptimizedStops() {
            return optimizedStops;
        }

        public void setOptimizedStops(List<RouteStop> optimizedStops) {
            this.optimizedStops = optimizedStops;
        }

        public int getStartStopIndex() {
            return startStopIndex;
        }

        public void setStartStopIndex(int startStopIndex) {
            this.startStopIndex = startStopIndex;
        }
    }

    /**
     * 计算从当前位置到路线的最优路径
     *
     * @param currentLat 当前位置纬度
     * @param currentLng 当前位置经度
     * @param stops      路线站点列表
     * @return 最优路径信息
     */
    private RoutePathInfo calculateOptimalPath(double currentLat, double currentLng, List<RouteStop> stops) {
        if (CollectionUtil.isEmpty(stops)) {
            return null;
        }

        double minTotalDistance = Double.MAX_VALUE;
        int optimalStartIndex = 0;
        List<RouteStop> optimizedStops = new ArrayList<>();

        // 遍历所有可能的起始站点
        for (int startIndex = 0; startIndex < stops.size(); startIndex++) {
            RouteStop startStop = stops.get(startIndex);

            // 计算从当前位置到起始站点的距离
            double distanceToStart = ToolUtil.haversine(
                currentLat, currentLng,
                Double.parseDouble(startStop.getLatitude()),
                Double.parseDouble(startStop.getLongitude())
            );

            // 计算从起始站点到终点的路线距离
            double routeDistance = calculateRouteDistanceFromIndex(stops, startIndex);

            double totalDistance = distanceToStart + routeDistance;

            // 如果这条路径更短，更新最优路径
            if (totalDistance < minTotalDistance) {
                minTotalDistance = totalDistance;
                optimalStartIndex = startIndex;

                // 优化站点列表：从起始站点开始，过滤掉不必要的途径点
                optimizedStops = optimizeStops(stops, startIndex);
            }
        }

        if (minTotalDistance == Double.MAX_VALUE) {
            return null;
        }

        RoutePathInfo pathInfo = new RoutePathInfo();
        pathInfo.setTotalDistance(minTotalDistance);
        pathInfo.setDistanceToFirstStop(ToolUtil.haversine(
            currentLat, currentLng,
            Double.parseDouble(stops.get(optimalStartIndex).getLatitude()),
            Double.parseDouble(stops.get(optimalStartIndex).getLongitude())
        ));
        pathInfo.setRouteDistance(calculateRouteDistanceFromIndex(stops, optimalStartIndex));
        pathInfo.setOptimizedStops(optimizedStops);
        pathInfo.setStartStopIndex(optimalStartIndex);

        return pathInfo;
    }

    /**
     * 计算从指定索引开始的路线距离
     *
     * @param stops      站点列表
     * @param startIndex 起始索引
     * @return 路线距离
     */
    private double calculateRouteDistanceFromIndex(List<RouteStop> stops, int startIndex) {
        double distance = 0;
        for (int i = startIndex; i < stops.size() - 1; i++) {
            RouteStop current = stops.get(i);
            RouteStop next = stops.get(i + 1);
            distance += ToolUtil.haversine(
                Double.parseDouble(current.getLatitude()),
                Double.parseDouble(current.getLongitude()),
                Double.parseDouble(next.getLatitude()),
                Double.parseDouble(next.getLongitude())
            );
        }
        return distance;
    }

    /**
     * 优化站点列表，过滤掉不必要的途径点
     *
     * @param stops      原始站点列表
     * @param startIndex 起始索引
     * @return 优化后的站点列表
     */
    private List<RouteStop> optimizeStops(List<RouteStop> stops, int startIndex) {
        List<RouteStop> optimized = new ArrayList<>();

        // 添加起始站点
        optimized.add(stops.get(startIndex));

        // 从起始站点开始，选择关键站点（避免过多的小站点）
        for (int i = startIndex + 1; i < stops.size(); i++) {
            RouteStop current = stops.get(i);

            // 如果是最后一个站点，或者距离上一个站点足够远，则保留
            if (i == stops.size() - 1 || isSignificantStop(stops, i)) {
                optimized.add(current);
            }
        }

        return optimized;
    }

    /**
     * 判断是否为重要站点（距离上一个站点足够远）
     *
     * @param stops 站点列表
     * @param index 当前站点索引
     * @return 是否为重要站点
     */
    private boolean isSignificantStop(List<RouteStop> stops, int index) {
        if (index <= 0) {
            return true;
        }

        RouteStop previous = stops.get(index - 1);
        RouteStop current = stops.get(index);

        double distance = ToolUtil.haversine(
            Double.parseDouble(previous.getLatitude()),
            Double.parseDouble(previous.getLongitude()),
            Double.parseDouble(current.getLatitude()),
            Double.parseDouble(current.getLongitude())
        );

        // 如果距离超过100米，认为是重要站点
        return distance > 0.1;
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
