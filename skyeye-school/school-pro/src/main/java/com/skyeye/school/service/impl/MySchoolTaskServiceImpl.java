/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.dao.MySchoolTaskDao;
import com.skyeye.school.service.MySchoolTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MySchoolTaskServiceImpl
 * @Description: 学校模块我的。。。服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class MySchoolTaskServiceImpl implements MySchoolTaskService {

    @Autowired
    private MySchoolTaskDao mySchoolTaskDao;

    /**
     * 获取我的待阅卷列表
     *
     * @param inputObject 入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMyWaitMarkingList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 获取当前用户拥有的学校的数据权限
        map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
        map.put("userId", inputObject.getLogParams().get("id"));
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = mySchoolTaskDao.queryMyWaitMarkingList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 获取我的已阅卷列表
     *
     * @param inputObject 入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMyEndMarkingList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 获取当前用户拥有的学校的数据权限
        map.put("schoolPowerId", inputObject.getLogParams().get("schoolPowerId"));
        map.put("userId", inputObject.getLogParams().get("id"));
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = mySchoolTaskDao.queryMyEndMarkingList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

}
