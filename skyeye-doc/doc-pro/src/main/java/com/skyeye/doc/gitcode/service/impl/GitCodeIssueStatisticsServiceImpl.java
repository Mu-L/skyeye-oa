/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.gitcode.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.doc.code.dao.CodeVersionDao;
import com.skyeye.doc.code.entity.CodeVersion;
import com.skyeye.doc.gitcode.dao.GitCodeIssueCommentDao;
import com.skyeye.doc.gitcode.dao.GitCodeIssueDao;
import com.skyeye.doc.gitcode.entity.GitCodeIssue;
import com.skyeye.doc.gitcode.entity.GitCodeIssueComment;
import com.skyeye.doc.gitcode.service.GitCodeIssueStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 问答社区统计分析服务实现
 * <p>
 * 入参：{@link TableSelectInfo}（startTime/endTime 可选，闭区间过滤 create_time）
 * 出参：合计类 {@code bean.total}；图表类 {@code bean.total}、{@code xAxisData}、{@code seriesData}
 * </p>
 */
@Service
public class GitCodeIssueStatisticsServiceImpl implements GitCodeIssueStatisticsService {

    private static final String OTHER_LABEL = "其他";
    private static final String LABEL_NORMAL = "普通问答";
    private static final String LABEL_BUG = "Bug";
    private static final String LABEL_REQUIREMENT = "需求";
    private static final String LABEL_BOTH = "Bug+需求";

    @Autowired
    private GitCodeIssueDao gitCodeIssueDao;

    @Autowired
    private GitCodeIssueCommentDao gitCodeIssueCommentDao;

    @Autowired
    private CodeVersionDao codeVersionDao;

