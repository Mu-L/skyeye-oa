/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.appeal.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.appeal.dao.AppealDao;
import com.skyeye.appeal.entity.Appeal;
import com.skyeye.appeal.service.AppealService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.checkwork.service.CheckWorkService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AppealServiceImpl
 * @Description: 考勤申诉管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/7/18 11:40
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "考勤申诉", groupName = "考勤申诉", flowable = true)
public class AppealServiceImpl extends SkyeyeBusinessServiceImpl<AppealDao, Appeal> implements AppealService {

    @Autowired
    private CheckWorkService checkWorkService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryAppealList(pageInfo);
        return beans;
    }

    @Override
    public Appeal selectById(String id) {
        Appeal appeal = super.selectById(id);
        checkWorkService.setDataMation(appeal, Appeal::getWorkId);
        return appeal;
    }
}
