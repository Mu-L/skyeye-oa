/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.code.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.doc.code.dao.CodeVersionDao;
import com.skyeye.doc.code.entity.CodeVersion;
import com.skyeye.doc.code.service.CodeVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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

    @Autowired
    private RedisCache redisCache;

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
    protected void writePostpose(CodeVersion entity, String userId) {
        super.writePostpose(entity, userId);
        String cacheKey = getCacheKey(entity.getReleaseYear());
        jedisClientService.del(cacheKey);
    }

    @Override
    protected void deletePostpose(CodeVersion entity) {
        jedisClientService.del(getCacheKey(entity.getReleaseYear()));
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
    public List<CodeVersion> queryAllReleaseCodeVersionList(String year) {
        String currentTime = DateUtil.getTimeAndToString();
        String cacheKey = getCacheKey(year);
        List<CodeVersion> tableColumns = redisCache.getList(cacheKey, key -> {
            QueryWrapper<CodeVersion> queryWrapper = new QueryWrapper<>();
            // 状态为已发布
            queryWrapper.eq(MybatisPlusUtil.toColumns(CodeVersion::getReleaseState), WhetherEnum.ENABLE_USING.getKey());
            // 发布年份等于当前年份
            queryWrapper.eq(MybatisPlusUtil.toColumns(CodeVersion::getReleaseYear), year);
            queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(CodeVersion::getReleaseTime));
            List<CodeVersion> list = list(queryWrapper);
            return list;
        }, RedisConstants.A_YEAR_SECONDS, CodeVersion.class);
        // 过滤出发布时间小于当前时间
        tableColumns.removeIf(codeVersion -> !DateUtil.compare(codeVersion.getReleaseTime(), currentTime));
        return tableColumns;
    }

    private String getCacheKey(String year) {
        return String.format(Locale.ROOT, "doc:code:version:%s", year);
    }
}
