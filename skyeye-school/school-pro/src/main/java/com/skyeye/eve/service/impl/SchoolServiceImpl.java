/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.dao.SchoolDao;
import com.skyeye.eve.entity.School;
import com.skyeye.eve.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SchoolController
 * @Description: 学校管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/6 21:13
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "学校管理", groupName = "学校管理")
public class SchoolServiceImpl extends SkyeyeBusinessServiceImpl<SchoolDao, School> implements SchoolService {

    @Autowired
    private SchoolDao schoolDao;

    /**
     * 获取所有学校列表展示为下拉选择框
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllSchoolList(InputObject inputObject, OutputObject outputObject) {
        List<School> schoolList = list();
        outputObject.setBeans(schoolList);
        outputObject.settotal(schoolList.size());
    }

    @Override
    public void coverBackground(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        schoolDao.coverBackground(params);
    }

}
