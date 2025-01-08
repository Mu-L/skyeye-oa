/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.SchoolConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.certification.service.ICertificationService;
import com.skyeye.school.semester.entity.Semester;
import com.skyeye.school.subject.dao.SubjectDao;
import com.skyeye.school.subject.entity.Subject;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import com.skyeye.school.subject.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Override
    public QueryWrapper<Subject> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Subject> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // 我创建的
        queryWrapper.eq(MybatisPlusUtil.toColumns(Subject::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public void createPrepose(Subject entity) {
        entity.setOwnerId(InputObject.getLogParamsStatic().get("id").toString());
    }

    @Override
    public Subject selectById(String id) {
        Subject subject = super.selectById(id);
        return subject;
    }

    @Override
    public List<Subject> selectByIds(String... ids) {
        List<Subject> subjectList = super.selectByIds(ids);
        return subjectList;
    }

    @Override
    public void querySubjectListByUserId(InputObject inputObject, OutputObject outputObject) {
        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        List<SubjectClasses> subjectClassesList = null;
        if (StrUtil.equals(userIdentity, LoginIdentity.TEACHER.getKey())) {
            // 教师身份信息
            // 查询当前用户创建的科目
            List<Subject> subjectList = querySubjectListByUserId(userId);
            if (CollectionUtil.isEmpty(subjectList)) {
                throw new CustomException("当前用户没有创建科目");
            }
            List<String> ids = subjectList.stream().map(Subject::getId).collect(Collectors.toList());
            subjectClassesList = subjectClassesService.querySubjectClassesByObjectId(ids.toArray(new String[]{}));
        } else if (StrUtil.equals(userIdentity, LoginIdentity.STUDENT.getKey())) {
            // 学生身份信息
            Map<String, Object> certification = iCertificationService.queryCertificationById(userId);
            String studentNumber = certification.get("studentNumber").toString();
            List<String> subClassLinkIdList = subjectClassesStuService.querySubClassLinkIdByStuNo(studentNumber);
            if (CollectionUtil.isEmpty(subClassLinkIdList)) {
                throw new CustomException("当前学生没有加入任何科目");
            }
            subjectClassesList = subjectClassesService.selectByIds(subClassLinkIdList.toArray(new String[]{}));
        }
        if (CollectionUtil.isEmpty(subjectClassesList)) {
            throw new CustomException("当前用户没有创建或加入任何科目");
        }
        List<Semester> semesterList = new ArrayList<>();
        List<String> semesterIdList = new ArrayList<>();
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
        outputObject.setBeans(semesterList);
        outputObject.settotal(semesterList.size());
    }

    @Override
    public void querySubjectListByMajorId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String majorId = commonPageInfo.getHolderId();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Subject::getMajorId), majorId);
        List<Subject> list = list(queryWrapper);
        iAuthUserService.setDataMation(list,Subject::getCreateId);
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    private List<Subject> querySubjectListByUserId(String userId) {
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Subject::getCreateId), userId);
        return list(queryWrapper);
    }

}
