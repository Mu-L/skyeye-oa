/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.code.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
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
import com.skyeye.doc.download.service.DownloadService;
import com.skyeye.doc.member.entity.DocMember;
import com.skyeye.doc.member.entity.DocMemberPackage;
import com.skyeye.doc.member.entity.DocMemberVersion;
import com.skyeye.doc.member.service.DocMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
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
@Slf4j
@Service
@SkyeyeService(name = "源代码管理", groupName = "源代码管理", tenant = TenantEnum.PLATE)
public class CodeSourceServiceImpl extends SkyeyeBusinessServiceImpl<CodeSourceDao, CodeSource> implements CodeSourceService {

    @Autowired
    private CodePackageService codePackageService;

    @Autowired
    private CodeVersionService codeVersionService;

    @Autowired
    private DocMemberService docMemberService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private DownloadService downloadService;

    @Override
    protected void createPrepose(CodeSource entity) {
        CodeVersion codeVersion = codeVersionService.selectById(entity.getVersionId());
        if (ObjectUtil.isEmpty(codeVersion) || StrUtil.isEmpty(codeVersion.getId())) {
            throw new IllegalArgumentException("版本信息不存在");
        }
        if (WhetherEnum.DISABLE_USING.getKey().equals(codeVersion.getReleaseState())) {
            throw new IllegalArgumentException("版本未发布");
        }
        CodePackage codePackage = codePackageService.selectById(entity.getPackageId());
        if (ObjectUtil.isEmpty(codePackage) || StrUtil.isEmpty(codePackage.getId())) {
            throw new IllegalArgumentException("源代码包信息不存在");
        }
        entity.setYear(codeVersion.getReleaseYear());
        // 先删除之前上传的源代码包
        QueryWrapper<CodeSource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CodeSource::getVersionId), entity.getVersionId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(CodeSource::getPackageId), entity.getPackageId());
        remove(queryWrapper);
    }

    @Override
    protected void createPostpose(CodeSource entity, String userId) {
        jedisClientService.del(getCacheKey(entity.getYear()));
    }

    @Override
    public List<CodeSource> selectByIds(String... ids) {
        List<CodeSource> codeSourceList = super.selectByIds(ids);
        codePackageService.setDataMation(codeSourceList, CodeSource::getPackageId);
        codeVersionService.setDataMation(codeSourceList, CodeSource::getVersionId);
        return codeSourceList;
    }

    @Override
    public void removeCodeSource(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String versionId = map.get("versionId").toString();
        String packageId = map.get("packageId").toString();
        QueryWrapper<CodeSource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CodeSource::getVersionId), versionId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CodeSource::getPackageId), packageId);
        CodeSource codeSource = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(codeSource)) {
            return;
        }
        deleteById(codeSource.getId());

        CodeVersion codeVersion = codeVersionService.selectById(versionId);
        jedisClientService.del(getCacheKey(codeVersion.getReleaseYear()));
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
        codeVersionService.setDataMation(codePackageList, CodePackage::getStartVersionId);
        codeVersionService.setDataMation(codePackageList, CodePackage::getEndVersionId);

        // 封装数据
        if (CollectionUtil.isNotEmpty(codeVersionList)) {
            String cacheKey = getCacheKey(year);
            List<String> versionIdList = codeVersionList.stream().map(CodeVersion::getId).collect(Collectors.toList());
            List<CodeSource> tableColumns = redisCache.getList(cacheKey, key -> {
                QueryWrapper<CodeSource> queryWrapper = new QueryWrapper<>();
                queryWrapper.in(MybatisPlusUtil.toColumns(CodeSource::getVersionId), versionIdList);
                List<CodeSource> codeSourceList = list(queryWrapper);
                return codeSourceList;
            }, RedisConstants.A_YEAR_SECONDS, CodeSource.class);
            // 根据版本id过滤源代码包，因为版本的状态信息变化不会影响源代码，所以需要过滤一下
            tableColumns.removeIf(codeSource -> !versionIdList.contains(codeSource.getVersionId()));
            result.put("codeSourceList", tableColumns);
        }
        result.put("codeVersionList", codeVersionList);
        result.put("codePackageList", codePackageList);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public String getCacheKey(String year) {
        return String.format(Locale.ROOT, "doc:code:source:%s", year);
    }

    @Override
    public Boolean checkFilePermission(String path) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<CodeSource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CodeSource::getFilePath), path);
        // 判断源代码是否存在
        CodeSource codeSource = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(codeSource)) {
            log.warn("源代码信息不存在");
            return false;
        }
        // 判断版本号是否存在
        CodeVersion codeVersion = codeVersionService.selectById(codeSource.getVersionId());
        if (ObjectUtil.isEmpty(codeVersion) || StrUtil.isEmpty(codeVersion.getId())) {
            log.warn("版本信息不存在");
            return false;
        }
        // 判断版本是否发布
        if (WhetherEnum.DISABLE_USING.getKey().equals(codeVersion.getReleaseState())) {
            log.warn("版本未发布");
            return false;
        }
        // 判断源代码包是否存在
        CodePackage codePackage = codePackageService.selectById(codeSource.getPackageId());
        if (ObjectUtil.isEmpty(codePackage) || StrUtil.isEmpty(codePackage.getId())) {
            log.warn("源代码包信息不存在");
            return false;
        }
        if (StrUtil.equals(codeSource.getCreateId(), userId)) {
            return true;
        }
        DocMember docMember = docMemberService.selectById(userId);
        if (ObjectUtil.isEmpty(docMember) || StrUtil.isEmpty(docMember.getId())) {
            return false;
        }
        List<String> packageIds = docMember.getPackageList().stream().map(DocMemberPackage::getPackageId).collect(Collectors.toList());
        List<String> versionIds = docMember.getVersionList().stream().map(DocMemberVersion::getVersionId).collect(Collectors.toList());
        if (packageIds.contains(codeSource.getPackageId()) && versionIds.contains(codeSource.getVersionId())) {
            downloadService.createDownloadLog(userId, codeSource.getId());
            return true;
        }
        return false;
    }
}
