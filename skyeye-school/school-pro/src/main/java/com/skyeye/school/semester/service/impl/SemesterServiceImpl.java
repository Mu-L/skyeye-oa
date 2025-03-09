/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.semester.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.semester.dao.SemesterDao;
import com.skyeye.school.semester.entity.Semester;
import com.skyeye.school.semester.service.SemesterService;
import org.jsoup.helper.DataUtil;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: SemesterServiceImpl
 * @Description: 学期管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/29 10:52
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "学期管理", groupName = "学期管理")
public class SemesterServiceImpl extends SkyeyeBusinessServiceImpl<SemesterDao, Semester> implements SemesterService {

    @Override
    protected void validatorEntity(Semester entity) {
        super.validatorEntity(entity);
        String startTime = entity.getStartTime();
        String endTime = entity.getEndTime();
        // 转换为日期格式
        LocalDate start = LocalDate.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate end = LocalDate.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE);
        // 结束时间早于开始时间，则compare为true
        boolean compare = end.isBefore(start);
        // 开始时间不能晚于结束时间
        if(compare){
            throw new CustomException("开始时间不能晚于结束时间");
        }
    }

    @Override
    public void queryAllSemesterList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<Semester> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Semester::getStartTime));
        List<Semester> semesterList = list(queryWrapper);
        outputObject.setBeans(semesterList);
        outputObject.settotal(semesterList.size());
    }

}
