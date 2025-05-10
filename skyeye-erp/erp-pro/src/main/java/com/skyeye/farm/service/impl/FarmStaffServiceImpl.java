/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.farm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.farm.dao.FarmStaffDao;
import com.skyeye.farm.entity.Farm;
import com.skyeye.farm.entity.FarmStaff;
import com.skyeye.farm.entity.FarmStaffVO;
import com.skyeye.farm.service.FarmService;
import com.skyeye.farm.service.FarmStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: FarmStaffServiceImpl
 * @Description: 车间与员工的关系服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 21:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "车间与员工的关系管理", groupName = "车间与员工的关系管理", manageShow = false)
public class FarmStaffServiceImpl extends SkyeyeBusinessServiceImpl<FarmStaffDao, FarmStaff> implements FarmStaffService {

    @Autowired
    private FarmService farmService;

    @Override
    public QueryWrapper<FarmStaff> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<FarmStaff> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(FarmStaff::getFarmId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.isEmpty(commonPageInfo.getObjectId())) {
            return CollectionUtil.newArrayList();
        }
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置员工信息
        List<String> staffIds = beans.stream().map(bean -> bean.get("staffId").toString())
            .filter(staffId -> StrUtil.isNotEmpty(staffId)).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        beans.forEach(bean -> {
            String staffId = bean.get("staffId").toString();
            bean.put("staffMation", staffMap.get(staffId));
        });
        return beans;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertFarmStaff(InputObject inputObject, OutputObject outputObject) {
        FarmStaffVO farmStaffVO = inputObject.getParams(FarmStaffVO.class);

        QueryWrapper<FarmStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FarmStaff::getFarmId), farmStaffVO.getFarmId());
        List<FarmStaff> list = list(queryWrapper);
        List<String> farmStaffIdList = list.stream().map(FarmStaff::getStaffId).collect(Collectors.toList());

        List<FarmStaff> beans = new ArrayList<>();
        String userId = inputObject.getLogParams().get("id").toString();
        for (String str : farmStaffVO.getStaffId()) {
            if (farmStaffIdList.contains(str)) {
                // 如果该车间已经存在这个员工，则跳过
                continue;
            }
            if (StrUtil.isNotEmpty(str)) {
                FarmStaff item = new FarmStaff();
                item.setFarmId(farmStaffVO.getFarmId());
                item.setStaffId(str);
                beans.add(item);
            }
        }
        if (CollectionUtil.isNotEmpty(beans)) {
            createEntity(beans, userId);
        }
    }

    @Override
    public void queryStaffBelongFarmList(InputObject inputObject, OutputObject outputObject) {
        String staffId = inputObject.getLogParams().get("staffId").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        List<Farm> farmList = getFarmListByStaffId(staffId, userId);
        outputObject.setBeans(farmList);
        outputObject.settotal(farmList.size());
    }

    private List<Farm> getFarmListByStaffId(String staffId, String userId) {
        // 1. 查询员工所属的车间
        QueryWrapper<FarmStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FarmStaff::getStaffId), staffId);
        List<FarmStaff> list = list(queryWrapper);
        // 获取车间id
        List<String> farmIdList = list.stream().map(FarmStaff::getFarmId).collect(Collectors.toList());
        // 查询车间信息
        List<Farm> farmList = farmService.selectByIds(farmIdList.toArray(new String[]{}));
        // 2. 查询当前用户所负责的车间
        List<Farm> chargeFarmList = farmService.queryFarmListByChargePerson(userId);
        // 3. 合并车间信息
        farmList.addAll(chargeFarmList);
        // 4. 去重
        farmList = farmList.stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toMap(
                    Farm::getId, // 使用id作为key
                    Function.identity(), // 使用原始对象作为value
                    (existing, replacement) -> existing), // 当键冲突时，保留现有的条目
                map -> new ArrayList<>(map.values()))); // 将Map的值转换回List
        return farmList;
    }

    @Override
    public void deleteFarmStaffByStaffId(InputObject inputObject, OutputObject outputObject) {
        String staffId = inputObject.getParams().get("id").toString();
        QueryWrapper<FarmStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FarmStaff::getStaffId), staffId);
        remove(queryWrapper);
    }

    @Override
    public void queryAllFarmStaffList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<FarmStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(FarmStaff::getCreateTime));
        List<FarmStaff> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}
