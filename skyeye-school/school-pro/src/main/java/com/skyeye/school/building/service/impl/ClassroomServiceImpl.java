/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.building.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.exception.CustomException;
import com.skyeye.school.building.dao.ClassroomDao;
import com.skyeye.school.building.entity.Classroom;
import com.skyeye.school.building.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ClassroomServiceImpl
 * @Description: 教室管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/9/5 17:15
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "教室管理", groupName = "教学楼管理")
public class ClassroomServiceImpl extends SkyeyeBusinessServiceImpl<ClassroomDao, Classroom> implements ClassroomService {


    @Autowired
    private SchoolService schoolService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> bean = super.queryPageDataList(inputObject);
        schoolService.setMationForMap(bean, "createId", "createMation");
        return bean;
    }


}