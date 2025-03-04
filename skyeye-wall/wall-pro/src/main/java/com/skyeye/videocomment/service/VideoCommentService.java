package com.skyeye.videocomment.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.videocomment.entity.VideoComment;

import java.util.List;

/**
 * @ClassName: VideoCommentService
 * @Description: 视频评论服务接口
 * @author: skyeye云系列--lqy
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface VideoCommentService extends SkyeyeBusinessService<VideoComment> {

    /**
     * 根据视频ID查询视频评论列表
     */
    List<VideoComment> queryVideoCommentListByVideoId(String videoId);
}
