/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.subject.entity.SubjectClassesStu;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SubjectClassesStuService
 * @Description: 科目表与班级表关系下的学生信息服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:19
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SubjectClassesStuService extends SkyeyeBusinessService<SubjectClassesStu> {

    void joinSubjectClasses(InputObject inputObject, OutputObject outputObject);

    void saveToClassStu(SubjectClassesStu subjectClassesStu, String studentNumber, boolean throwException);

    /**
     * 查询科目与班级的关联数据下有多少学生
     *
     * @param subClassLinkId
     * @return
     */
    Long queryClassStuNum(String... subClassLinkId);

    /**
     * 查询科目与班级的关联数据下学生信息列表
     *
     * @param subClassLinkId 科目与班级的关联id
     * @return
     */
    List<Map<String, Object>> queryClassStuIds(String... subClassLinkId);

    /**
     * 查询学生所属的科目与班级的关联数据
     *
     * @param stuNo 学生编号
     * @return
     */
    List<String> querySubClassLinkIdByStuNo(String stuNo);

    void deleteBySno(String subClassLinkId, String sno);

    void deleteBySubClassLinkId(List<String> subClassLinkId);

    void queryAllStudentById(InputObject inputObject, OutputObject outputObject);

    void deleteUserById(InputObject inputObject, OutputObject outputObject);

    void updateReward(String subClassesId, String Reward);

    void updateRewardNumberById(InputObject inputObject, OutputObject outputObject);

    void selectRewardList(InputObject inputObject, OutputObject outputObject);

    List<SubjectClassesStu> selectReward(String reward);

    void queryIdBysubClassLinkIdAndstuNo(InputObject inputObject, OutputObject outputObject);

    void queryStudentSubjectClassesBySubClassLinkIdAndStuNo(InputObject inputObject, OutputObject outputObject);

    void selectStudentList(InputObject inputObject, OutputObject outputObject);

    List<SubjectClassesStu> queryListBySubClassLinkId(String... subClassLinkId);

    List<SubjectClassesStu> selectNumBySubClassLinkId(String subjectClassId);

    List<SubjectClassesStu> queryListBySubClassLinkIds(List<String> subLinkIds);

    Map<String, String> queryStuStarNumBySubClassesId(String id, List<String> stuNumbers);

    List<Map<String, Object>> queryStuRewordList(String subjectClassId);

    SubjectClassesStu querySubClassLinkIdByStuNumberNo(String studentNumber);

    String queryRewordNumByStuNo(String studentNumber);
}
