/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.student.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.SchoolConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.rest.wall.certification.rest.ICertificationRest;
import com.skyeye.rest.wall.certification.service.ICertificationService;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.entity.SchoolStudentExcelModel;
import com.skyeye.school.entity.SchoolStudentGlobalExcelDictHandler;
import com.skyeye.school.faculty.service.FacultyService;
import com.skyeye.school.grade.entity.YearSystem;
import com.skyeye.school.grade.service.ClassesService;
import com.skyeye.school.grade.service.YearSystemService;
import com.skyeye.school.major.service.MajorService;
import com.skyeye.school.personnel.entity.SysEveUserStaff;
import com.skyeye.school.personnel.service.SysEveUserStaffService;
import com.skyeye.school.personnel.service.impl.SysEveUserStaffServiceImpl;
import com.skyeye.school.student.dao.StudentDao;
import com.skyeye.school.student.entity.Student;
import com.skyeye.school.student.service.StudentBodyMindService;
import com.skyeye.school.student.service.StudentFamilySituationService;
import com.skyeye.school.student.service.StudentParentsService;
import com.skyeye.school.student.service.StudentService;
import com.skyeye.school.subject.entity.Subject;
import com.skyeye.school.yearsub.service.YearSubjectService;
import org.apache.poi.ss.usermodel.Workbook;
import org.aspectj.weaver.ast.Var;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: StudentServiceImpl
 * @Description: 学生管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/9 9:52
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "学生管理", groupName = "学生管理")
public class StudentServiceImpl extends SkyeyeBusinessServiceImpl<StudentDao, Student> implements StudentService {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private YearSystemService yearSystemService;

    @Autowired
    private StudentBodyMindService studentBodyMindService;

    @Autowired
    private StudentFamilySituationService studentFamilySituationService;

    @Autowired
    private StudentParentsService studentParentsService;

    @Autowired
    private YearSubjectService yearSubjectService;

    @Autowired
    private ICertificationRest iCertificationRest;

    @Autowired
    private ICertificationService iCertificationService;

    @Autowired
    private SysEveUserStaffService sysEveUserStaffService;

    @Autowired
    private IUserService iUserService;




