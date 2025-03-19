package com.skyeye.rest.promote.company.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.promote.company.rest.ICompanyRest;
import com.skyeye.rest.promote.company.service.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ICompanyServiceImpl extends IServiceImpl implements ICompanyService {

    @Autowired
    private ICompanyRest iCompanyRest;

    @Override
    public Map<String, Object> queryCompanyInfoByCompanyIdAndDepartmentIdAndJobId(String companyId, String departmentId, String jobId) {
        return ExecuteFeignClient.get(() -> iCompanyRest.queryCompanyInfoByCompanyIdAndDepartmentIdAndJobId(companyId,departmentId,jobId)).getBean();
    }

}
