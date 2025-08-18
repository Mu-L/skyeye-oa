/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.code.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.doc.code.dao.CodeVersionDao;
import com.skyeye.doc.code.entity.CodeVersion;
import com.skyeye.doc.code.service.CodeVersionService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: CodeVersionServiceImpl
 * @Description: 代码版本管理服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/17 21:13
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "代码版本管理", groupName = "代码版本管理", tenant = TenantEnum.PLATE)
public class CodeVersionServiceImpl extends SkyeyeBusinessServiceImpl<CodeVersionDao, CodeVersion> implements CodeVersionService {

    @Override
    protected void validatorEntity(CodeVersion entity) {
        super.validatorEntity(entity);
        if (WhetherEnum.ENABLE_USING.getKey().equals(entity.getReleaseState())) {
            if (StrUtil.isEmpty(entity.getReleaseTime())) {
                throw new IllegalArgumentException("请选择发布时间");
            }
            SimpleDateFormat sdf1 = new SimpleDateFormat(DateUtil.YYYY);
            String yearTime = sdf1.format(DateUtil.getPointTime(entity.getReleaseTime(), DateUtil.YYYY));
            entity.setReleaseYear(yearTime);
        }
    }

    @Override
    public void queryAllCodeVersionList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<CodeVersion> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(CodeVersion::getCreateTime));
        List<CodeVersion> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void queryAllReleaseCodeVersionList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String year = map.get("year").toString();
        String currentTime = DateUtil.getTimeAndToString();
        QueryWrapper<CodeVersion> queryWrapper = new QueryWrapper<>();
        // 状态为已发布
        queryWrapper.eq(MybatisPlusUtil.toColumns(CodeVersion::getReleaseState), WhetherEnum.ENABLE_USING.getKey());
        // 发布时间小于当前时间
        queryWrapper.le(MybatisPlusUtil.toColumns(CodeVersion::getReleaseTime), currentTime);
        // 发布年份等于当前年份
        queryWrapper.le(MybatisPlusUtil.toColumns(CodeVersion::getReleaseYear), year);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(CodeVersion::getReleaseTime));
        List<CodeVersion> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}