    @Override
    public void queryIssueTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        long total = gitCodeIssueDao.selectCount(buildIssueTimeRangeWrapper(params));
        outputTotalResult(outputObject, total);
    }

    @Override
    public void queryIssueCommentTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        long total = gitCodeIssueCommentDao.selectCount(buildCommentTimeRangeWrapper(params));
        outputTotalResult(outputObject, total);
    }

    @Override
    public void queryIssueBugTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<GitCodeIssue> qw = buildIssueTimeRangeWrapper(params);
        qw.eq(MybatisPlusUtil.toColumns(GitCodeIssue::getRecordBug), WhetherEnum.ENABLE_USING.getKey());
        outputTotalResult(outputObject, gitCodeIssueDao.selectCount(qw));
    }

    @Override
    public void queryIssueRequirementTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<GitCodeIssue> qw = buildIssueTimeRangeWrapper(params);
        qw.eq(MybatisPlusUtil.toColumns(GitCodeIssue::getRecordRequirement), WhetherEnum.ENABLE_USING.getKey());
        outputTotalResult(outputObject, gitCodeIssueDao.selectCount(qw));
    }

    @Override
    public void queryIssueBugCompletedTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<GitCodeIssue> qw = buildIssueTimeRangeWrapper(params);
        qw.eq(MybatisPlusUtil.toColumns(GitCodeIssue::getRecordBug), WhetherEnum.ENABLE_USING.getKey());
        qw.eq(MybatisPlusUtil.toColumns(GitCodeIssue::getBugCompleted), WhetherEnum.ENABLE_USING.getKey());
        outputTotalResult(outputObject, gitCodeIssueDao.selectCount(qw));
    }

    @Override
    public void queryIssueRequirementCompletedTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<GitCodeIssue> qw = buildIssueTimeRangeWrapper(params);
        qw.eq(MybatisPlusUtil.toColumns(GitCodeIssue::getRecordRequirement), WhetherEnum.ENABLE_USING.getKey());
        qw.eq(MybatisPlusUtil.toColumns(GitCodeIssue::getRequirementCompleted), WhetherEnum.ENABLE_USING.getKey());
        outputTotalResult(outputObject, gitCodeIssueDao.selectCount(qw));
    }

    @Override
    public void queryIssueStatsByCreateTime(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        List<GitCodeIssue> list = gitCodeIssueDao.selectList(buildIssueTimeRangeWrapper(params));
        outputDayTrendResult(outputObject, list.stream()
            .map(GitCodeIssue::getCreateTime)
            .collect(Collectors.toList()));
    }

    @Override
    public void queryIssueCommentStatsByCreateTime(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        List<GitCodeIssueComment> list = gitCodeIssueCommentDao.selectList(buildCommentTimeRangeWrapper(params));
        outputDayTrendResult(outputObject, list.stream()
            .map(GitCodeIssueComment::getCreateTime)
            .collect(Collectors.toList()));
    }

    @Override
    public void queryIssueStatsByVersion(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        List<GitCodeIssue> list = gitCodeIssueDao.selectList(buildIssueTimeRangeWrapper(params));
        long total = list.size();

        Map<String, Long> versionStats = list.stream()
            .collect(Collectors.groupingBy(
                issue -> StrUtil.isNotEmpty(issue.getVersionId()) ? issue.getVersionId() : OTHER_LABEL,
                Collectors.counting()));

        List<String> versionIds = versionStats.keySet().stream()
            .filter(id -> !OTHER_LABEL.equals(id))
            .collect(Collectors.toList());

        Map<String, String> idToName = new HashMap<>();
        if (!versionIds.isEmpty()) {
            List<CodeVersion> versions = codeVersionDao.selectBatchIds(versionIds);
            if (versions != null) {
                for (CodeVersion version : versions) {
                    String name = StrUtil.isNotEmpty(version.getName()) ? version.getName() : version.getId();
                    idToName.put(version.getId(), name);
                }
            }
        }

        List<Map.Entry<String, Long>> entries = new ArrayList<>(versionStats.entrySet());
        entries.sort(Comparator.comparing(e -> {
            if (OTHER_LABEL.equals(e.getKey())) {
                return OTHER_LABEL;
            }
            return idToName.getOrDefault(e.getKey(), e.getKey());
        }));

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : entries) {
            String versionId = entry.getKey();
            xAxisData.add(OTHER_LABEL.equals(versionId) ? OTHER_LABEL : idToName.getOrDefault(versionId, versionId));
            seriesData.add(entry.getValue());
        }
        outputChartResult(outputObject, total, xAxisData, seriesData);
    }

    @Override
    public void queryIssueStatsByRecordType(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        List<GitCodeIssue> list = gitCodeIssueDao.selectList(buildIssueTimeRangeWrapper(params));
        long total = list.size();

        long bothCount = 0L;
        long bugOnlyCount = 0L;
        long requirementOnlyCount = 0L;
        long normalCount = 0L;

        for (GitCodeIssue issue : list) {
            boolean recordBug = isEnabled(issue.getRecordBug());
            boolean recordRequirement = isEnabled(issue.getRecordRequirement());
            if (recordBug && recordRequirement) {
                bothCount++;
            } else if (recordBug) {
                bugOnlyCount++;
            } else if (recordRequirement) {
                requirementOnlyCount++;
            } else {
                normalCount++;
            }
        }

        List<String> xAxisData = Arrays.asList(LABEL_NORMAL, LABEL_BUG, LABEL_REQUIREMENT, LABEL_BOTH);
        List<Long> seriesData = Arrays.asList(normalCount, bugOnlyCount, requirementOnlyCount, bothCount);
        outputChartResult(outputObject, total, xAxisData, seriesData);
    }

    private QueryWrapper<GitCodeIssue> buildIssueTimeRangeWrapper(TableSelectInfo params) {
        return buildTimeRangeWrapper(params, MybatisPlusUtil.toColumns(GitCodeIssue::getCreateTime));
    }

    private QueryWrapper<GitCodeIssueComment> buildCommentTimeRangeWrapper(TableSelectInfo params) {
        return buildTimeRangeWrapper(params, MybatisPlusUtil.toColumns(GitCodeIssueComment::getCreateTime));
    }

    private <T> QueryWrapper<T> buildTimeRangeWrapper(TableSelectInfo params, String createTimeColumn) {
        QueryWrapper<T> qw = new QueryWrapper<>();
        if (params == null) {
            return qw;
        }
        if (StrUtil.isNotEmpty(params.getStartTime())) {
            qw.ge(createTimeColumn, params.getStartTime());
        }
        if (StrUtil.isNotEmpty(params.getEndTime())) {
            qw.le(createTimeColumn, params.getEndTime());
        }
        return qw;
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

    private void outputDayTrendResult(OutputObject outputObject, List<String> createTimes) {
        Map<String, Long> dayStats = createTimes.stream()
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
        outputChartResult(outputObject, createTimes.size(), xAxisData, seriesData);
    }

    private boolean isEnabled(Integer value) {
        return value != null && WhetherEnum.ENABLE_USING.getKey().equals(value);
    }

}
