package com.skyeye.feeapplication.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.feeapplication.dao.FeeApplicationDao;
import com.skyeye.feeapplication.entity.FeeApplication;
import com.skyeye.feeapplication.service.FeeApplicationService;
import com.skyeye.organization.service.IDepmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FeeApplicationServiceImpl
 * @Description: 费用申请服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 14:17
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "费用申请", groupName = "费用申请", flowable = true)
public class FeeApplicationServiceImpl extends SkyeyeFlowableServiceImpl<FeeApplicationDao, FeeApplication> implements FeeApplicationService {

    @Autowired
    private IDepmentService iDepmentService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iDepmentService.setMationForMap(beans,"departmentId","departmentMation");
        iAuthUserService.setMationForMap(beans,"applicantId","applicantMation");
        return beans;
    }

    @Override
    public FeeApplication selectById(String id) {
        FeeApplication feeApplication = super.selectById(id);
        iDepmentService.setDataMation(feeApplication,FeeApplication::getDepartmentId);
        iAuthUserService.setDataMation(feeApplication,FeeApplication::getApplicantId);
        return feeApplication;
    }

    @Override
    public void queryFeeApplicationAnalysis(InputObject inputObject, OutputObject outputObject) {

    }
}
