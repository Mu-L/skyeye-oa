package com.skyeye.school.test.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.faculty.service.FacultyService;
import com.skyeye.school.test.entity.Test;
import com.skyeye.school.student.service.StudentService;
import com.skyeye.school.test.dao.TestDao;
import com.skyeye.school.test.entity.Test;
import com.skyeye.school.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.itextpdf.text.pdf.security.SecurityConstants.Id;

@Service
@SkyeyeService(name = "测试案例", groupName = "测试案例")
//
public class TestServicelmpl extends SkyeyeBusinessServiceImpl<TestDao, Test> implements TestService {

  
}



