package com.skyeye.office.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.office.dao.DocumentCommentDao;
import com.skyeye.office.entity.DocumentComment;
import com.skyeye.office.service.DocumentCommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: DocumentCommentServiceImpl
 * @Description: 文档评论服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@Service
public class DocumentCommentServiceImpl extends SkyeyeBusinessServiceImpl<DocumentCommentDao,DocumentComment> implements DocumentCommentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addComment(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String documentId = map.get("documentId").toString();
        String content = map.get("content").toString();
        String parentId = map.get("parentId").toString();
        DocumentComment comment = new DocumentComment();
        comment.setDocumentId(documentId);
        comment.setContent(content);
        comment.setParentId(parentId);
        super.createEntity(comment, StrUtil.EMPTY);
        outputObject.setBean(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        QueryWrapper<DocumentComment> queryWrapper = new QueryWrapper<>();
        // 删除评论及其所有回复
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentComment::getDocumentId), id)
                .or()
                .eq(MybatisPlusUtil.toColumns(DocumentComment::getParentId), id);
        remove(queryWrapper);
    }

    @Override
    public void getCommentList(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().get("documentId").toString();
        QueryWrapper<DocumentComment> queryWrapper = new QueryWrapper<>();
        // 查询主评论（没有父评论的评论）
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentComment::getDocumentId), documentId)
                .isNull(MybatisPlusUtil.toColumns(DocumentComment::getParentId))
                .orderByDesc(MybatisPlusUtil.toColumns(DocumentComment::getCreateTime));
        List<DocumentComment> comments = list(queryWrapper);
        // 获取每个评论的回复数量
        if (!comments.isEmpty()) {
            List<String> commentIds = comments.stream()
                    .map(DocumentComment::getDocumentId)
                    .collect(Collectors.toList());
            QueryWrapper<DocumentComment> replyWrapper = new QueryWrapper<>();
            replyWrapper.select(MybatisPlusUtil.toColumns(DocumentComment::getParentId),
                            "count(1) as reply_count")
                    .in(MybatisPlusUtil.toColumns(DocumentComment::getParentId), commentIds)
                    .groupBy(MybatisPlusUtil.toColumns(DocumentComment::getParentId));
            List<Map<String, Object>> replyCounts = super.baseMapper.selectMaps(replyWrapper);
            Map<String, Integer> replyCountMap = replyCounts.stream()
                    .collect(Collectors.toMap(
                            map -> map.get("parent_id").toString(),
                            map -> Integer.valueOf(map.get("reply_count").toString())
                    ));
            comments.forEach(comment ->
                    comment.setReplyCount(replyCountMap.getOrDefault(comment.getDocumentId(), 0)));
        }
        outputObject.setBean(comments);
    }

    @Override
    public void getReplyList(InputObject inputObject, OutputObject outputObject) {
        String parentId = inputObject.getParams().get("parentId").toString();
        // 查询指定评论的所有回复
        QueryWrapper<DocumentComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentComment::getParentId), parentId)
                .orderByAsc(MybatisPlusUtil.toColumns(DocumentComment::getCreateTime));
        List<DocumentComment> replies = list(queryWrapper);
        outputObject.setBean(replies);
    }
} 