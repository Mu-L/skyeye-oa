package com.skyeye.rest.promote.company.service;

import com.skyeye.base.rest.service.IService;

import java.util.Map;

public interface ICompanyService extends IService {

    Map<String, Object> queryCompanyInfoByCompanyIdAndDepartmentIdAndJobId(String companyId, String departmentId, String jobId);
}
