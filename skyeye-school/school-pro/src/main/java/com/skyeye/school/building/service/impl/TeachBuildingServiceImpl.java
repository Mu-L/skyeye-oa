/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.building.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.exception.CustomException;
import com.skyeye.school.building.dao.TeachBuildingDao;
import com.skyeye.school.building.entity.Classroom;
import com.skyeye.school.building.entity.FloorInfo;
import com.skyeye.school.building.entity.TeachBuilding;
import com.skyeye.school.building.service.ClassroomService;
import com.skyeye.school.building.service.FloorInfoService;
import com.skyeye.school.building.service.TeachBuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: TeachBuildingServiceImpl
 * @Description: 地点管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/8/7 20:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "地点管理", groupName = "地点管理")
public class TeachBuildingServiceImpl extends SkyeyeBusinessServiceImpl<TeachBuildingDao, TeachBuilding> implements TeachBuildingService {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private FloorInfoService floorInfoService;


    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> bean = super.queryPageDataList(inputObject);
        schoolService.setMationForMap(bean, "schoolId", "schoolMation");
        return bean;
    }

    @Override
    public void selectById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        TeachBuilding building = selectById(id);
        iAuthUserService.setName(building,"createId", "createName");
        iAuthUserService.setName(building,"lastUpdateId", "lastUpdateName");
        outputObject.setBean(building);
    }

    @Override
    public void queryTeachBuildingBySchoolId(InputObject inputObject, OutputObject outputObject) {
        String schoolId = inputObject.getParams().get("schoolId").toString();
        QueryWrapper<TeachBuilding> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TeachBuilding::getSchoolId), schoolId);
        List<TeachBuilding> teachBuildingList = list(queryWrapper);
        schoolService.setDataMation(teachBuildingList,TeachBuilding::getSchoolId);
        iAuthUserService.setName(teachBuildingList,"createId","createName");
        iAuthUserService.setName(teachBuildingList,"lastUpdateId","lastUpdateName");
        outputObject.setBeans(teachBuildingList);
        outputObject.settotal(teachBuildingList.size());
    }


    @Override
    public void queryTeachBuildingByHolderId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String typeId = commonPageInfo.getHolderId();
        if(StringUtils.isEmpty(typeId)){
            throw new CustomException("地点分类id不能为空");
        }
        if(commonPageInfo.getIsPaging()){
            Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
            List<TeachBuilding> teachBuildingList = getTeachBuildings(typeId);
            outputObject.setBeans(teachBuildingList);
            outputObject.settotal(page.getTotal());
        }else {
            List<TeachBuilding> teachBuildingList = getTeachBuildings(typeId);
            outputObject.setBeans(teachBuildingList);
            outputObject.settotal(teachBuildingList.size());
        }

    }

    private List<TeachBuilding> getTeachBuildings(String typeId) {
        QueryWrapper<TeachBuilding> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TeachBuilding::getTypeId), typeId);
        List<TeachBuilding> teachBuildingList = list(queryWrapper);
        schoolService.setDataMation(teachBuildingList,TeachBuilding::getSchoolId);
        return teachBuildingList;
    }

    @Transactional
    @Override
    public void deleteById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        deleteById(id);
        // 删除楼层、教室、服务
        QueryWrapper<FloorInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getLocationId), id);
        floorInfoService.remove(queryWrapper);
    }

}
