/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.SchoolConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.eve.entity.School;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.certification.service.ICertificationService;
import com.skyeye.school.semester.entity.Semester;
import com.skyeye.school.subject.dao.SubjectDao;
import com.skyeye.school.subject.entity.Subject;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import com.skyeye.school.subject.service.SubjectClassesTopService;
import com.skyeye.school.subject.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: StudentServiceImpl
 * @Description: 科目管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/9 9:52
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "科目管理", groupName = "科目管理")
public class SubjectServiceImpl extends SkyeyeBusinessServiceImpl<SubjectDao, Subject> implements SubjectService {

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private ICertificationService iCertificationService;

    @Autowired
    private SubjectClassesService subjectClassesService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private SubjectClassesTopService subjectClassesTopService;

    @Override
    public void validatorEntity(Subject entity) {
        super.validatorEntity(entity);
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Subject::getName), entity.getName())
            .eq(MybatisPlusUtil.toColumns(Subject::getCreateId), userId);
        long count = count(queryWrapper);
        // name 同时作唯一性约束
        if (StrUtil.isEmpty(entity.getId()) && count > CommonNumConstants.NUM_ZERO) {
            throw new CustomException("课程名称已存在");
        }
        if (StrUtil.isNotEmpty(entity.getId())) {
            Subject subject = selectById(entity.getId());
            if (!subject.getName().equals(entity.getName()) && count > CommonNumConstants.NUM_ZERO) {
                throw new CustomException("课程名称已存在");
            }
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Subject::getNo), entity.getNo())
            .eq(MybatisPlusUtil.toColumns(Subject::getCreateId), userId);
        long countNo = count(queryWrapper);
        // no 同时作唯一性约束
        if (StrUtil.isEmpty(entity.getId()) && countNo > CommonNumConstants.NUM_ZERO) {
            throw new CustomException("课程编号已存在");
        }
        if (StrUtil.isNotEmpty(entity.getId())) {
            Subject subject = selectById(entity.getId());
            if (!subject.getNo().equals(entity.getNo()) && countNo > CommonNumConstants.NUM_ZERO) {
                throw new CustomException("课程编号已存在");
            }
        }
    }

    @Override
    public void createPrepose(Subject entity) {
        entity.setOwnerId(InputObject.getLogParamsStatic().get("id").toString());
    }

    @Override
    public Subject selectById(String id) {
        Subject subject = super.selectById(id);
        schoolService.setDataMation(subject, Subject::getSchoolId);
        return subject;
    }

    @Override
    public void deletePostpose(String id) {
        // 删除所有人对该科目的置顶信息
        subjectClassesTopService.deleteSubjectClassesTopBySubjectId(id);
        // 删除所有班级对该科目的关联信息
        subjectClassesService.deleteBySubjectId(id);
    }

    @Override
    public List<Subject> selectByIds(String... ids) {
        List<Subject> subjectList = super.selectByIds(ids);
        schoolService.setDataMation(subjectList, Subject::getSchoolId);
        return subjectList;
    }

    @Override
    public void querySubjectListByUserId(InputObject inputObject, OutputObject outputObject) {
        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        List<SubjectClasses> subjectClassesList = null;
        List<Semester> semesterList = new ArrayList<>();
        if (StrUtil.equals(userIdentity, LoginIdentity.TEACHER.getKey())) {
            // 教师身份信息
            // 查询当前用户创建的科目
            List<Subject> subjectList = querySubjectListByUserId(userId);
            if (CollectionUtil.isEmpty(subjectList)) {
                return;
            }
            // 获取所有学校信息
            List<School> schoolList = schoolService.queryAllData();
            Map<String, School> schoolMap = schoolList.stream().collect(Collectors.toMap(School::getId, school -> school));
            // 按学期分组
            List<String> ids = subjectList.stream().map(Subject::getId).collect(Collectors.toList());
            subjectClassesList = subjectClassesService.querySubjectClassesByObjectId(ids.toArray(new String[]{}));
            Map<String, List<SubjectClasses>> collect = subjectClassesList.stream().collect(Collectors.groupingBy(SubjectClasses::getSemesterId));
            collect.forEach((key, value) -> {
                Semester semester = new Semester();
                semester.setId(key);
                semester.setName(value.get(CommonNumConstants.NUM_ZERO).getSemesterMation().getName());
                List<Subject> subjects = value.stream().map(SubjectClasses::getObjectMation).collect(Collectors.toList());
                // 根据id进行去重
                subjects = new ArrayList<>(subjects.stream()
                    .collect(Collectors.toMap(Subject::getId, Function.identity(), (existing, replacement) -> existing))
                    .values());
                subjects.forEach(subject -> {
                    subject.setSchoolMation(schoolMap.get(subject.getSchoolId()));
                });
                semester.setSubjectList(subjects);
                semesterList.add(semester);
            });
            outputObject.setBeans(semesterList);
            outputObject.settotal(semesterList.size());
        } else if (StrUtil.equals(userIdentity, LoginIdentity.STUDENT.getKey())) {
            // 学生身份信息
            Map<String, Object> certification = iCertificationService.queryCertificationById(userId);
            String studentNumber = certification.get("studentNumber").toString();
            List<String> subClassLinkIdList = subjectClassesStuService.querySubClassLinkIdByStuNo(studentNumber);
            if (CollectionUtil.isNotEmpty(subClassLinkIdList)) {
                subjectClassesList = subjectClassesService.selectByIds(subClassLinkIdList.toArray(new String[]{}));
            }
            // 学生按照学期分组
            List<String> semesterIdList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(subjectClassesList)) {
                subjectClassesList.forEach(subjectClasses -> {
                    if (!semesterIdList.contains(subjectClasses.getSemesterId())) {
                        Semester semester = new Semester();
                        semester.setId(subjectClasses.getSemesterId());
                        semester.setName(subjectClasses.getSemesterMation().getName());
                        semesterList.add(semester);
                        semesterIdList.add(subjectClasses.getSemesterId());
                    }
                });
                // 按学期分组
                for (Semester semester : semesterList) {
                    List<SubjectClasses> subjectClassesListBySemester = subjectClassesList.stream()
                        .filter(subjectClasses -> subjectClasses.getSemesterId().equals(semester.getId()))
                        .collect(Collectors.toList());
                    semester.setSubjectClassesList(subjectClassesListBySemester);
                }
            }
        }
        if (CollectionUtil.isEmpty(semesterList)){
            outputObject.setBean(new ArrayList<>());
        }
        outputObject.setBeans(semesterList);
        outputObject.settotal(semesterList.size());
    }

    @Override
    public void querySubjectListByMajorId(InputObject inputObject, OutputObject outputObject) {
        String majorId = inputObject.getParams().get("majorId").toString();
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Subject::getMajorId), majorId);
        List<Subject> list = list(queryWrapper);
        iAuthUserService.setDataMation(list, Subject::getCreateId);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void searchSubjectList(InputObject inputObject, OutputObject outputObject) {
        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        String currentUserId = InputObject.getLogParamsStatic().get("id").toString();
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = null;
        List<Map<String, Object>> beans = null;
        if (StrUtil.equals(userIdentity, LoginIdentity.TEACHER.getKey())) {
            // 开启分页
            setCommonPageInfoOtherInfo(commonPageInfo);
            pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
            // 教师查询
            QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
            if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
                queryWrapper.like(MybatisPlusUtil.toColumns(Subject::getName), commonPageInfo.getKeyword());
            }
            queryWrapper.eq(MybatisPlusUtil.toColumns(Subject::getCreateId), currentUserId)
                .orderByDesc(MybatisPlusUtil.toColumns(Subject::getCreateTime));
            List<Subject> list = list(queryWrapper);
            schoolService.setDataMation(list, Subject::getSchoolId);
            beans = JSONUtil.toList(JSONUtil.toJsonStr(list), null);
        } else if (StrUtil.equals(userIdentity, LoginIdentity.STUDENT.getKey())) {
            // 学生身份信息
            // 查学号
            Map<String, Object> certification = iCertificationService.queryCertificationById(currentUserId);
            String studentNumber = certification.get("studentNumber").toString();
            // 查科目与班级关联id
            List<String> subClassLinkIdList = subjectClassesStuService.querySubClassLinkIdByStuNo(studentNumber);
            // 开启分页
            setCommonPageInfoOtherInfo(commonPageInfo);
            pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
            List<SubjectClasses> subjectClasses = subjectClassesService.selectByIds(subClassLinkIdList.toArray(new String[]{}));
            List<String> subjectIdList = subjectClasses.stream().map(SubjectClasses::getObjectId).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(subjectIdList)) {
                return;
            }
            QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(CommonConstants.ID, subjectIdList)
                .orderByDesc(MybatisPlusUtil.toColumns(Subject::getCreateTime));
            if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
                queryWrapper.like(MybatisPlusUtil.toColumns(Subject::getName), commonPageInfo.getKeyword());
            }
            List<Subject> list = list(queryWrapper);
            Map<String, Subject> idSubjectMap = list.stream().collect(Collectors.toMap(Subject::getId, subject -> subject));
            List<SubjectClasses> flagBeans = subjectClasses.stream().filter(subjectClasses1 -> idSubjectMap.containsKey(subjectClasses1.getObjectId())).collect(Collectors.toList());
            for (SubjectClasses flagBean : flagBeans) {
                flagBean.setObjectMation(idSubjectMap.get(flagBean.getObjectId()));
            }
            beans = JSONUtil.toList(JSONUtil.toJsonStr(flagBeans), null);
        }
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        for (Map<String, Object> bean : beans) {
            bean.put("serviceClassName", getServiceClassName());
        }
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void queryMySubjectListOnly(InputObject inputObject, OutputObject outputObject) {
        String currentUserId = InputObject.getLogParamsStatic().get("id").toString();
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = null;
        // 开启分页
        setCommonPageInfoOtherInfo(commonPageInfo);
        pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 教师查询
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Subject::getCreateId), currentUserId)
            .orderByDesc(MybatisPlusUtil.toColumns(Subject::getCreateTime));
        List<Subject> beans = list(queryWrapper);
        schoolService.setDataMation(beans, Subject::getSchoolId);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    private List<Subject> querySubjectListByUserId(String userId) {
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Subject::getCreateId), userId);
        return list(queryWrapper);
    }

}
