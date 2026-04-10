package com.skyeye.cost.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.cost.dao.ProUserCostDao;
import com.skyeye.cost.entity.ProUserCost;
import com.skyeye.cost.service.ProUserCostService;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.service.IDepmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ProUserCostServiceImpl
 * @Description: 人力成本管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:04
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "人力成本管理", groupName = "人力成本管理")
public class ProUserCostServiceImpl extends SkyeyeBusinessServiceImpl<ProUserCostDao, ProUserCost> implements ProUserCostService {


    @Autowired
    private IDepmentService iDepmentService;

    @Override
    protected void validatorEntity(ProUserCost entity) {
        super.validatorEntity(entity);
        double totalPrice = Double.parseDouble(entity.getTotalPrice());
        double manHours = Double.parseDouble(entity.getManHours());
        double workHours = Double.parseDouble(entity.getWorkHours());
        if (totalPrice < CommonNumConstants.NUM_ZERO || manHours < CommonNumConstants.NUM_ZERO || workHours < CommonNumConstants.NUM_ZERO) {
            throw new CustomException("金额、工时、时长不能小于0");
        }
    }

    @Override
    protected QueryWrapper<ProUserCost> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ProUserCost> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProUserCost::getProjectId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        List<String> ids = beans.stream().map(bean -> bean.get("userId").toString()).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMaps = iAuthUserService.queryUserMationListByStaffIds(ids);
        beans.forEach(bean -> {
            Map<String, Object> staffMap = staffMaps.get(bean.get("userId").toString());
            bean.put("userMation", staffMap);
        });
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        return beans;
    }

    @Override
    public ProUserCost selectById(String id) {
        ProUserCost proUserCost = super.selectById(id);
        List<String> ids = new ArrayList<>();
        ids.add(proUserCost.getUserId());
        Map<String, Map<String, Object>> staffMaps = iAuthUserService.queryUserMationListByStaffIds(ids);
        Map<String, Object> staffMap = staffMaps.get(proUserCost.getUserId());
        proUserCost.setUserMation(staffMap);
        iDepmentService.setDataMation(proUserCost, ProUserCost::getDepartmentId);
        return proUserCost;
    }

    @Override
    public List<Map<String, Object>> queryLastMonthHumanCost() {
        QueryWrapper<ProUserCost> queryWrapper = new QueryWrapper<>();
        //获取上个月日期
        String lastMonth = DateUtil.getLastMonthDate();
        queryWrapper.apply("DATE_FORMAT("+MybatisPlusUtil.toColumns(ProUserCost::getCreateTime)+", '%Y-%m') = {0}",lastMonth);
        queryWrapper.isNotNull(MybatisPlusUtil.toColumns(ProUserCost::getProjectId));
        List<ProUserCost> bean = list(queryWrapper);
        List<Map<String,Object>> result = new ArrayList<>();
        if(CollectionUtil.isEmpty(bean)){
            return result;
        }
        // 根据projectId分组
        Map<String, List<ProUserCost>> groupMap = bean.stream().collect(Collectors.groupingBy(ProUserCost::getProjectId));
        for (Map.Entry<String, List<ProUserCost>> entry : groupMap.entrySet()) {
            Map<String,Object> map = new HashMap<>();
            String price = String.valueOf(CommonNumConstants.NUM_ZERO);
            map.put("projectId",entry.getKey());
            for (ProUserCost proUserCost : entry.getValue()) {
                price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                        StrUtil.isEmpty(proUserCost.getTotalPrice()) ? "0" : proUserCost.getTotalPrice(),
                        price);
            }
            map.put("price",price);
            result.add(map);
        }
        return result;
    }
}
