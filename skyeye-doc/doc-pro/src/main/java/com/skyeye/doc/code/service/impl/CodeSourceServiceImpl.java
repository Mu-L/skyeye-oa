/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.code.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.doc.code.dao.CodeSourceDao;
import com.skyeye.doc.code.entity.CodePackage;
import com.skyeye.doc.code.entity.CodeSource;
import com.skyeye.doc.code.entity.CodeVersion;
import com.skyeye.doc.code.service.CodePackageService;
import com.skyeye.doc.code.service.CodeSourceService;
import com.skyeye.doc.code.service.CodeVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CodeSourceServiceImpl
 * @Description: 源代码服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/19 8:26
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "源代码管理", groupName = "源代码管理", tenant = TenantEnum.PLATE)
public class CodeSourceServiceImpl extends SkyeyeBusinessServiceImpl<CodeSourceDao, CodeSource> implements CodeSourceService {

    @Autowired
    private CodePackageService codePackageService;

    @Autowired
    private CodeVersionService codeVersionService;

    @Override
    protected void createPrepose(CodeSource entity) {
        // 先删除之前上传的源代码包
        QueryWrapper<CodeSource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CodeSource::getVersionId), entity.getVersionId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(CodeSource::getPachageId), entity.getPachageId());
        remove(queryWrapper);
    }

    @Override
    public void queryAllReleaseCodeList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String year = map.get("year").toString();
        Map<String, Object> result = inputObject.getParams();
        // 查询版本
        List<CodeVersion> codeVersionList = codeVersionService.queryAllReleaseCodeVersionList(year);
        // 查询源代码包
        List<CodePackage> codePackageList = codePackageService.queryAllCodePackage();
        // 封装数据
        if (CollectionUtil.isNotEmpty(codeVersionList)) {
            List<String> versionIdList = codeVersionList.stream().map(CodeVersion::getId).collect(Collectors.toList());
            QueryWrapper<CodeSource> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(MybatisPlusUtil.toColumns(CodeSource::getVersionId), versionIdList);
            List<CodeSource> codeSourceList = list(queryWrapper);
            result.put("codeSourceList", codeSourceList);
        }
        result.put("codeVersionList", codeVersionList);
        result.put("codePackageList", codePackageList);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}
