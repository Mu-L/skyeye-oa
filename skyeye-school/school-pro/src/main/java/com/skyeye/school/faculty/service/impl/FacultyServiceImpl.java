/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.faculty.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.school.faculty.dao.FacultyDao;
import com.skyeye.school.faculty.entity.Faculty;
import com.skyeye.school.faculty.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: FacultyServiceImpl
 * @Description: 院系管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 15:28
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "院系管理", groupName = "院系管理")
public class FacultyServiceImpl extends SkyeyeBusinessServiceImpl<FacultyDao, Faculty> implements FacultyService {

    @Autowired
    private SchoolService schoolService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        schoolService.setMationForMap(beans, "schoolId", "schoolMation");
        return beans;
    }

    @Override
    public Faculty selectById(String id) {
        Faculty faculty = super.selectById(id);
        schoolService.setDataMation(faculty, Faculty::getSchoolId);
        return faculty;
    }

    @Override
    public List<Faculty> selectByIds(String... ids) {
        List<Faculty> facultyList = super.selectByIds(ids);
        schoolService.setDataMation(facultyList, Faculty::getSchoolId);
        return facultyList;
    }

    /**
     * 根据学校id获取院系列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryFacultyListBySchoolId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String schoolId = map.get("schoolId").toString();
        if (StrUtil.isEmpty(schoolId)) {
            return;
        }
        QueryWrapper<Faculty> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Faculty::getSchoolId), schoolId);
        List<Faculty> facultyList = list(queryWrapper);
        outputObject.setBeans(facultyList);
        outputObject.settotal(facultyList.size());
    }

    @Override
    public Map<String, List<Faculty>> selectByIdList(List<String> facultyIds) {
        if (CollectionUtil.isEmpty(facultyIds)) {
            return new HashMap<>();
        }
        QueryWrapper<Faculty> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, facultyIds);
        Map<String, List<Faculty>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(Faculty::getId));
        return stringListMap;
    }
}
