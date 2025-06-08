/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.job.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.job.dao.JobResumeDao;
import com.skyeye.job.entity.JobResume;
import com.skyeye.job.service.JobResumeService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: JobResumeServiceImpl
 * @Description: 员工工作履历管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "员工工作履历信息", groupName = "员工工作履历信息", teamAuth = true)
public class JobResumeServiceImpl extends SkyeyeBusinessServiceImpl<JobResumeDao, JobResume> implements JobResumeService {

    @Override
    protected QueryWrapper<JobResume> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<JobResume> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(JobResume::getObjectId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

}
