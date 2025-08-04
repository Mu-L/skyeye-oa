package com.skyeye.school.route.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.route.entity.Routes;
import com.skyeye.school.route.service.RoutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: RouteController
 * @Description: 路线管理控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/18 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "路线管理", tags = "路线管理", modelName = "路线管理")
public class RouteController {

    @Autowired
    private RoutesService routeService;

    @ApiOperation(id = "writeRoute", value = "新增/编辑路线", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Routes.class)
    @RequestMapping("/post/RouteController/writeRoute")
    public void writeRoute(InputObject inputObject, OutputObject outputObject) {
        routeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRouteList", value = "除分页参数还需要holderId(schoolId)查询路线列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/RouteController/queryRouteList")
    public void queryRouteList(InputObject inputObject, OutputObject outputObject) {
        routeService.queryPageListBySchoolId(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteRouteById", value = "根据id删除路线", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "路线id", required = "required")
    })
    @RequestMapping("/post/RouteController/deleteRouteById")
    public void deleteRouteById(InputObject inputObject, OutputObject outputObject) {
        routeService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRouteById", value = "根据id查询路线信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "路线id", required = "required")
    })
    @RequestMapping("/post/RouteController/queryRouteById")
    public void queryRouteById(InputObject inputObject, OutputObject outputObject) {
        routeService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRoutesByStartAndEnd", value = "根据起点id--终点id 根据路线长度升序排序", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "startId", name = "startId", value = "起点id", required = "required"),
        @ApiImplicitParam(id = "endId", name = "endId", value = "终点id", required = "required"),
        @ApiImplicitParam(id = "schoolId", name = "schoolId", value = "学校id", required = "required"),
        @ApiImplicitParam(id = "typeId", name = "typeId", value = "路线类型")
    })
    @RequestMapping("/post/RouteController/queryRoutesByStartAndEnd")
    public void queryRoutesByStartAndEnd(InputObject inputObject, OutputObject outputObject) {
        routeService.queryRoutesByStartAndEnd(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRoutesNavigationLists", value = "根据当前位置到终点的导航路线查询前3条路线（<=3）", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "latitude", name = "latitude", value = "纬度", required = "required"),
        @ApiImplicitParam(id = "longitude", name = "longitude", value = "经度", required = "required"),
        @ApiImplicitParam(id = "endId", name = "endId", value = "终点id", required = "required"),
        @ApiImplicitParam(id = "schoolId", name = "schoolId", value = "学校id", required = "required"),
        @ApiImplicitParam(id = "typeId", name = "typeId", value = "路线类型")
    })
    @RequestMapping("/post/RouteController/queryRoutesNavigationLists")
    public void queryRoutesNavigationLists(InputObject inputObject, OutputObject outputObject) {
        routeService.queryRoutesNavigationLists(inputObject, outputObject);
    }
}
