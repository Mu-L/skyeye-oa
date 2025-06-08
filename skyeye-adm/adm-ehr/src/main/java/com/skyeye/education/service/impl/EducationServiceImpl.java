/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.education.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.education.dao.EducationDao;
import com.skyeye.education.entity.Education;
import com.skyeye.education.service.EducationService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: EducationServiceImpl
 * @Description: 员工教育背景管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:38
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "员工教育背景", groupName = "员工教育背景", teamAuth = true)
public class EducationServiceImpl extends SkyeyeBusinessServiceImpl<EducationDao, Education> implements EducationService {

    @Override
    protected QueryWrapper<Education> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Education> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Education::getObjectId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public Education selectById(String id) {
        Education education = super.selectById(id);
        iSysDictDataService.setDataMation(education, Education::getEducationId);
        iSysDictDataService.setDataMation(education, Education::getLearningModalityId);
        iSysDictDataService.setDataMation(education, Education::getSchoolNature);
        return education;
    }

}
