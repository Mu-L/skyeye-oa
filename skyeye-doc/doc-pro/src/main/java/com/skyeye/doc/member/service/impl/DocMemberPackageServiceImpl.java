/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.member.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.doc.member.dao.DocMemberPackageDao;
import com.skyeye.doc.member.entity.DocMemberPackage;
import com.skyeye.doc.member.service.DocMemberPackageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: DocMemberPackageServiceImpl
 * @Description: 会员购买的包服务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/21 8:21
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "会员购买的包", groupName = "会员管理", tenant = TenantEnum.PLATE)
public class DocMemberPackageServiceImpl extends SkyeyeBusinessServiceImpl<DocMemberPackageDao, DocMemberPackage> implements DocMemberPackageService {

    @Override
    public void saveList(String memberId, List<String> packageIds) {
        deleteByMemberId(memberId);
        if (CollectionUtil.isNotEmpty(packageIds)) {
            List<DocMemberPackage> docMemberPackageList = new ArrayList<>();
            for (String packageId : packageIds) {
                DocMemberPackage docMemberPackage = new DocMemberPackage();
                docMemberPackage.setMemberId(memberId);
                docMemberPackage.setPackageId(packageId);
                docMemberPackageList.add(docMemberPackage);
            }
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            createEntity(docMemberPackageList, userId);
        }
    }

    @Override
    public void deleteByMemberId(String memberId) {
        QueryWrapper<DocMemberPackage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocMemberPackage::getMemberId), memberId);
        remove(queryWrapper);
    }

    @Override
    public List<DocMemberPackage> selectByMemberId(String memberId) {
        QueryWrapper<DocMemberPackage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocMemberPackage::getMemberId), memberId);
        List<DocMemberPackage> list = list(queryWrapper);
        return list;
    }
}
