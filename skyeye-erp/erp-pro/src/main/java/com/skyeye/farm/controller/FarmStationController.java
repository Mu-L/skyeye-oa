package com.skyeye.farm.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.farm.entity.FarmStation;
import com.skyeye.farm.service.FarmStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: FarmStationController
 * @Description: 车间工位管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 21:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "车间工位管理", tags = "车间工位管理", modelName = "车间工位管理")
public class FarmStationController {

    @Autowired
    private FarmStationService farmStationService;

    /**
     * 获取车间工位列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryFarmStationList", value = "获取车间工位列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class,
            value = {
                    @ApiImplicitParam(id = "objectId", name = "objectId", value = "工序信息的主键id")})
    @RequestMapping("/post/FarmStationController/queryFarmStationList")
    public void queryFarmStationList(InputObject inputObject, OutputObject outputObject) {
        farmStationService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑车间工位信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeFarmStation", value = "新增/编辑车间工位信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = FarmStation.class,
            value = {
                    @ApiImplicitParam(id = "name", name = "name", value = "工位名称", required = "required")})
    @RequestMapping("/post/FarmStationController/writeFarmStation")
    public void writeFarmStation(InputObject inputObject, OutputObject outputObject) {
        farmStationService.saveOrUpdateEntity(inputObject, outputObject);
    }


    /**
     * 根据id获取车间工位信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryFarmStationById", value = "根据id获取车间工位信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FarmStationController/queryFarmStationById")
    public void queryFarmStationById(InputObject inputObject, OutputObject outputObject) {
        farmStationService.selectById(inputObject, outputObject);
    }

    /**
     * 根据ID删除车间工位信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteFarmStationById", value = "根据ID删除车间工位信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FarmStationController/deleteFarmStationById")
    public void deleteFarmStationById(InputObject inputObject, OutputObject outputObject) {
        farmStationService.deleteById(inputObject, outputObject);
    }

}
