package com.skyeye.video.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.video.entity.Video;
import com.skyeye.video.entity.VideoRecord;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: VideoRecordService
 * @Description: 视频点赞收藏记录服务接口层
 * @author: lqy
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface VideoRecordService extends SkyeyeBusinessService<VideoRecord> {
    // 检验当前登录人是否对视频点赞 或 收藏
    boolean checkUpvoteOrCollectByUserId(Video video,int type);

    Map<String, List<String>> queryAllCollectSupportVideoIds(InputObject inputObject);

    boolean checkSupportOrCollectByVideoId(String videoId, int type);

    List<VideoRecord> queryAllSupportOrCollect(int type);

    void deleteByVideoId(String id);
}
