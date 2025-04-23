package com.skyeye.school.route.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.route.dao.RouteStopDao;
import com.skyeye.school.route.entity.RouteStop;
import com.skyeye.school.route.service.RouteStopService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: RouteStopServiceImpl
 * @Description: 路线站点服务管理实现类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/18 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "路线站点服务", groupName = "路线站点服务")
public class RouteStopServiceImpl extends SkyeyeBusinessServiceImpl<RouteStopDao, RouteStop> implements RouteStopService {
    @Override
    public Map<String, List<RouteStop>> queryStopListGroupByRoteIds(List<String> routeIds) {
        if(CollectionUtil.isEmpty(routeIds)){
            return Collections.emptyMap();
        }
        QueryWrapper<RouteStop> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(RouteStop::getRouteId), routeIds);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(RouteStop::getStopOrder));
        List<RouteStop> routeStops = list(queryWrapper);
        return routeStops.stream().collect(Collectors.groupingBy(RouteStop::getRouteId));
    }
}
