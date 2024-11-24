package com.skyeye.school.building.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.school.building.dao.FloorInfoDao;
import com.skyeye.school.building.entity.FloorInfo;
import com.skyeye.school.building.service.FloorInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FloorInfoServiceImpl
 * @Description: 楼层教室服务管理控制层
 * @author: skyeye云系列--lqy
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "楼层教室服务管理", groupName = "楼层教室服务管理")
public class FloorInfoServiceImpl extends SkyeyeBusinessServiceImpl<FloorInfoDao, FloorInfo> implements FloorInfoService {


    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public void createPrepose(FloorInfo floorInfo) {
        Integer nodeType = floorInfo.getNodeType();
        if(nodeType == CommonNumConstants.NUM_ONE){
            floorInfo.setLevel(CommonNumConstants.NUM_ONE);
        }else if(nodeType == CommonNumConstants.NUM_TWO){
            floorInfo.setLevel(CommonNumConstants.NUM_TWO);
        }else if(nodeType == CommonNumConstants.NUM_THREE){
            floorInfo.setLevel(CommonNumConstants.NUM_THREE);
        }
    }

    @Override
    public void validatorEntity(FloorInfo floorInfo) {
        Integer nodeType = floorInfo.getNodeType();
        String name = floorInfo.getName();
        QueryWrapper<FloorInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getLocationId), floorInfo.getLocationId());
        if(nodeType == CommonNumConstants.NUM_ONE){
            // 新增、更新楼层
            queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getName), name);
            if(StrUtil.isEmpty(floorInfo.getId())&& count(queryWrapper) > 0){
                throw new CustomException("楼层名称已存在");
            }
            if(StrUtil.isNotEmpty(floorInfo.getId())){
                FloorInfo floor = selectById(floorInfo.getId());
                if(!name.equals(floor.getName()) && count(queryWrapper) > 0){
                    throw new CustomException("楼层名称已存在1");
                }
            }
        }else if(nodeType == CommonNumConstants.NUM_TWO){
            // 新增、更新教室
            queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getParentId), floorInfo.getParentId());
            queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getName), name);
            if(StrUtil.isEmpty(floorInfo.getId())&& count(queryWrapper) > 0){
                throw new CustomException("教室名已存在");
            }
            if(StrUtil.isNotEmpty(floorInfo.getId())){
                FloorInfo floor = selectById(floorInfo.getId());
                if(!name.equals(floor.getName()) && count(queryWrapper) > 0){
                    throw new CustomException("教室名已存在1");
                }
            }
        }
    }

    @Override
    public void deleteById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        FloorInfo floorInfo = selectById(id);
        Integer nodeType = floorInfo.getNodeType();
        // 删除服务--直接删除
        super.deleteById(id);
        if (nodeType == CommonNumConstants.NUM_TWO){
            // 删除教室--判断是否有服务，有则级联删除
            QueryWrapper<FloorInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getParentId), id);
            remove(queryWrapper);
        }else if(nodeType == CommonNumConstants.NUM_ONE){
            // 删除楼层--判断是否有教室，有则级联删除,删除服务
            QueryWrapper<FloorInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getParentId), id);
            List<FloorInfo> classroomList = list(queryWrapper);
            remove(queryWrapper);
            for(FloorInfo classroom : classroomList){
                // 删除服务
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getParentId), classroom.getId());
                remove(queryWrapper);
            }
        }
    }

    @Override
    public void queryFloorInfosByLocationId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String locationId = map.get("locationId").toString();
        if(StrUtil.isEmpty(locationId)){
            return;
        }
        String keyword = map.get("keyword").toString();
        QueryWrapper<FloorInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getLocationId), locationId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(FloorInfo::getSortOrder));
        if(StrUtil.isNotEmpty(keyword)){
            queryWrapper.like(MybatisPlusUtil.toColumns(FloorInfo::getName), keyword);
        }
        List<FloorInfo> list = list(queryWrapper);
        iAuthUserService.setName(list,"createId","createName");
        iAuthUserService.setName(list,"lastUpdateId","lastUpdateName");
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}
