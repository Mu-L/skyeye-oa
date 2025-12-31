/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.voucher.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.voucher.classenum.VoucherState;
import com.skyeye.voucher.dao.IfsVoucherDao;
import com.skyeye.voucher.entity.Voucher;
import com.skyeye.voucher.service.IfsVoucherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IfsVoucherServiceImpl
 * @Description: 凭证信息管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/3 18:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "凭证管理", groupName = "凭证管理")
public class IfsVoucherServiceImpl extends SkyeyeBusinessServiceImpl<IfsVoucherDao, Voucher> implements IfsVoucherService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        commonPageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryIfsVoucherList(commonPageInfo);
        return beans;
    }

    @Override
    protected void createPrepose(Voucher entity) {
        super.createPrepose(entity);
        entity.setState(VoucherState.UN_CLUTTERED.getKey());
    }

    @Override
    public void deletePostpose(Voucher entity) {
        if (VoucherState.UN_CLUTTERED.getKey() == entity.getState()) {
            String basePath = tPath + entity.getPath().replace("/images/", "");
            FileUtil.deleteFile(basePath);
        }
    }

    @Override
    public void editIfsVoucherState(String id, Integer state) {
        UpdateWrapper<Voucher> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Voucher::getState), state);
        update(updateWrapper);
        refreshCache(id);
    }
}
