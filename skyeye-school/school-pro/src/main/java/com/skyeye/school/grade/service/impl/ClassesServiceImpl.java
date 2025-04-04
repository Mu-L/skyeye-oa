/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.grade.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.exception.CustomException;
import com.skyeye.school.faculty.service.FacultyService;
import com.skyeye.school.grade.dao.ClassesDao;
import com.skyeye.school.grade.entity.Classes;
import com.skyeye.school.grade.entity.YearSystem;
import com.skyeye.school.grade.service.ClassesService;
import com.skyeye.school.grade.service.YearSystemService;
import com.skyeye.school.major.entity.Major;
import com.skyeye.school.major.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName: ClassesServiceImpl
 * @Description: 班级信息管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "班级管理", groupName = "班级管理")
public class ClassesServiceImpl extends SkyeyeBusinessServiceImpl<ClassesDao, Classes> implements ClassesService {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private YearSystemService yearSystemService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        schoolService.setMationForMap(beans, "schoolId", "schoolMation");
        facultyService.setMationForMap(beans, "facultyId", "facultyMation");
        majorService.setMationForMap(beans, "majorId", "majorMation");
        iAuthUserService.setMationForMap(beans, "masterUserId", "masterUserMation");
        return beans;
    }

    @Override
    public void createPostpose(Classes entity, String userId) {
        Major major = majorService.selectById(entity.getMajorId());
        if (ObjectUtil.isEmpty(major)) {
            throw new CustomException("该专业数据不存在");
        }
        List<YearSystem> beans = new ArrayList<>();
        for (int i = 1; i <= major.getYear(); i++) {
            YearSystem yearSystem = new YearSystem();
            yearSystem.setClassId(entity.getId());
            String semester = String.format(Locale.ROOT, "%s-%s-%s", entity.getYear() + i - 1, entity.getYear() + i, 1);
            yearSystem.setSemester(semester);
            yearSystem.setYear(entity.getYear());
            yearSystem.setOrderBy(2 * i - 1);
            beans.add(yearSystem);

            YearSystem yearSystem1 = new YearSystem();
            yearSystem1.setClassId(entity.getId());
            String semester1 = String.format(Locale.ROOT, "%s-%s-%s", entity.getYear() + i - 1, entity.getYear() + i, 2);
            yearSystem1.setSemester(semester1);
            yearSystem1.setYear(entity.getYear());
            yearSystem1.setOrderBy(2 * i);
            beans.add(yearSystem1);
        }
        yearSystemService.createEntity(beans, userId);
    }

    @Override
    public Classes getDataFromDb(String id) {
        Classes classes = super.getDataFromDb(id);
        classes.setYearSystem(yearSystemService.queryLinkListByClassId(id));
        return classes;
    }

    @Override
    public List<Classes> getDataFromDb(List<String> idList) {
        List<Classes> classesList = super.getDataFromDb(idList);
        Map<String, List<YearSystem>> yearSystemMap = yearSystemService.queryLinkListByClassId(idList);
        classesList.forEach(classes -> {
            classes.setYearSystem(yearSystemMap.get(classes.getId()));
        });
        return classesList;
    }

    @Override
    public Classes selectById(String id) {
        Classes classes = super.selectById(id);
        schoolService.setDataMation(classes, Classes::getSchoolId);
        facultyService.setDataMation(classes, Classes::getFacultyId);
        majorService.setDataMation(classes, Classes::getMajorId);
        iAuthUserService.setDataMation(classes, Classes::getMasterUserId);
        return classes;
    }

    @Override
    public List<Classes> selectByIds(String... ids) {
        List<Classes> classesList = super.selectByIds(ids);
        schoolService.setDataMation(classesList, Classes::getSchoolId);
        facultyService.setDataMation(classesList, Classes::getFacultyId);
        majorService.setDataMation(classesList, Classes::getMajorId);
        iAuthUserService.setDataMation(classesList, Classes::getMasterUserId);
        return classesList;
    }

    @Override
    public void deletePostpose(String id) {
        yearSystemService.deleteLinkListByClassId(id);
    }

    /**
     * 根据专业id获取班级列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryClassListByMajorId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String majorId = map.get("majorId").toString();
        if (StrUtil.isEmpty(majorId)) {
            return;
        }
        QueryWrapper<Classes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Classes::getMajorId), majorId);
        List<Classes> ClassList = list(queryWrapper);
        outputObject.setBeans(ClassList);
        outputObject.settotal(ClassList.size());
    }

    @Override
    public List<Classes> queryClassListById(String classId) {
        QueryWrapper<Classes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, classId);
        List<Classes> classesList = list(queryWrapper);
        return classesList;
    }

    @Override
    public List<Classes> selectClssByIds(List<String> stringList) {
        QueryWrapper<Classes> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, stringList);
        return list(queryWrapper);
    }

}
