package com.skyeye.school.measurement.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.SchoolConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.exception.CustomException;
import com.skyeye.school.chapter.service.ChapterService;
import com.skyeye.school.measurement.classnum.MeasurementTimeState;
import com.skyeye.school.measurement.dao.MeasurementDao;
import com.skyeye.school.measurement.entity.Measurement;
import com.skyeye.school.measurement.service.MeasurementService;
import com.skyeye.school.measurement.service.MeasurementSubService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: MeasurementServiceImpl
 * @Description: 测试管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 10:46
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "测试管理", groupName = "测试管理")
public class MeasurementServiceImpl extends SkyeyeBusinessServiceImpl<MeasurementDao, Measurement> implements MeasurementService {

    @Autowired
    private MeasurementSubService measurementSubService;

    @Override
    public List<String> queryMeasurementIdsBySubjectClassId(String id) {
        QueryWrapper<Measurement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Measurement::getSubjectClassesId), id);
        List<Measurement> list = list(queryWrapper);
        return list.stream().map(Measurement::getId).collect(Collectors.toList());
    }

}
