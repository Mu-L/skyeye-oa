/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.browse.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.doc.browse.dao.DocumentBrowseHistoryDao;
import com.skyeye.doc.browse.entity.DocumentBrowseHistory;
import com.skyeye.doc.browse.service.DocumentBrowseHistoryStatisticsService;
import com.skyeye.doc.document.dao.DocumentDao;
import com.skyeye.doc.document.entity.Document;
import com.skyeye.doc.member.dao.DocMemberDao;
import com.skyeye.doc.member.entity.DocMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文档浏览历史统计分析服务实现
 * <p>
 * 入参：{@link TableSelectInfo}（startTime/endTime 可选，闭区间过滤）
 * 出参：合计类 {@code bean.total}；图表类 {@code bean.total}、{@code xAxisData}、{@code seriesData}
 * </p>
 */
@Service
public class DocumentBrowseHistoryStatisticsServiceImpl implements DocumentBrowseHistoryStatisticsService {

    private static final String UNKNOWN_CITY = "未知";
    private static final String OTHER_LABEL = "其他";
    private static final int TOP_LIMIT = 10;

    @Autowired
    private DocumentBrowseHistoryDao documentBrowseHistoryDao;

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private DocMemberDao docMemberDao;

    @Override
    public void queryBrowseHistoryTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        outputTotalResult(outputObject, documentBrowseHistoryDao.selectCount(buildCreateTimeRangeWrapper(params)));
    }

    @Override
    public void queryBrowseViewCountTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        List<DocumentBrowseHistory> list = documentBrowseHistoryDao.selectList(buildLastViewTimeRangeWrapper(params));
        long total = list.stream()
            .mapToLong(item -> item.getViewCount() == null ? 0L : item.getViewCount())
            .sum();
        outputTotalResult(outputObject, total);
    }

    @Override
    public void queryBrowseMemberTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        List<DocumentBrowseHistory> list = documentBrowseHistoryDao.selectList(buildCreateTimeRangeWrapper(params));
        long total = list.stream()
            .map(DocumentBrowseHistory::getMemberId)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .count();
        outputTotalResult(outputObject, total);
    }

    @Override
    public void queryBrowseDocumentTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        List<DocumentBrowseHistory> list = documentBrowseHistoryDao.selectList(buildCreateTimeRangeWrapper(params));
        long total = list.stream()
            .map(DocumentBrowseHistory::getDocumentId)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .count();
        outputTotalResult(outputObject, total);
    }

    @Override
    public void queryBrowseRevisitTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        List<DocumentBrowseHistory> list = documentBrowseHistoryDao.selectList(buildCreateTimeRangeWrapper(params));
        long total = list.stream()
            .filter(item -> item.getViewCount() != null && item.getViewCount() > 1)
            .count();
        outputTotalResult(outputObject, total);
    }

    @Override
    public void queryBrowseHistoryStatsByCreateTime(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        List<DocumentBrowseHistory> list = documentBrowseHistoryDao.selectList(buildCreateTimeRangeWrapper(params));
        outputDayTrendResult(outputObject, list.stream()
            .map(DocumentBrowseHistory::getCreateTime)
            .collect(Collectors.toList()));
    }

    @Override
    public void queryBrowseHistoryStatsByLastViewTime(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        List<DocumentBrowseHistory> list = documentBrowseHistoryDao.selectList(buildLastViewTimeRangeWrapper(params));
        outputDayTrendResult(outputObject, list.stream()
            .map(DocumentBrowseHistory::getLastViewTime)
            .collect(Collectors.toList()));
    }

    @Override
    public void queryBrowseStatsByDocument(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        List<DocumentBrowseHistory> list = documentBrowseHistoryDao.selectList(buildCreateTimeRangeWrapper(params));
        long total = list.size();

        Map<String, Long> documentStats = list.stream()
            .collect(Collectors.groupingBy(
                item -> StrUtil.isNotEmpty(item.getDocumentId()) ? item.getDocumentId() : OTHER_LABEL,
                Collectors.summingLong(item -> item.getViewCount() == null ? 0L : item.getViewCount())));

        outputTopChartResult(outputObject, total, documentStats, this::resolveDocumentNames);
    }

    @Override
    public void queryBrowseStatsByCity(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        List<DocumentBrowseHistory> list = documentBrowseHistoryDao.selectList(buildCreateTimeRangeWrapper(params));
        long total = list.size();

        Map<String, Long> cityStats = list.stream()
            .collect(Collectors.groupingBy(
                item -> StrUtil.isNotEmpty(item.getCity()) ? item.getCity() : UNKNOWN_CITY,
                Collectors.counting()));

        List<Map.Entry<String, Long>> entries = new ArrayList<>(cityStats.entrySet());
        entries.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : entries) {
            xAxisData.add(entry.getKey());
            seriesData.add(entry.getValue());
        }
        outputChartResult(outputObject, total, xAxisData, seriesData);
    }

    @Override
    public void queryBrowseStatsByMember(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        List<DocumentBrowseHistory> list = documentBrowseHistoryDao.selectList(buildCreateTimeRangeWrapper(params));
        long total = list.size();

        Map<String, Long> memberStats = list.stream()
            .collect(Collectors.groupingBy(
                item -> StrUtil.isNotEmpty(item.getMemberId()) ? item.getMemberId() : OTHER_LABEL,
                Collectors.summingLong(item -> item.getViewCount() == null ? 0L : item.getViewCount())));

        outputTopChartResult(outputObject, total, memberStats, this::resolveMemberNames);
    }

    private QueryWrapper<DocumentBrowseHistory> buildCreateTimeRangeWrapper(TableSelectInfo params) {
        return buildTimeRangeWrapper(params, MybatisPlusUtil.toColumns(DocumentBrowseHistory::getCreateTime));
    }

    private QueryWrapper<DocumentBrowseHistory> buildLastViewTimeRangeWrapper(TableSelectInfo params) {
        return buildTimeRangeWrapper(params, MybatisPlusUtil.toColumns(DocumentBrowseHistory::getLastViewTime));
    }

    private QueryWrapper<DocumentBrowseHistory> buildTimeRangeWrapper(TableSelectInfo params, String timeColumn) {
        QueryWrapper<DocumentBrowseHistory> queryWrapper = new QueryWrapper<>();
        if (params == null) {
            return queryWrapper;
        }
        if (StrUtil.isNotEmpty(params.getStartTime())) {
            queryWrapper.ge(timeColumn, params.getStartTime());
        }
        if (StrUtil.isNotEmpty(params.getEndTime())) {
            queryWrapper.le(timeColumn, params.getEndTime());
        }
        return queryWrapper;
    }

    private void outputTotalResult(OutputObject outputObject, long total) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private void outputChartResult(OutputObject outputObject, long total, List<String> xAxisData, List<Long> seriesData) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private void outputDayTrendResult(OutputObject outputObject, List<String> times) {
        Map<String, Long> dayStats = times.stream()
            .filter(StrUtil::isNotEmpty)
            .collect(Collectors.groupingBy(
                time -> time.length() >= 10 ? time.substring(0, 10) : time,
                Collectors.counting()));

        List<String> xAxisData = new ArrayList<>(dayStats.keySet());
        xAxisData.sort(Comparator.naturalOrder());

        List<Long> seriesData = new ArrayList<>();
        for (String day : xAxisData) {
            seriesData.add(dayStats.get(day));
        }
        outputChartResult(outputObject, times.size(), xAxisData, seriesData);
    }

    private void outputTopChartResult(OutputObject outputObject, long total, Map<String, Long> stats,
                                      java.util.function.Function<List<String>, Map<String, String>> nameResolver) {
        List<Map.Entry<String, Long>> entries = new ArrayList<>(stats.entrySet());
        entries.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        List<Map.Entry<String, Long>> topEntries = entries.size() > TOP_LIMIT
            ? entries.subList(0, TOP_LIMIT)
            : entries;

        List<String> ids = topEntries.stream()
            .map(Map.Entry::getKey)
            .filter(id -> !OTHER_LABEL.equals(id))
            .collect(Collectors.toList());

        Map<String, String> idToName = nameResolver.apply(ids);

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : topEntries) {
            String id = entry.getKey();
            xAxisData.add(OTHER_LABEL.equals(id) ? OTHER_LABEL : idToName.getOrDefault(id, id));
            seriesData.add(entry.getValue());
        }
        outputChartResult(outputObject, total, xAxisData, seriesData);
    }

    private Map<String, String> resolveDocumentNames(List<String> documentIds) {
        Map<String, String> idToName = new HashMap<>();
        if (documentIds == null || documentIds.isEmpty()) {
            return idToName;
        }
        List<Document> documents = documentDao.selectBatchIds(documentIds);
        if (documents == null) {
            return idToName;
        }
        for (Document document : documents) {
            String name = StrUtil.isNotEmpty(document.getName()) ? document.getName() : document.getId();
            idToName.put(document.getId(), name);
        }
        return idToName;
    }

    private Map<String, String> resolveMemberNames(List<String> memberIds) {
        Map<String, String> idToName = new HashMap<>();
        if (memberIds == null || memberIds.isEmpty()) {
            return idToName;
        }
        List<DocMember> members = docMemberDao.selectBatchIds(memberIds);
        if (members == null) {
            return idToName;
        }
        for (DocMember member : members) {
            String name = StrUtil.isNotEmpty(member.getName()) ? member.getName() : member.getId();
            idToName.put(member.getId(), name);
        }
        return idToName;
    }

}
