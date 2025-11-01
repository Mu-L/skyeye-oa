package com.skyeye.school.building.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.building.dao.LocationRangeDao;
import com.skyeye.school.building.entity.LocationRange;
import com.skyeye.school.building.entity.RangeVertex;
import com.skyeye.school.building.service.LocationRangeService;
import com.skyeye.school.building.service.RangeVertexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: LocationRangeServiceImpl
 * @Description: 地点范围实现类
 * @author: skyeye云系列--，lqy
 * @date: 2023/9/5 17:15
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "地点范围管理", groupName = "地点范围管理")
public class LocationRangeServiceImpl extends SkyeyeBusinessServiceImpl<LocationRangeDao, LocationRange> implements LocationRangeService {

    @Autowired
    private RangeVertexService rangeVertexService;

    @Override
    public void queryLocationRangeList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<LocationRange> queryWrapper = new QueryWrapper<>();
        List<LocationRange> bean = list(queryWrapper);
        for (LocationRange locationRange : bean) {
            String rangeId = locationRange.getId();
            QueryWrapper<RangeVertex> vertexQueryWrapper = new QueryWrapper<>();
            vertexQueryWrapper.eq(MybatisPlusUtil.toColumns(RangeVertex::getRangeId), rangeId);
            vertexQueryWrapper.orderByAsc(MybatisPlusUtil.toColumns(RangeVertex::getOrderNum));
            List<RangeVertex> vertexList = rangeVertexService.list(vertexQueryWrapper);
            locationRange.setRangeVertexMation(vertexList);
        }
        outputObject.setBeans(bean);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public LocationRange selectById(String id) {
        LocationRange locationRange = super.selectById(id);
        String rangeId = locationRange.getId();
        QueryWrapper<RangeVertex> vertexQueryWrapper = new QueryWrapper<>();
        vertexQueryWrapper.eq(MybatisPlusUtil.toColumns(RangeVertex::getRangeId), rangeId);
        vertexQueryWrapper.orderByAsc(MybatisPlusUtil.toColumns(RangeVertex::getOrderNum));
        List<RangeVertex> vertexList = rangeVertexService.list(vertexQueryWrapper);
        locationRange.setRangeVertexMation(vertexList);
        return locationRange;
    }

    @Override
    public void deleteById(String id) {
        super.deleteById(id);
        QueryWrapper<RangeVertex> vertexQueryWrapper = new QueryWrapper<>();
        vertexQueryWrapper.eq(MybatisPlusUtil.toColumns(RangeVertex::getRangeId), id);
        rangeVertexService.remove(vertexQueryWrapper);
    }

    @Override
    public void createPostpose(LocationRange locationRange, String userId) {
        String rangeId = locationRange.getId();
        List<RangeVertex> rangeVertexMation = locationRange.getRangeVertexMation();
        for (RangeVertex rangeVertex : rangeVertexMation) {
            rangeVertex.setRangeId(rangeId);
        }
        rangeVertexService.createEntity(rangeVertexMation, userId);
    }

}
