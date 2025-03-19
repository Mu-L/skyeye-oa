package com.skyeye.rest.promote.company.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.promote.company.rest.ICompanyAndDepartmentAndJobRest;
import com.skyeye.rest.promote.company.service.ICompanyAndDepartmentAndJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ICompanyAndDepartmentAndJobServiceImpl extends IServiceImpl implements ICompanyAndDepartmentAndJobService {

    @Autowired
    private ICompanyAndDepartmentAndJobRest iCompanyAndDepartmentAndJobRest;

    @Override
    public Map<String, Object> queryCompanyInfoByCompanyIdAndDepartmentIdAndJobId(String companyId, String departmentId, String jobId) {
        return ExecuteFeignClient.get(() -> iCompanyAndDepartmentAndJobRest.queryCompanyInfoByCompanyIdAndDepartmentIdAndJobId(companyId,departmentId,jobId)).getBean();
    }

}
