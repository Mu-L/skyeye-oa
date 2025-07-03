/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.holder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IBarCodeService;
import com.skyeye.holder.classenum.HolderNormsChildState;
import com.skyeye.holder.dao.HolderNormsChildDao;
import com.skyeye.holder.entity.HolderNormsChild;
import com.skyeye.holder.service.HolderNormsChildService;
import com.skyeye.holder.service.HolderNormsService;
import com.skyeye.material.entity.MaterialNormsCode;
import com.skyeye.material.service.MaterialNormsCodeService;
import com.skyeye.material.service.impl.MaterialNormsCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: HolderNormsChildServiceImpl
 * @Description: 关联的客户/供应商/会员购买或者出售的商品子信息服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/5 11:19
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "关联的客户/供应商/会员购买或者出售的商品子信息", groupName = "关联的客户/供应商/会员购买或者出售的商品子信息", manageShow = false)
public class HolderNormsChildServiceImpl extends SkyeyeBusinessServiceImpl<HolderNormsChildDao, HolderNormsChild> implements HolderNormsChildService {

    @Autowired
    private MaterialNormsCodeService materialNormsCodeService;

    @Autowired
    private IBarCodeService iBarCodeService;

    @Autowired
    private HolderNormsService holderNormsService;

    @Override
    public QueryWrapper<HolderNormsChild> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<HolderNormsChild> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNormsChild::getHolderId), commonPageInfo.getHolderId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNormsChild::getHolderKey), commonPageInfo.getHolderKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNormsChild::getNormsId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        // 商品编码信息
        List<String> normsCodeNumList = beans.stream().map(bean -> bean.get("normsCodeNum").toString())
            .collect(Collectors.toList());
        List<MaterialNormsCode> materialNormsCodeList = materialNormsCodeService.queryMaterialNormsCodeByCodeNum(StrUtil.EMPTY, normsCodeNumList);
        iBarCodeService.setBarCodeMation(materialNormsCodeList, "id", MaterialNormsCodeServiceImpl.class.getName());
        Map<String, MaterialNormsCode> materialNormsCodeMap = materialNormsCodeList.stream().collect(Collectors.toMap(MaterialNormsCode::getCodeNum, bean -> bean));
        beans.forEach(bean -> {
            String normsCodeNum = bean.get("normsCodeNum").toString();
            bean.put("normsCodeMation", materialNormsCodeMap.get(normsCodeNum));
        });
        return beans;
    }

    @Override
    public void editHolderNormsChildState(String holderId, List<String> normsCode, Integer state) {
        // 获取关联的客户/供应商/会员购买或者出售的商品信息id
        List<String> ids = holderNormsService.queryHolderMaterialIdListByHolderId(holderId);
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        // 查询购买或者出售的商品编码信息
        QueryWrapper<HolderNormsChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(HolderNormsChild::getParentId), ids);
        List<HolderNormsChild> holderNormsChildList = list(queryWrapper);
        if (CollectionUtil.isEmpty(holderNormsChildList)) {
            return;
        }
        List<String> childIds = holderNormsChildList.stream()
            .filter(bean -> normsCode.contains(bean.getNormsCodeNum()))
            .map(HolderNormsChild::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(childIds)) {
            return;
        }
        // 修改状态
        UpdateWrapper<HolderNormsChild> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(CommonConstants.ID, childIds);
        updateWrapper.set(MybatisPlusUtil.toColumns(HolderNormsChild::getState), state);
        update(updateWrapper);
    }

    @Override
    public List<Map<String, Object>> queryHolderMaterialNormsCodeListByHolder(String holderId, String holderKey, String normsId) {
        QueryWrapper<HolderNormsChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNormsChild::getHolderId), holderId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNormsChild::getHolderKey), holderKey);
        queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNormsChild::getNormsId), normsId);
        // 只查询正常的状态
        queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNormsChild::getState), HolderNormsChildState.NORMAL_TRANSACTIONS.getKey());
        List<HolderNormsChild> holderNormsChildList = list(queryWrapper);
        List<Map<String, Object>> resultList = holderNormsChildList.stream().map(bean -> {
            Map<String, Object> map = BeanUtil.beanToMap(bean);
            map.put("id", bean.getNormsCodeNum());
            map.put("name", bean.getNormsCodeNum());
            return map;
        }).collect(Collectors.toList());
        return resultList;
    }
}
