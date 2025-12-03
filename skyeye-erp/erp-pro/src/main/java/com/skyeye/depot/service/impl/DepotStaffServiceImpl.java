/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.dao.DepotStaffDao;
import com.skyeye.depot.entity.Depot;
import com.skyeye.depot.entity.DepotStaff;
import com.skyeye.depot.entity.DepotStaffVO;
import com.skyeye.depot.service.DepotStaffService;
import com.skyeye.depot.service.ErpDepotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: DepotStaffServiceImpl
 * @Description: 仓库与员工的关系服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 21:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "仓库与员工的关系管理", groupName = "仓库与员工的关系管理", manageShow = false)
public class DepotStaffServiceImpl extends SkyeyeBusinessServiceImpl<DepotStaffDao, DepotStaff> implements DepotStaffService {

    @Autowired
    private ErpDepotService erpDepotService;

    @Override
    public QueryWrapper<DepotStaff> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DepotStaff> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotStaff::getDepotId), commonPageInfo.getObjectId());
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
    public void insertDepotStaff(InputObject inputObject, OutputObject outputObject) {
        DepotStaffVO depotStaffVO = inputObject.getParams(DepotStaffVO.class);
        Depot depot = erpDepotService.selectById(depotStaffVO.getDepotId());
        if (ObjectUtil.isEmpty(depot) || StrUtil.isEmpty(depot.getId())) {
            throw new IllegalArgumentException("仓库不存在");
        }

        QueryWrapper<DepotStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotStaff::getDepotId), depotStaffVO.getDepotId());
        List<DepotStaff> list = list(queryWrapper);
        List<String> DepotStaffIdList = list.stream().map(DepotStaff::getStaffId).collect(Collectors.toList());

        List<DepotStaff> beans = new ArrayList<>();
        String userId = inputObject.getLogParams().get("id").toString();
        for (String str : depotStaffVO.getStaffId()) {
            if (DepotStaffIdList.contains(str)) {
                // 如果该仓库已经存在这个员工，则跳过
                continue;
            }
            if (StrUtil.isNotEmpty(str)) {
                DepotStaff item = new DepotStaff();
                item.setDepotId(depotStaffVO.getDepotId());
                item.setStaffId(str);
                beans.add(item);
            }
        }
        if (CollectionUtil.isNotEmpty(beans)) {
            createEntity(beans, userId);
        }
    }

    @Override
    public void queryStaffBelongDepotList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String enabled = params.get("enabled").toString();

        String staffId = inputObject.getLogParams().get("staffId").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        List<Depot> DepotList = getDepotListByStaffId(staffId, userId, enabled);
        outputObject.setBeans(DepotList);
        outputObject.settotal(DepotList.size());
    }

    private List<Depot> getDepotListByStaffId(String staffId, String userId, String enabled) {
        // 1. 查询员工所属的仓库
        QueryWrapper<DepotStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotStaff::getStaffId), staffId);
        List<DepotStaff> list = list(queryWrapper);
        // 获取仓库id
        List<String> depotIdList = list.stream().map(DepotStaff::getDepotId).collect(Collectors.toList());
        // 查询仓库信息
        List<Depot> depotList = erpDepotService.selectByIds(depotIdList.toArray(new String[]{}));
        // 2. 查询当前用户所负责的仓库
        List<Depot> chargeDepotList = erpDepotService.queryDepotListByChargePerson(userId, enabled);
        // 3. 合并仓库信息
        depotList.addAll(chargeDepotList);
        // 4. 去重
        depotList = depotList.stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toMap(
                    Depot::getId, // 使用id作为key
                    Function.identity(), // 使用原始对象作为value
                    (existing, replacement) -> existing), // 当键冲突时，保留现有的条目
                map -> new ArrayList<>(map.values()))); // 将Map的值转换回List
        return depotList;
    }

    @Override
    public void deleteDepotStaffByStaffId(InputObject inputObject, OutputObject outputObject) {
        String staffId = inputObject.getParams().get("id").toString();
        QueryWrapper<DepotStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotStaff::getStaffId), staffId);
        remove(queryWrapper);
    }
}
