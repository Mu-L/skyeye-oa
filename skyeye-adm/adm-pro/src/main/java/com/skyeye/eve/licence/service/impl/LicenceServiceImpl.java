/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.licence.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.licence.dao.LicenceDao;
import com.skyeye.eve.licence.entity.Licence;
import com.skyeye.eve.licence.service.LicenceService;
import com.skyeye.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: LicenceServiceImpl
 * @Description: 证照管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:09
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "证照管理", groupName = "证照模块")
public class LicenceServiceImpl extends SkyeyeBusinessServiceImpl<LicenceDao, Licence> implements LicenceService {

    @Override
    public void validatorEntity(Licence entity) {
        super.validatorEntity(entity);
        QueryWrapper<Licence> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(Licence::getLicenceNum), entity.getLicenceNum()));
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        Licence checkLicenceMation = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(checkLicenceMation)) {
            throw new CustomException("证照编号已存在");
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryLicenceList(pageInfo);
        iAuthUserService.setMationForMap(beans, "licenceAdmin", "licenceAdminMation");
        iAuthUserService.setMationForMap(beans, "borrowId", "borrowMation");
        return beans;
    }

    @Override
    public Licence selectById(String id) {
        Licence licence = super.selectById(id);
        iAuthUserService.setDataMation(licence, Licence::getLicenceAdmin);
        iAuthUserService.setDataMation(licence, Licence::getBorrowId);
        return licence;
    }

    /**
     * 获取所有证照信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllLicenceList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<Licence> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Licence::getCreateTime));
        List<Licence> licenceList = list(queryWrapper);

        outputObject.setBeans(licenceList);
        outputObject.settotal(licenceList.size());
    }

    /**
     * 获取我借用中的所有证照信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMyRevertLicenceList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<Licence> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Licence::getBorrowId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Licence::getCreateTime));
        List<Licence> licenceList = list(queryWrapper);

        outputObject.setBeans(licenceList);
        outputObject.settotal(licenceList.size());
    }

    /**
     * 设置证照领用信息
     *
     * @param id        证照id
     * @param useUserId 领用人id
     */
    @Override
    public void setLicenceUse(String id, String useUserId) {
        UpdateWrapper<Licence> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        Licence licence = new Licence();
        licence.setBorrowId(useUserId);
        update(licence, updateWrapper);
        refreshCache(id);
    }

    /**
     * 设置证照归还信息
     *
     * @param id 证照id
     */
    @Override
    public void setLicenceRevert(String id) {
        UpdateWrapper<Licence> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        Licence licence = new Licence();
        licence.setBorrowId(StringUtil.EMPTY);
        update(licence, updateWrapper);
        refreshCache(id);
    }

    /**
     * 获取我借用中的证照列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMyRevertLicencePageList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setChargePersonId(inputObject.getLogParams().get("id").toString());
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryLicenceList(pageInfo);
        iAuthUserService.setMationForMap(beans, "licenceAdmin", "licenceAdminMation");
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

}
