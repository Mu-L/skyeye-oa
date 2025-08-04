package com.skyeye.cost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.cost.dao.ProUserCostDao;
import com.skyeye.cost.entity.ProUserCost;
import com.skyeye.cost.service.ProUserCostService;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.service.IDepmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
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
}
