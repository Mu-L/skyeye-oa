package com.skyeye.rest.school.student.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.school.student.rest.IStudentRest;
import com.skyeye.rest.school.student.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * @ClassName: IStudentServiceImpl
 * @Description: 学生信息
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class IStudentServiceImpl extends IServiceImpl implements IStudentService {

    @Autowired
    private IStudentRest iStudentRest;

    @Override
    public void addStudent(Map<String, Object> map) {
        ExecuteFeignClient.get(()-> iStudentRest.addStudent(map));
    }

    @Override
    public List<Map<String, Object>> queryStudentByStudentNumbers(String studentNumbers) {
        return ExecuteFeignClient.get(()-> iStudentRest.queryStudentByStudentNumbers(studentNumbers)).getRows();
    }
}
