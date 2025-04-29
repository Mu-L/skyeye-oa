/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.organization.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAreaService;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.dao.CompanyDepartmentDao;
import com.skyeye.organization.dao.CompanyJobDao;
import com.skyeye.organization.dao.CompanyMationDao;
import com.skyeye.organization.entity.Company;
import com.skyeye.organization.entity.CompanyTaxRate;
import com.skyeye.organization.service.CompanyMationService;
import com.skyeye.organization.service.CompanyTaxRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: CompanyMationServiceImpl
 * @Description: 公司信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:57
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "企业管理", groupName = "组织模块")
public class CompanyMationServiceImpl extends SkyeyeBusinessServiceImpl<CompanyMationDao, Company> implements CompanyMationService {

    @Autowired
    private CompanyDepartmentDao companyDepartmentDao;

    @Autowired
    private CompanyJobDao companyJobDao;

    @Autowired
    private IAreaService iAreaService;

    @Autowired
    private CompanyTaxRateService companyTaxRateService;

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryCompanyMationList(map);
        iAreaService.setNameForMap(beans, "provinceId", "provinceName");
        iAreaService.setNameForMap(beans, "cityId", "cityName");
        iAreaService.setNameForMap(beans, "areaId", "areaName");
        iAreaService.setNameForMap(beans, "townshipId", "townshipName");
        return beans;
    }

    @Override
    public void writePostpose(Company entity, String userId) {
        super.writePostpose(entity, userId);
        companyTaxRateService.saveCompanyTaxRate(entity.getId(), entity.getTaxRate());
    }

    @Override
    public void deletePreExecution(String id) {
        // 判断是否有子公司
        Map<String, Object> bean = skyeyeBaseMapper.queryCompanyMationById(id);
        if (Integer.parseInt(bean.get("childsNum").toString()) > 0) {
            throw new CustomException("该公司下存在子公司，无法直接删除。");
        }
        // 判断是否有部门
        bean = skyeyeBaseMapper.queryCompanyDepartMentNumMationById(id);
        if (Integer.parseInt(bean.get("departmentNum").toString()) > 0) {
            throw new CustomException("该公司下存在部门，无法直接删除。");
        }
        // 判断是否有员工
        bean = skyeyeBaseMapper.queryCompanyUserNumMationById(id);
        if (Integer.parseInt(bean.get("companyUserNum").toString()) > 0) {
            throw new CustomException("该公司下存在员工，无法直接删除。");
        }
    }

    @Override
    public void deletePostpose(String id) {
        // 删除完成后的后置执行事件，根据公司id删除该公司拥有的个人所得税税率信息
        companyTaxRateService.deleteCompanyTaxRateByPId(id);
    }

    @Override
    public Company getDataFromDb(String id) {
        Company company = super.getDataFromDb(id);
        // 个人所得税税率信息
        company.setTaxRate(companyTaxRateService.queryCompanyTaxRateByPId(id));
        return company;
    }

    @Override
    public List<Company> getDataFromDb(List<String> ids) {
        List<Company> companyList = super.getDataFromDb(ids);
        if (CollectionUtil.isEmpty(companyList)) {
            return new ArrayList<>();
        }
        Map<String, List<CompanyTaxRate>> companyTaxRateMap = companyTaxRateService.queryCompanyTaxRateByPId(ids);
        companyList.forEach(company -> {
            company.setTaxRate(companyTaxRateMap.get(company.getId()));
        });
        return companyList;
    }

    /**
     * 获取总公司信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryOverAllCompanyMationList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String notId = map.get("notId").toString();
        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Company::getpId), CommonNumConstants.NUM_ZERO.toString());
        if (StrUtil.isNotEmpty(notId)) {
            queryWrapper.ne(CommonConstants.ID, notId);
        }
        List<Company> companyList = list(queryWrapper);
        outputObject.setBeans(companyList);
        outputObject.settotal(companyList.size());
    }

    /**
     * 获取公司信息列表展示为树
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryCompanyMationListTree(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryCompanyMationListTree(map);
        String[] s;
        for (Map<String, Object> bean : beans) {
            s = bean.get("parentId").toString().split(",");
            bean.put("level", s.length);
            if (s.length > 0) {
                bean.put("parentId", s[s.length - 1]);
            } else {
                bean.put("parentId", "0");
            }
        }
        beans = ToolUtil.listToTree(beans, "id", "parentId", "children");
        if (CollectionUtil.isNotEmpty(beans)) {
            ToolUtil.setLastIdentification(beans);
            outputObject.setBeans(beans);
            outputObject.settotal(beans.size());
        }
    }

    /**
     * 获取公司列表展示为下拉选择框
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryCompanyListToSelect(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryCompanyListToSelect(map);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 获取企业组织机构图
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryCompanyOrganization(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = new ArrayList<>();
        // 1.获取企业
        List<Map<String, Object>> company = skyeyeBaseMapper.queryCompanyListToSelect(map);
        beans.addAll(company);
        // 2.获取部门
        List<Map<String, Object>> department = companyDepartmentDao.queryCompanyDepartmentOrganization(map);
        for (Map<String, Object> bean : department) {
            String[] s = bean.get("parentId").toString().split(",");
            if (s.length > 0 && !"0".equals(bean.get("parentId").toString())) {
                bean.put("parentId", s[s.length - 1]);
            } else {
                bean.put("parentId", bean.get("companyId"));
            }
        }
        beans.addAll(department);
        // 3.获取职位
        List<Map<String, Object>> job = companyJobDao.queryCompanyJobSimpleList(new HashMap<>());
        for (Map<String, Object> bean : job) {
            String[] s = bean.get("parentId").toString().split(",");
            if (s.length > 0 && !"0".equals(bean.get("parentId").toString())) {
                bean.put("parentId", s[s.length - 1]);
            } else {
                bean.put("parentId", bean.get("departmentId"));
            }
        }
        beans.addAll(job);
        beans = ToolUtil.listToTree(beans, "id", "parentId", "children");
        outputObject.setBeans(getParentMap(beans));
    }

    private List<Map<String, Object>> getParentMap(List<Map<String, Object>> children) {
        List<Map<String, Object>> beans = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "所有");
        map.put("title", "企业组织结构图");
        map.put("children", children);
        beans.add(map);
        return beans;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        return this.queryDataList(inputObject);
    }

    @Override
    public List<Map<String, Object>> queryCompanyList(String companyId) {
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryCompanyList(companyId);
        return CollectionUtil.isNotEmpty(beans) ? beans : new ArrayList<>();
    }

}
