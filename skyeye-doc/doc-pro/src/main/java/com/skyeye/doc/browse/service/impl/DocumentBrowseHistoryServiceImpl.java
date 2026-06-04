/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.browse.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.IpConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.IPSeeker;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.doc.browse.dao.DocumentBrowseHistoryDao;
import com.skyeye.doc.browse.entity.DocumentBrowseHistory;
import com.skyeye.doc.browse.service.DocumentBrowseHistoryService;
import com.skyeye.doc.document.entity.Document;
import com.skyeye.doc.document.enums.DocumentType;
import com.skyeye.doc.document.service.DocumentService;
import com.skyeye.doc.member.service.DocMemberService;
import com.skyeye.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 文档浏览历史服务实现
 */
@Service
@Slf4j
@SkyeyeService(name = "文档浏览历史", groupName = "文档管理", tenant = TenantEnum.PLATE, allowDynamicAttrKey = false)
public class DocumentBrowseHistoryServiceImpl extends SkyeyeBusinessServiceImpl<DocumentBrowseHistoryDao, DocumentBrowseHistory> implements DocumentBrowseHistoryService {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocMemberService docMemberService;

    @Autowired
    private Executor docMemberBrowseHistoryExecutor;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        documentService.setMationForMap(beans, "documentId", "documentMation");
        docMemberService.setMationForMap(beans, "memberId", "memberMation");
        return beans;
    }

    @Override
    protected QueryWrapper<DocumentBrowseHistory> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DocumentBrowseHistory> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getCreateId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentBrowseHistory::getMemberId), commonPageInfo.getCreateId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentBrowseHistory::getDocumentId), commonPageInfo.getObjectId());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(DocumentBrowseHistory::getLastViewTime));
        return queryWrapper;
    }

    @Override
    public void recordDocumentBrowseHistory(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String documentId = params.get("documentId").toString();
        String userId = inputObject.getLogParams().get("id").toString();

        Document document = documentService.selectById(documentId);
        if (ObjectUtil.isEmpty(document) || StrUtil.isEmpty(document.getId())) {
            outputObject.setreturnMessage("文档不存在");
            return;
        }
        if (!DocumentType.DOCUMENT.getKey().equals(document.getType())) {
            return;
        }

        HttpServletRequest request = PutObject.getRequest();
        String clientIp = resolveClientIp(request);
        docMemberBrowseHistoryExecutor.execute(() -> {
            TenantContext.setTenantId(TenantTypeEnum.PLATFORM.getCode());
            try {
                saveOrUpdateBrowseHistory(userId, documentId, clientIp);
            } catch (Exception e) {
                log.error("记录文档浏览历史失败，用户ID：{}，文档ID：{}，错误信息：{}", userId, documentId, e.getMessage(), e);
            }
        });
    }

    private String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return StrUtil.EMPTY;
        }
        String clientIp = ToolUtil.getIpByRequest(request);
        if (StrUtil.isEmpty(clientIp)) {
            return StrUtil.EMPTY;
        }
        for (String filterIp : IpConstants.FILTER_FILE_IP_OPTION) {
            if (clientIp.equals(filterIp) || clientIp.contains(filterIp)) {
                return "127.0.0.1";
            }
        }
        return clientIp;
    }

    private String resolveCityByIp(String clientIp) {
        if (StrUtil.isEmpty(clientIp) || clientIp.contains(":")) {
            return StrUtil.EMPTY;
        }
        try {
            String address = IPSeeker.getCountry(clientIp);
            if (StrUtil.isEmpty(address)) {
                return StrUtil.EMPTY;
            }
            return IPSeeker.getCurCityByCountry(address);
        } catch (Exception e) {
            log.warn("解析IP城市失败，IP：{}，错误信息：{}", clientIp, e.getMessage());
            return StrUtil.EMPTY;
        }
    }

    private void saveOrUpdateBrowseHistory(String userId, String documentId, String clientIp) {
        QueryWrapper<DocumentBrowseHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentBrowseHistory::getMemberId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentBrowseHistory::getDocumentId), documentId);
        DocumentBrowseHistory existing = getOne(queryWrapper);

        String city = resolveCityByIp(clientIp);
        String now = DateUtil.getTimeAndToString();

        if (existing != null && StrUtil.isNotEmpty(existing.getId())) {
            int viewCount = existing.getViewCount() == null ? 1 : existing.getViewCount() + 1;
            UpdateWrapper<DocumentBrowseHistory> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, existing.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(DocumentBrowseHistory::getViewCount), viewCount);
            updateWrapper.set(MybatisPlusUtil.toColumns(DocumentBrowseHistory::getLastViewTime), now);
            updateWrapper.set(MybatisPlusUtil.toColumns(DocumentBrowseHistory::getIp), clientIp);
            updateWrapper.set(MybatisPlusUtil.toColumns(DocumentBrowseHistory::getCity), city);
            update(updateWrapper);
            refreshCache(existing.getId());
            return;
        }

        DocumentBrowseHistory history = new DocumentBrowseHistory();
        history.setMemberId(userId);
        history.setDocumentId(documentId);
        history.setViewCount(1);
        history.setLastViewTime(now);
        history.setIp(clientIp);
        history.setCity(city);
        createEntity(history, userId);
    }

    @Override
    public void queryMyDocumentBrowseHistoryList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = inputObject.getLogParams().get("id").toString();

        Page<?> pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DocumentBrowseHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentBrowseHistory::getMemberId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(DocumentBrowseHistory::getLastViewTime));
        List<DocumentBrowseHistory> list = list(queryWrapper);
        List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(list), null);
        documentService.setMationForMap(beans, "documentId", "documentMation");

        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void deleteMyDocumentBrowseHistoryById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        DocumentBrowseHistory history = selectById(id);
        if (ObjectUtil.isEmpty(history) || StrUtil.isEmpty(history.getId())) {
            throw new CustomException("记录不存在");
        }
        if (!userId.equals(history.getMemberId())) {
            throw new CustomException("无权删除该记录");
        }
        deleteById(id);
        refreshCache(id);
    }

    @Override
    public void clearMyDocumentBrowseHistory(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<DocumentBrowseHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentBrowseHistory::getMemberId), userId);
        List<DocumentBrowseHistory> list = list(queryWrapper);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (DocumentBrowseHistory history : list) {
            deleteById(history.getId());
            refreshCache(history.getId());
        }
    }

}
