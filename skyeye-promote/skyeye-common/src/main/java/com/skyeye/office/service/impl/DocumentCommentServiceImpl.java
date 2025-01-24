package com.skyeye.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
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
public class DocumentCommentServiceImpl extends SkyeyeBusinessServiceImpl<DocumentComment> implements DocumentCommentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addComment(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");
        String content = inputObject.getParams().getString("content");
        String parentId = inputObject.getParams().getString("parentId");

        DocumentComment comment = new DocumentComment();
        comment.setDocumentId(documentId);
        comment.setContent(content);
        comment.setParentId(parentId);

        super.createEntity(comment);
        outputObject.setBean(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().getString("id");

        // 删除评论及其所有回复
        super.deleteByWrapper(ToolUtil.getWrapper(DocumentComment.class)
            .eq("id", id)
            .or()
            .eq("parent_id", id));
    }

    @Override
    public void getCommentList(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");

        // 查询主评论（没有父评论的评论）
        List<DocumentComment> comments = super.selectList(ToolUtil.getWrapper(DocumentComment.class)
            .eq("document_id", documentId)
            .isNull("parent_id")
            .orderByDesc("create_time"));

        // 获取每个评论的回复数量
        if (!comments.isEmpty()) {
            List<String> commentIds = comments.stream()
                .map(DocumentComment::getId)
                .collect(Collectors.toList());

            QueryWrapper<DocumentComment> replyWrapper = new QueryWrapper<>();
            replyWrapper.select("parent_id, count(1) as reply_count")
                .in("parent_id", commentIds)
                .groupBy("parent_id");

            List<Map<String, Object>> replyCounts = super.selectMaps(replyWrapper);
            Map<String, Integer> replyCountMap = replyCounts.stream()
                .collect(Collectors.toMap(
                    map -> map.get("parent_id").toString(),
                    map -> Integer.valueOf(map.get("reply_count").toString())
                ));

            comments.forEach(comment -> 
                comment.setReplyCount(replyCountMap.getOrDefault(comment.getId(), 0)));
        }

        outputObject.setBean(comments);
    }

    @Override
    public void getReplyList(InputObject inputObject, OutputObject outputObject) {
        String parentId = inputObject.getParams().getString("parentId");

        // 查询指定评论的所有回复
        List<DocumentComment> replies = super.selectList(ToolUtil.getWrapper(DocumentComment.class)
            .eq("parent_id", parentId)
            .orderByAsc("create_time"));

        outputObject.setBean(replies);
    }
} 