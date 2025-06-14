/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.seal.service.impl;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.seal.dao.SealDao;
import com.skyeye.eve.seal.entity.Seal;
import com.skyeye.eve.seal.service.SealService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SealServiceImpl
 * @Description: 印章管理服务类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:02
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "印章管理", groupName = "印章模块")
public class SealServiceImpl extends SkyeyeBusinessServiceImpl<SealDao, Seal> implements SealService {

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iAuthUserService.setMationForMap(beans, "sealAdmin", "sealAdminMation");
        iAuthUserService.setMationForMap(beans, "borrowId", "borrowMation");
        return beans;
    }

    @Override
    public Seal selectById(String id) {
        Seal seal = super.selectById(id);
        seal.setSealAdminMation(iAuthUserService.queryDataMationById(seal.getSealAdmin()));
        seal.setBorrowMation(iAuthUserService.queryDataMationById(seal.getBorrowId()));
        return seal;
    }

    @Override
    public List<Seal> selectByIds(String... ids) {
        List<Seal> sealList = super.selectByIds(ids);
        iAuthUserService.setDataMation(sealList, Seal::getSealAdmin);
        iAuthUserService.setDataMation(sealList, Seal::getBorrowId);
        return sealList;
    }

    /**
     * 获取所有启用的印章信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllEnabledSealList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<Seal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Seal::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Seal::getCreateTime));
        List<Seal> sealList = list(queryWrapper);
        outputObject.setBeans(sealList);
        outputObject.settotal(sealList.size());
    }

    /**
     * 获取我借用中的所有印章信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMyRevertSealList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<Seal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Seal::getBorrowId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Seal::getCreateTime));
        List<Seal> sealList = list(queryWrapper);

        outputObject.setBeans(sealList);
        outputObject.settotal(sealList.size());
    }

    /**
     * 设置印章领用信息
     *
     * @param id        印章id
     * @param useUserId 领用人id
     */
    @Override
    public void setSealUse(String id, String useUserId) {
        UpdateWrapper<Seal> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        Seal seal = new Seal();
        seal.setBorrowId(useUserId);
        update(seal, updateWrapper);
        refreshCache(id);
    }

    /**
     * 设置印章归还信息
     *
     * @param id 印章id
     */
    @Override
    public void setSealRevert(String id) {
        UpdateWrapper<Seal> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        Seal seal = new Seal();
        seal.setBorrowId(StringUtil.EMPTY);
        update(seal, updateWrapper);
        refreshCache(id);
    }

    @Override
    public void queryMyRevertSealPageList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setChargePersonId(inputObject.getLogParams().get("id").toString());
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        QueryWrapper<Seal> queryWrapper = super.getQueryWrapper(pageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Seal::getBorrowId), inputObject.getLogParams().get("id").toString());
        List<Seal> sealList = list(queryWrapper);
        iAuthUserService.setDataMation(sealList, Seal::getSealAdmin);
        outputObject.setBeans(sealList);
        outputObject.settotal(pages.getTotal());
    }

}
