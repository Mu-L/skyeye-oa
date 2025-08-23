/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.code.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.doc.code.dao.CodePackageDao;
import com.skyeye.doc.code.entity.CodePackage;
import com.skyeye.doc.code.entity.CodeVersion;
import com.skyeye.doc.code.service.CodePackageService;
import com.skyeye.doc.code.service.CodeVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * @ClassName: CodePackageServiceImpl
 * @Description: 代码包管理服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/17 17:37
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "源代码包管理", groupName = "源代码包管理", tenant = TenantEnum.PLATE)
public class CodePackageServiceImpl extends SkyeyeBusinessServiceImpl<CodePackageDao, CodePackage> implements CodePackageService {

    @Autowired
    private CodeVersionService codeVersionService;

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void validatorEntity(CodePackage entity) {
        super.validatorEntity(entity);
        CodeVersion codeVersion = codeVersionService.selectById(entity.getStartVersionId());
        if (ObjectUtil.isEmpty(codeVersion) || StrUtil.isEmpty(codeVersion.getId())) {
            throw new IllegalArgumentException("开始版本信息不存在");
        }
    }

    @Override
    protected void writePostpose(CodePackage entity, String userId) {
        super.writePostpose(entity, userId);
        jedisClientService.del(getCacheKey());
    }

    @Override
    protected void deletePostpose(CodePackage entity) {
        jedisClientService.del(getCacheKey());
    }

    @Override
    public List<CodePackage> queryAllCodePackage() {
        String cacheKey = getCacheKey();
        List<CodePackage> tableColumns = redisCache.getList(cacheKey, key -> {
            List<CodePackage> list = list();
            return list;
        }, RedisConstants.A_YEAR_SECONDS, CodePackage.class);
        return tableColumns;
    }

    @Override
    public void queryAllCodePackageList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<CodePackage> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(CodePackage::getCreateTime));
        List<CodePackage> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    private String getCacheKey() {
        return String.format(Locale.ROOT, "doc:code:package");
    }
}
