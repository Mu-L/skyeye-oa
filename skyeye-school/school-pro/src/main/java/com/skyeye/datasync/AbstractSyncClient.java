/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.datasync;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AbstractSyncClient
 * @Description: 同步客户端配置接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/7/2 8:38
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
public abstract class AbstractSyncClient {

    public AbstractSyncClient() {
    }

    /**
     * 获取学校信息
     *
     * @return 学校信息
     */
    protected abstract Map<String, Object> getSchool();

    /**
     * 同步学校信息
     *
     * @return 同步结果
     */
    public abstract boolean syncSchool();

    /**
     * 获取院系信息
     *
     * @return 院系信息列表
     */
    protected abstract List<Map<String, Object>> getFaculties();

    /**
     * 同步院系信息
     *
     * @return 同步结果
     */
    public abstract boolean syncFaculties();

    /**
     * 获取专业信息
     *
     * @param facultyId 院系ID
     * @return 专业信息列表
     */
    protected abstract List<Map<String, Object>> getMajors(String facultyId);

    /**
     * 同步专业信息
     *
     * @return 同步结果
     */
    public abstract boolean syncMajors();

    /**
     * 获取班级信息
     *
     * @param majorId 专业ID
     * @return 班级信息列表
     */
    protected abstract List<Map<String, Object>> getClasses(String majorId);

    /**
     * 同步班级信息
     *
     * @return 同步结果
     */
    public abstract boolean syncClasses();

    /**
     * 获取学生信息
     *
     * @param classId 班级ID
     * @return 学生信息列表
     */
    protected abstract List<Map<String, Object>> getStudents(String classId);

    /**
     * 同步学生信息
     *
     * @return 同步结果
     */
    public abstract boolean syncStudents();

    /**
     * 获取教师信息
     *
     * @return 教师信息列表
     */
    protected abstract List<Map<String, Object>> getTeachers();

    /**
     * 同步教师信息
     *
     * @return 同步结果
     */
    public abstract boolean syncTeachers();

    /**
     * 获取课程信息
     *
     * @return 课程信息列表
     */
    protected abstract List<Map<String, Object>> getCourses();

    /**
     * 同步课程信息
     *
     * @return 同步结果
     */
    public abstract boolean syncCourses();

    /**
     * 执行全量同步
     *
     * @return 同步结果
     */
    public boolean syncAll() {
        log.info("开始全量同步数据");
        boolean result = true;

        try {
            result &= syncSchool();
            result &= syncFaculties();
            result &= syncMajors();
            result &= syncClasses();
            result &= syncStudents();
            result &= syncTeachers();
            result &= syncCourses();
        } catch (Exception e) {
            log.error("全量同步数据失败", e);
            result = false;
        }

        log.info("全量同步数据完成，结果：{}", result);
        return result;
    }

    /**
     * 测试连接
     *
     * @return 连接测试结果
     */
    public abstract boolean testConnection();

}
