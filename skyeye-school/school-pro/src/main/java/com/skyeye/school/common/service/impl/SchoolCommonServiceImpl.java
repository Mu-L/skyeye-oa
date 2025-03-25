/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.common.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.common.entity.UserOrStudent;
import com.skyeye.school.common.service.SchoolCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: SchoolCommonServiceImpl
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2025/3/23 22:19
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SchoolCommonServiceImpl implements SchoolCommonService {

    @Autowired
    private IUserService iUserService;

    @Autowired
    protected IAuthUserService iAuthUserService;

    @Override
    public UserOrStudent queryUserOrStudent(String userId) {
        UserOrStudent item = new UserOrStudent();
        // 学生信息
        Map<String, Object> studentMation = iUserService.queryDataMationById(userId);
        if (CollectionUtil.isNotEmpty(studentMation)) {
            item.setUserOrStudent(true);
            studentMation.put("userIdentity", LoginIdentity.STUDENT.getKey());
            String id = studentMation.getOrDefault("id", StrUtil.EMPTY).toString();

            item.setDataMation(studentMation);
            return item;
        }
        // 教师信息
        Map<String, Object> teacherMation = iAuthUserService.queryDataMationById(userId);
        if (CollectionUtil.isNotEmpty(teacherMation)) {
            item.setUserOrStudent(false);
            teacherMation.put("userIdentity", LoginIdentity.TEACHER.getKey());
            item.setDataMation(teacherMation);
        }
        return item;
    }
}
