/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.circleview.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.circle.entity.Circle;
import com.skyeye.circle.service.CircleService;
import com.skyeye.circleview.dao.CircleViewDao;
import com.skyeye.circleview.entity.CircleView;
import com.skyeye.circleview.service.CircleViewService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: CircleServiceImpl
 * @Description: 圈子服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "圈子浏览记录管理", groupName = "圈子浏览记录管理")
public class CircleViewServiceImpl extends SkyeyeBusinessServiceImpl<CircleViewDao, CircleView> implements CircleViewService {

    @Autowired
    private CircleService circleService;

    @Override
    public void queryMyCircleList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<CircleView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CircleView::getCreateId), userId);
        List<CircleView> circleViewList = list(queryWrapper);
        outputObject.setBeans(circleViewList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public String createEntity(CircleView circleView, String userId) {
        QueryWrapper<CircleView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CircleView::getCircleId), circleView.getCircleId())
            .eq(MybatisPlusUtil.toColumns(CircleView::getCreateId), userId);
        long count = count(queryWrapper);
        if (count > 0) {
            return StrUtil.EMPTY;
        }
        return super.createEntity(circleView, userId);
    }

    @Override
    public void validatorEntity(CircleView circleView) {
        Circle circle = circleService.selectById(circleView.getCircleId());
        if (StrUtil.isEmpty(circle.getId())) {
            throw new CustomException("圈子不存在");
        }
    }

    @Override
    public void createPrepose(CircleView circleView) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        circleView.setCreateId(userId);
    }

    @Override
    public void createPostpose(CircleView circleView, String userId) {
        QueryWrapper<CircleView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CircleView::getCircleId), circleView.getCircleId());
        long count = count(queryWrapper);
        circleService.updateViewNum(circleView.getCircleId(), (int) count);
    }

    @Override
    public void deletePreExecution(CircleView circleView) {
        Circle circle = circleService.selectById(circleView.getCircleId());
        if (ObjectUtil.isEmpty(circle)) {
            throw new CustomException("圈子不存在");
        }
    }

    @Override
    public void deletePostpose(CircleView circleView) {
        QueryWrapper<CircleView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CircleView::getCircleId), circleView.getCircleId());
        long count = count(queryWrapper);
        circleService.updateViewNum(circleView.getCircleId(), (int) count);
    }
}
