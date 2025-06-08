/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.certificate.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.certificate.dao.CertificateDao;
import com.skyeye.certificate.entity.Certificate;
import com.skyeye.certificate.service.CertificateService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import org.springframework.stereotype.Service;

/**
 * @ClassName: CertificateServiceImpl
 * @Description: 员工证书管理服务类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:37
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "员工证书", groupName = "员工证书", teamAuth = true)
public class CertificateServiceImpl extends SkyeyeBusinessServiceImpl<CertificateDao, Certificate> implements CertificateService {

    @Override
    protected QueryWrapper<Certificate> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Certificate> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Certificate::getObjectId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public Certificate selectById(String id) {
        Certificate certificate = super.selectById(id);
        iSysDictDataService.setDataMation(certificate, Certificate::getTypeId);
        return certificate;
    }

}
