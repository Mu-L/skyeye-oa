/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.building.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.school.building.dao.TeachBuildingDao;
import com.skyeye.school.building.entity.FloorInfo;
import com.skyeye.school.building.entity.TeachBuilding;
import com.skyeye.school.building.service.FloorInfoService;
import com.skyeye.school.building.service.TeachBuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
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
    private FloorInfoService floorInfoService;

    @Autowired
    private RedisCache redisCache;

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
        iAuthUserService.setName(building, "createId", "createName");
        iAuthUserService.setName(building, "lastUpdateId", "lastUpdateName");
        outputObject.setBean(building);
    }

    @Override
    protected void writePostpose(TeachBuilding entity, String userId) {
        super.writePostpose(entity, userId);
        // 删除缓存
        jedisClientService.del(getCacheKey(entity.getSchoolId()));
    }

    @Override
    protected void deletePostpose(TeachBuilding entity) {
        // 删除缓存
        jedisClientService.del(getCacheKey(entity.getSchoolId()));
    }

    @Override
    public void queryTeachBuildingBySchoolId(InputObject inputObject, OutputObject outputObject) {
        String schoolId = inputObject.getParams().get("schoolId").toString();
        if (StrUtil.isEmpty(schoolId)) {
            return;
        }
        String cacheKey = getCacheKey(schoolId);
        List<TeachBuilding> teachBuildingList = redisCache.getList(cacheKey, key -> {
            QueryWrapper<TeachBuilding> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(TeachBuilding::getSchoolId), schoolId);
            return list(queryWrapper);
        }, RedisConstants.A_YEAR_SECONDS, TeachBuilding.class);
        schoolService.setDataMation(teachBuildingList, TeachBuilding::getSchoolId);
        iAuthUserService.setName(teachBuildingList, "createId", "createName");
        iAuthUserService.setName(teachBuildingList, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(teachBuildingList);
        outputObject.settotal(teachBuildingList.size());
    }

    private String getCacheKey(String schoolId) {
        return String.format(Locale.ROOT, "school:teachBuilding:all:%s", schoolId);
    }

    @Override
    public void queryTeachBuildingByHolderId(InputObject inputObject, OutputObject outputObject) {
        Map params = inputObject.getParams();
        String schoolId = params.get("schoolId").toString();
        String typeId = params.get("typeId").toString();
        QueryWrapper<TeachBuilding> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TeachBuilding::getSchoolId), schoolId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TeachBuilding::getTypeId), typeId);
        List<TeachBuilding> teachBuildingList = list(queryWrapper);
        outputObject.setBeans(teachBuildingList);
        outputObject.settotal(teachBuildingList.size());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        deleteById(id);
        // 删除楼层、教室、服务
        QueryWrapper<FloorInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FloorInfo::getLocationId), id);
        floorInfoService.remove(queryWrapper);
    }

}
