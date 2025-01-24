package com.skyeye.office.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.office.entity.DocumentComment;

/**
 * @ClassName: DocumentCommentService
 * @Description: 文档评论服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
public interface DocumentCommentService extends SkyeyeBusinessService<DocumentComment> {

    void addComment(InputObject inputObject, OutputObject outputObject);

    void deleteComment(InputObject inputObject, OutputObject outputObject);

    void getCommentList(InputObject inputObject, OutputObject outputObject);

    void getReplyList(InputObject inputObject, OutputObject outputObject);
} 