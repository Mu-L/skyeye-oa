/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.major.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.school.faculty.service.FacultyService;
import com.skyeye.school.major.dao.MajorDao;
import com.skyeye.school.major.entity.Major;
import com.skyeye.school.major.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MajorServiceImpl
 * @Description: 专业管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/9 9:52
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "专业管理", groupName = "专业管理")
public class MajorServiceImpl extends SkyeyeBusinessServiceImpl<MajorDao, Major> implements MajorService {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private FacultyService facultyService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        schoolService.setMationForMap(beans, "schoolId", "schoolMation");
        facultyService.setMationForMap(beans, "facultyId", "facultyMation");
        return beans;
    }

    @Override
    public Major selectById(String id) {
        Major major = super.selectById(id);
        schoolService.setDataMation(major, Major::getSchoolId);
        facultyService.setDataMation(major, Major::getFacultyId);
        return major;
    }

    @Override
    public List<Major> selectByIds(String... ids) {
        List<Major> majorList = super.selectByIds(ids);
        schoolService.setDataMation(majorList, Major::getSchoolId);
        facultyService.setDataMation(majorList, Major::getFacultyId);
        return majorList;
    }

    /**
     * 根据院系id获取专业列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMajorListByFacultyId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String facultyId = map.get("facultyId").toString();
        if (StrUtil.isEmpty(facultyId)) {
            return;
        }
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Major::getFacultyId), facultyId);
        List<Major> majorList = list(queryWrapper);
        outputObject.setBeans(majorList);
        outputObject.settotal(majorList.size());
    }

    @Override
    public Map<String, List<Major>> selectByIdList(List<String> majorIds) {
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, majorIds);
        Map<String, List<Major>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(Major::getId));
        return stringListMap;
    }
}