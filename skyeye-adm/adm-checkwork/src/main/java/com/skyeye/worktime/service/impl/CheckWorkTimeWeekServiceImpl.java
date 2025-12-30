/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.worktime.dao.CheckWorkTimeWeekDao;
import com.skyeye.worktime.entity.CheckWorkTimeWeek;
import com.skyeye.worktime.service.CheckWorkTimeWeekService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CheckWorkTimeWeekServiceImpl
 * @Description: 考勤班次里的具体时间服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/3 15:10
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "考勤班次里的具体时间", groupName = "考勤班次", manageShow = false)
public class CheckWorkTimeWeekServiceImpl extends SkyeyeBusinessServiceImpl<CheckWorkTimeWeekDao, CheckWorkTimeWeek> implements CheckWorkTimeWeekService {

    @Override
    public void saveCheckWorkTimeWeekList(String timeId, List<CheckWorkTimeWeek> beans, String userId) {
        deleteByTimeId(timeId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (CheckWorkTimeWeek farmProcedure : beans) {
                farmProcedure.setTimeId(timeId);
            }
            createEntity(beans, userId);
        }
    }

    @Override
    public void deleteByTimeId(String timeId) {
        QueryWrapper<CheckWorkTimeWeek> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CheckWorkTimeWeek::getTimeId), timeId);
        remove(queryWrapper);
    }

    @Override
    public List<CheckWorkTimeWeek> selectByTimeId(String timeId) {
        QueryWrapper<CheckWorkTimeWeek> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CheckWorkTimeWeek::getTimeId), timeId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(CheckWorkTimeWeek::getWeekNumber));
        List<CheckWorkTimeWeek> checkWorkTimeWeekList = list(queryWrapper);
        return checkWorkTimeWeekList;
    }

    @Override
    public Map<String, List<CheckWorkTimeWeek>> selectByTimeId(List<String> timeIds) {
        QueryWrapper<CheckWorkTimeWeek> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(CheckWorkTimeWeek::getTimeId), timeIds);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(CheckWorkTimeWeek::getWeekNumber));
        List<CheckWorkTimeWeek> checkWorkTimeWeekList = list(queryWrapper);
        return checkWorkTimeWeekList.stream().collect(Collectors.groupingBy(CheckWorkTimeWeek::getTimeId));
    }

}
