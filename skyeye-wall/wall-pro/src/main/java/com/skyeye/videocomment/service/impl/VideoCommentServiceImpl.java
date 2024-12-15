package com.skyeye.videocomment.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.videocomment.dao.VideoCommentDao;
import com.skyeye.videocomment.entity.VideoComment;
import com.skyeye.videocomment.service.VideoCommentService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: VideoCommentServiceImpl
 * @Description:  视频评论业务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "视频评论管理", groupName = "视频评论管理")
public class VideoCommentServiceImpl extends SkyeyeBusinessServiceImpl<VideoCommentDao, VideoComment> implements VideoCommentService {
}
