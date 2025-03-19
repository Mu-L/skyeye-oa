package com.skyeye.rest.promote.company.service;

import com.skyeye.base.rest.service.IService;

import java.util.List;
import java.util.Map;

public interface ICompanyAndDepartmentAndJobService extends IService {

    List<Map<String, Object>> queryCompanyInfoByCompanyIdAndDepartmentIdAndJobId(String companyId, String departmentId, String jobId);
}
