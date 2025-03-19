package com.skyeye.rest.school.service;

import com.skyeye.base.rest.service.IService;

import java.util.List;
import java.util.Map;

public interface ISchoolService extends IService {
    List<Map<String,Object>> querySchoolStudentMation(String no,String id,String userId);
}
