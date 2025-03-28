package com.skyeye.rest.school.student.service;


import com.skyeye.base.rest.service.IService;

import java.util.Map;

public interface IStudentService extends IService {
    void addStudent(Map<String, Object> map);
}
