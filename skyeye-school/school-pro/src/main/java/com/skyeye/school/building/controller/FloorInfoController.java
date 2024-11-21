package com.skyeye.school.building.controller;


import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.building.entity.FloorInfo;
import com.skyeye.school.building.service.FloorInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "楼层教室服务管理", tags = "楼层教室服务管理", modelName = "楼层教室服务管理")
public class FloorInfoController {

    @Autowired
    private FloorInfoService floorInfoService;

    @ApiOperation(id = "writeOrUpdateFloorInfo", value = "新增/编辑楼层、教室、服务", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = FloorInfo.class)
    @RequestMapping("/post/FloorInfoController/writeOrUpdateFloorInfo")
    public void writeOrUpdateFloorInfo(InputObject inputObject, OutputObject outputObject) {
        floorInfoService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteFloorInfoById", value = "根据id删除楼层、教室、服务", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id= "id",name = "id", value = "主键id", required = "required")
    })
    @RequestMapping("/post/FloorInfoController/deleteFloorInfoById")
    public void deleteFloorInfoById(InputObject inputObject, OutputObject outputObject) {
        floorInfoService.deleteById(inputObject, outputObject);
    }


    @ApiOperation(id = "queryFloorInfosByLocationId", value = "根据地点id获取楼层所有信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id= "locationId",name = "locationId", value = "地点id", required = "required"),
        @ApiImplicitParam(id= "keyword",name = "keyword", value = "关键字查询")
    })
    @RequestMapping("/post/FloorInfoController/queryFloorInfosByLocationId")
    public void queryFloorInfosByLocationId(InputObject inputObject, OutputObject outputObject) {
        floorInfoService.queryFloorInfosByLocationId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFloorInfoById", value = "根据id查询楼层、教室、服务", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id= "id",name = "id", value = "主键id", required = "required")
    })
    @RequestMapping("/post/FloorInfoController/queryFloorInfoById")
    public void queryFloorInfoById(InputObject inputObject, OutputObject outputObject) {
        floorInfoService.selectById(inputObject, outputObject);
    }
}