    private static final String EXPORT_EXCEL_NAME = "学生导入模板";

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        schoolService.setMationForMap(beans, "schoolId", "schoolMation");
        facultyService.setMationForMap(beans, "facultyId", "facultyMation");
        majorService.setMationForMap(beans, "majorId", "majorMation");
        classesService.setMationForMap(beans, "classId", "classMation");
        return beans;
    }

    @Override
    public void createPrepose(Student entity) {
        setBirthday(entity);
    }

    @Override
    public void updatePrepose(Student entity) {
        setBirthday(entity);
    }

    private void setBirthday(Student entity) {
        // 根据证件号码获取出生日期
        if (entity.getIdcardType() == 1) {
            // 居民身份证
            Map<String, String> stu = ToolUtil.getBirAgeSex(entity.getIdCard());
            entity.setBirthday(stu.get("birthday"));
        }
    }

    @Override
    public void writePostpose(Student entity, String userId) {
        super.writePostpose(entity, userId);
        // 保存学生身心障碍信息
        studentBodyMindService.saveLinkList(entity.getId(), entity.getStudentBodyMindList());
        // 保存学生家庭情况信息
        studentFamilySituationService.saveLinkList(entity.getId(), entity.getStudentFamilySituation());
        // 保存学生父母情况信息
        studentParentsService.saveLinkList(entity.getId(), entity.getStudentParents());
    }

    @Override
    public Student getDataFromDb(String id) {
        Student student = super.getDataFromDb(id);
        student.setStudentBodyMindList(studentBodyMindService.queryLinkListByStudentId(id));
        student.setStudentFamilySituation(studentFamilySituationService.queryLinkListByStudentId(id));
        student.setStudentParents(studentParentsService.queryLinkListByStudentId(id));
        return student;
    }

    @Override
    public Student selectById(String id) {
        Student student = super.selectById(id);
        schoolService.setDataMation(student, Student::getSchoolId);
        facultyService.setDataMation(student, Student::getFacultyId);
        majorService.setDataMation(student, Student::getMajorId);
        classesService.setDataMation(student, Student::getClassId);
        return student;
    }

    @Override
    public List<Student> selectByIds(String...ids){
        List<Student> studentList = super.selectByIds(ids);
        schoolService.setDataMation(studentList, Student::getSchoolId);
        facultyService.setDataMation(studentList, Student::getFacultyId);
        majorService.setDataMation(studentList, Student::getMajorId);
        classesService.setDataMation(studentList, Student::getClassId);
        return studentList;
    }

    /**
     * 导出学生模板
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void exportStudentModel(InputObject inputObject, OutputObject outputObject) {
        ExportParams exportParams = new ExportParams();
        exportParams.setTitle(EXPORT_EXCEL_NAME);
        exportParams.setSheetName("学生信息");
        exportParams.setDictHandler(new SchoolStudentGlobalExcelDictHandler());
        exportParams.setCreateHeadRows(true);
        exportParams.setHeaderHeight(500D);
        exportParams.setType(ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, SchoolStudentExcelModel.class, new ArrayList<>());
        try {
            ExcelUtil.fileWrite(workbook, PutObject.getResponse(), EXPORT_EXCEL_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryCurrentUserSubject(InputObject inputObject, OutputObject outputObject) {
        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        List<Map<String, Object>> result = null;
        if (StrUtil.equals(userIdentity, LoginIdentity.STUDENT.getKey())) {
            result = getStudentSubject(inputObject.getLogParams());
        }
        outputObject.setBeans(result);
        outputObject.settotal(CollectionUtil.size(result));
    }

    @Override
    public List<Student> getStudentListByClassesId(String classesId) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Student::getClassId), classesId);
        return list(queryWrapper);
    }
    @Override
    public void queryStudentListByNameOrNo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String name = map.get("name").toString();
        String no = map.get("no").toString();
        List<Map<String,Object>> mapList = new ArrayList<>();
        if (StrUtil.isNotEmpty(name)){
            List<Map<String, Object>> maps1 = iUserService.queryUserByRealNameOrStudentNumber(name, null);
            if (CollectionUtil.isNotEmpty(maps1)){
                mapList.addAll(maps1);
            }
        }
        if (StrUtil.isNotEmpty(no)){
            List<Map<String, Object>> maps2 = iUserService.queryUserByRealNameOrStudentNumber(null, no);
            if (CollectionUtil.isNotEmpty(maps2)){
                mapList.addAll(maps2);
            }
        }
        schoolService.setMationForMap(mapList, "schoolId", "schoolMation");
        facultyService.setMationForMap(mapList, "facultyId", "facultyMation");
        majorService.setMationForMap(mapList, "majorId", "majorMation");
        classesService.setMationForMap(mapList, "classId", "classMation");
        outputObject.setBeans(mapList);
        outputObject.settotal(mapList.size());
    }

    @Override
    public void queryTeacherListByName(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String name = map.get("name").toString();
        String number = map.get("jobNumber").toString();
        List<SysEveUserStaff> sysEveUserStaffList = sysEveUserStaffService.selectByName(name, number);
        outputObject.setBeans(sysEveUserStaffList);
        outputObject.settotal(sysEveUserStaffList.size());
    }

    @Override
    public void querySchoolStudentListByNo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String studentNumber = map.get("no").toString();
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Student::getNo),studentNumber);
        List<Student> studentList = list(queryWrapper);
        schoolService.setDataMation(studentList, Student::getSchoolId);
        facultyService.setDataMation(studentList, Student::getFacultyId);
        majorService.setDataMation(studentList, Student::getMajorId);
        classesService.setDataMation(studentList, Student::getClassId);
        outputObject.setBeans(studentList);
        outputObject.settotal(studentList.size());
    }

    private List<Map<String, Object>> getStudentSubject(Map<String, Object> studentMap) {
        Student student = JSONUtil.toBean(JSON.toJSONString(studentMap), Student.class);
        // 获取年制信息
        List<YearSystem> yearSystemList = yearSystemService.queryLinkListByClassId(student.getClassId());
        List<String> semesterList = yearSystemList.stream().map(YearSystem::getSemester).collect(Collectors.toList());
        // 获取每学年下的课程信息
        Map<String, List<Subject>> subjectMap = yearSubjectService.querySubjectList(student.getMajorId(), semesterList);
        // 设置值
        yearSystemList.forEach(yearSystem -> {
            yearSystem.setSubjectList(subjectMap.get(yearSystem.getSemester()));
        });
        return JSONUtil.toList(JSON.toJSONString(yearSystemList), null);
    }
}