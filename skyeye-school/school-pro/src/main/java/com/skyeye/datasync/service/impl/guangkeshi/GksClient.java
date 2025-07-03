/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.datasync.service.impl.guangkeshi;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.datasync.AbstractSyncClient;
import com.skyeye.datasync.http.HttpClientRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: GksClient
 * @Description: 广西科技师范学院客户端
 * @author: skyeye云系列--卫志强
 * @date: 2025/7/2 8:36
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@Slf4j
public class GksClient extends AbstractSyncClient {

    // 静态配置变量
    private static final String API_URL = "http://api.gks.edu.cn/api";
    private static final String APP_ID = "gks_app_id";
    private static final String APP_SECRET = "gks_app_secret";
    private static final String TOKEN = "gks_token";

    // HTTP请求客户端
    private HttpClientRequest httpClient;

    public GksClient() {
        // 初始化HTTP客户端
        httpClient = new HttpClientRequest(API_URL);
        // 设置默认请求头
        Map<String, String> headers = getDefaultHeaders();
        httpClient.setDefaultHeaders(headers);
    }

    /**
     * 获取默认请求头
     *
     * @return 默认请求头
     */
    private Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("App-Id", APP_ID);
        headers.put("App-Secret", APP_SECRET);
        headers.put("Authorization", "Bearer " + TOKEN);
        return headers;
    }

    /**
     * 解析API响应
     *
     * @param response API响应
     * @return 解析后的数据
     */
    private Map<String, Object> parseResponse(String response) {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(response);
            if (jsonObject.containsKey("returnCode") && "0".equals(jsonObject.getStr("returnCode"))) {
                if (jsonObject.containsKey("data") && jsonObject.get("data") instanceof JSONObject) {
                    return JSONUtil.toBean(jsonObject.getJSONObject("data").toString(), HashMap.class);
                } else {
                    log.error("API响应中data字段不是JSON对象：{}", response);
                    return null;
                }
            } else {
                log.error("API响应错误：{}", response);
                return null;
            }
        } catch (Exception e) {
            log.error("解析API响应失败", e);
            return null;
        }
    }

    /**
     * 解析API响应列表
     *
     * @param response API响应
     * @return 解析后的数据列表
     */
    private List<Map<String, Object>> parseResponseList(String response) {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(response);
            if (jsonObject.containsKey("returnCode") && "0".equals(jsonObject.getStr("returnCode"))) {
                List<Map<String, Object>> result = new ArrayList<>();
                jsonObject.getJSONArray("data").forEach(item -> {
                    if (item instanceof JSONObject) {
                        result.add(((JSONObject) item).toBean(Map.class));
                    }
                });
                return result;
            } else {
                log.error("API响应错误：{}", response);
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("解析API响应失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取学校信息
     *
     * @return 学校信息
     */
    @Override
    public Map<String, Object> getSchool() {
        log.info("获取广西科技师范学院学校信息");
        try {
            // 调用API获取学校信息
            String response = httpClient.get("/school/info");
            return parseResponse(response);
        } catch (Exception e) {
            log.error("获取学校信息失败", e);
            return null;
        }
    }

    /**
     * 同步学校信息
     *
     * @return 同步结果
     */
    @Override
    public boolean syncSchool() {
        log.info("开始同步广西科技师范学院学校信息");
        try {
            Map<String, Object> schoolInfo = getSchool();
            if (schoolInfo == null) {
                log.error("获取学校信息失败，无法同步");
                return false;
            }

            // TODO: 实现将学校信息保存到数据库的逻辑
            log.info("学校信息同步成功：{}", schoolInfo);
            return true;
        } catch (Exception e) {
            log.error("同步学校信息失败", e);
            return false;
        }
    }

    /**
     * 获取院系信息
     *
     * @return 院系信息列表
     */
    @Override
    protected List<Map<String, Object>> getFaculties() {
        log.info("获取广西科技师范学院院系信息");
        try {
            // 调用API获取院系信息
            String response = httpClient.get("/faculty/list");
            return parseResponseList(response);
        } catch (Exception e) {
            log.error("获取院系信息失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 同步院系信息
     *
     * @return 同步结果
     */
    @Override
    public boolean syncFaculties() {
        log.info("开始同步广西科技师范学院院系信息");
        try {
            List<Map<String, Object>> faculties = getFaculties();
            if (faculties.isEmpty()) {
                log.error("获取院系信息失败，无法同步");
                return false;
            }

            // TODO: 实现将院系信息保存到数据库的逻辑
            log.info("院系信息同步成功，共 {} 条记录", faculties.size());
            return true;
        } catch (Exception e) {
            log.error("同步院系信息失败", e);
            return false;
        }
    }

    /**
     * 获取专业信息
     *
     * @param facultyId 院系ID
     * @return 专业信息列表
     */
    @Override
    protected List<Map<String, Object>> getMajors(String facultyId) {
        log.info("获取广西科技师范学院专业信息，院系ID：{}", facultyId);
        try {
            // 调用API获取专业信息
            Map<String, Object> params = new HashMap<>();
            params.put("facultyId", facultyId);
            String response = httpClient.get("/major/list", params);
            return parseResponseList(response);
        } catch (Exception e) {
            log.error("获取专业信息失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 同步专业信息
     *
     * @return 同步结果
     */
    @Override
    public boolean syncMajors() {
        log.info("开始同步广西科技师范学院专业信息");
        try {
            List<Map<String, Object>> faculties = getFaculties();
            if (faculties.isEmpty()) {
                log.error("获取院系信息失败，无法同步专业信息");
                return false;
            }

            List<Map<String, Object>> allMajors = new ArrayList<>();
            for (Map<String, Object> faculty : faculties) {
                String facultyId = (String) faculty.get("facultyId");
                List<Map<String, Object>> majors = getMajors(facultyId);
                allMajors.addAll(majors);
            }

            if (allMajors.isEmpty()) {
                log.error("获取专业信息失败，无法同步");
                return false;
            }

            // TODO: 实现将专业信息保存到数据库的逻辑
            log.info("专业信息同步成功，共 {} 条记录", allMajors.size());
            return true;
        } catch (Exception e) {
            log.error("同步专业信息失败", e);
            return false;
        }
    }

    /**
     * 获取班级信息
     *
     * @param majorId 专业ID
     * @return 班级信息列表
     */
    @Override
    protected List<Map<String, Object>> getClasses(String majorId) {
        log.info("获取广西科技师范学院班级信息，专业ID：{}", majorId);
        try {
            // 调用API获取班级信息
            Map<String, Object> params = new HashMap<>();
            params.put("majorId", majorId);
            String response = httpClient.get("/class/list", params);
            return parseResponseList(response);
        } catch (Exception e) {
            log.error("获取班级信息失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 同步班级信息
     *
     * @return 同步结果
     */
    @Override
    public boolean syncClasses() {
        log.info("开始同步广西科技师范学院班级信息");
        try {
            List<Map<String, Object>> faculties = getFaculties();
            if (faculties.isEmpty()) {
                log.error("获取院系信息失败，无法同步班级信息");
                return false;
            }

            List<Map<String, Object>> allClasses = new ArrayList<>();
            for (Map<String, Object> faculty : faculties) {
                String facultyId = (String) faculty.get("facultyId");
                List<Map<String, Object>> majors = getMajors(facultyId);

                for (Map<String, Object> major : majors) {
                    String majorId = (String) major.get("majorId");
                    List<Map<String, Object>> classes = getClasses(majorId);
                    allClasses.addAll(classes);
                }
            }

            if (allClasses.isEmpty()) {
                log.error("获取班级信息失败，无法同步");
                return false;
            }

            // TODO: 实现将班级信息保存到数据库的逻辑
            log.info("班级信息同步成功，共 {} 条记录", allClasses.size());
            return true;
        } catch (Exception e) {
            log.error("同步班级信息失败", e);
            return false;
        }
    }

    /**
     * 获取学生信息
     *
     * @param classId 班级ID
     * @return 学生信息列表
     */
    @Override
    protected List<Map<String, Object>> getStudents(String classId) {
        log.info("获取广西科技师范学院学生信息，班级ID：{}", classId);
        try {
            // 调用API获取学生信息
            Map<String, Object> params = new HashMap<>();
            params.put("classId", classId);
            String response = httpClient.get("/student/list", params);
            return parseResponseList(response);
        } catch (Exception e) {
            log.error("获取学生信息失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 同步学生信息
     *
     * @return 同步结果
     */
    @Override
    public boolean syncStudents() {
        log.info("开始同步广西科技师范学院学生信息");
        try {
            List<Map<String, Object>> faculties = getFaculties();
            if (faculties.isEmpty()) {
                log.error("获取院系信息失败，无法同步学生信息");
                return false;
            }

            List<Map<String, Object>> allStudents = new ArrayList<>();
            for (Map<String, Object> faculty : faculties) {
                String facultyId = (String) faculty.get("facultyId");
                List<Map<String, Object>> majors = getMajors(facultyId);

                for (Map<String, Object> major : majors) {
                    String majorId = (String) major.get("majorId");
                    List<Map<String, Object>> classes = getClasses(majorId);

                    for (Map<String, Object> clazz : classes) {
                        String classId = (String) clazz.get("classId");
                        List<Map<String, Object>> students = getStudents(classId);
                        allStudents.addAll(students);
                    }
                }
            }

            if (allStudents.isEmpty()) {
                log.error("获取学生信息失败，无法同步");
                return false;
            }

            // TODO: 实现将学生信息保存到数据库的逻辑
            log.info("学生信息同步成功，共 {} 条记录", allStudents.size());
            return true;
        } catch (Exception e) {
            log.error("同步学生信息失败", e);
            return false;
        }
    }

    /**
     * 获取教师信息
     *
     * @return 教师信息列表
     */
    @Override
    protected List<Map<String, Object>> getTeachers() {
        log.info("获取广西科技师范学院教师信息");
        try {
            // 调用API获取教师信息
            String response = httpClient.get("/teacher/list");
            return parseResponseList(response);
        } catch (Exception e) {
            log.error("获取教师信息失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 同步教师信息
     *
     * @return 同步结果
     */
    @Override
    public boolean syncTeachers() {
        log.info("开始同步广西科技师范学院教师信息");
        try {
            List<Map<String, Object>> teachers = getTeachers();
            if (teachers.isEmpty()) {
                log.error("获取教师信息失败，无法同步");
                return false;
            }

            // TODO: 实现将教师信息保存到数据库的逻辑
            log.info("教师信息同步成功，共 {} 条记录", teachers.size());
            return true;
        } catch (Exception e) {
            log.error("同步教师信息失败", e);
            return false;
        }
    }

    /**
     * 获取课程信息
     *
     * @return 课程信息列表
     */
    @Override
    protected List<Map<String, Object>> getCourses() {
        log.info("获取广西科技师范学院课程信息");
        try {
            // 调用API获取课程信息
            String response = httpClient.get("/course/list");
            return parseResponseList(response);
        } catch (Exception e) {
            log.error("获取课程信息失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 同步课程信息
     *
     * @return 同步结果
     */
    @Override
    public boolean syncCourses() {
        log.info("开始同步广西科技师范学院课程信息");
        try {
            List<Map<String, Object>> courses = getCourses();
            if (courses.isEmpty()) {
                log.error("获取课程信息失败，无法同步");
                return false;
            }

            // TODO: 实现将课程信息保存到数据库的逻辑
            log.info("课程信息同步成功，共 {} 条记录", courses.size());
            return true;
        } catch (Exception e) {
            log.error("同步课程信息失败", e);
            return false;
        }
    }

    /**
     * 测试连接
     *
     * @return 连接测试结果
     */
    @Override
    public boolean testConnection() {
        log.info("测试广西科技师范学院API连接");
        try {
            // 调用API测试连接
            String response = httpClient.get("/ping");
            JSONObject jsonObject = JSONUtil.parseObj(response);
            return jsonObject.containsKey("returnCode") && "0".equals(jsonObject.getStr("returnCode"));
        } catch (Exception e) {
            log.error("API连接测试失败", e);
            return false;
        }
    }
}
