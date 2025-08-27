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
import com.skyeye.doc.member.dao.DocMemberVersionDao;
import com.skyeye.doc.member.entity.DocMemberVersion;
import com.skyeye.doc.member.service.DocMemberVersionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: DocMemberVersionServiceImpl
 * @Description: 会员购买的版本服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/21 8:31
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "会员购买的版本", groupName = "会员管理", tenant = TenantEnum.PLATE)
public class DocMemberVersionServiceImpl extends SkyeyeBusinessServiceImpl<DocMemberVersionDao, DocMemberVersion> implements DocMemberVersionService {

    @Override
    public void saveList(String memberId, List<String> versionIds) {
        deleteByMemberId(memberId);
        if (CollectionUtil.isNotEmpty(versionIds)) {
            List<DocMemberVersion> docMemberVersionList = new ArrayList<>();
            for (String versionId : versionIds) {
                DocMemberVersion docMemberVersion = new DocMemberVersion();
                docMemberVersion.setMemberId(memberId);
                docMemberVersion.setVersionId(versionId);
                docMemberVersionList.add(docMemberVersion);
            }
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            createEntity(docMemberVersionList, userId);
        }
    }

    @Override
    public void deleteByMemberId(String memberId) {
        QueryWrapper<DocMemberVersion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocMemberVersion::getMemberId), memberId);
        remove(queryWrapper);
    }

    @Override
    public List<DocMemberVersion> selectByMemberId(String memberId) {
        QueryWrapper<DocMemberVersion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocMemberVersion::getMemberId), memberId);
        List<DocMemberVersion> list = list(queryWrapper);
        return list;
    }

    @Override
    public Map<String, List<DocMemberVersion>> selectByMemberIds(List<String> memberIds) {
        QueryWrapper<DocMemberVersion> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DocMemberVersion::getMemberId), memberIds);
        List<DocMemberVersion> list = list(queryWrapper);
        return list.stream().collect(Collectors.groupingBy(DocMemberVersion::getMemberId));
    }
}
