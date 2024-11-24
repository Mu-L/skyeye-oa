/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.dao.SchoolDao;
import com.skyeye.eve.entity.School;
import com.skyeye.eve.service.SchoolService;
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

    @Override
    public void updatePrepose(School entity) {
        School school = selectById(entity.getId());
        entity.setBackground(school.getBackground());
        entity.setNeLongitude(school.getNeLongitude());
        entity.setNeLatitude(school.getNeLatitude());
        entity.setSwLongitude(school.getSwLongitude());
        entity.setSwLatitude(school.getSwLatitude());
    }

    @Override
    public void queryAllSchoolList(InputObject inputObject, OutputObject outputObject) {
        List<School> schoolList = list();
        outputObject.setBeans(schoolList);
        outputObject.settotal(schoolList.size());
    }

    @Override
    public void coverBackground(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        UpdateWrapper<School> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, map.get("id").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(School::getBackground), map.get("background").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(School::getNeLongitude), map.get("neLongitude").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(School::getNeLatitude), map.get("neLatitude").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(School::getSwLongitude), map.get("swLongitude").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(School::getSwLatitude), map.get("swLatitude").toString());
        update(updateWrapper);
        refreshCache(map.get("id").toString());
    }

}
