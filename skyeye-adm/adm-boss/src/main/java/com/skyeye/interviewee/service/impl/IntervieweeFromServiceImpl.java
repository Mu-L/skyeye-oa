/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.interviewee.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.interviewee.dao.IntervieweeFromDao;
import com.skyeye.interviewee.entity.IntervieweeFrom;
import com.skyeye.interviewee.service.IntervieweeFromService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IntervieweeFromServiceImpl
 * @Description: 面试者来源管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/7 13:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "面试者来源管理", groupName = "面试者管理")
public class IntervieweeFromServiceImpl extends SkyeyeBusinessServiceImpl<IntervieweeFromDao, IntervieweeFrom> implements IntervieweeFromService {

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryBossIntervieweeFromList(commonPageInfo);
        return beans;
    }

    /**
     * 获取所有的面试者来源信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllBossIntervieweeFrom(InputObject inputObject, OutputObject outputObject) {
        List<IntervieweeFrom> intervieweeFromList = queryAllData();
        outputObject.setBeans(intervieweeFromList);
        outputObject.settotal(intervieweeFromList.size());
    }
}
