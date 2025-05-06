package com.skyeye.video.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.video.entity.VideoView;

import java.util.List;

/**
 * @ClassName: VideoViewService
 * @Description: 视频观看记录服务接口
 * @author: lqy
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface VideoViewService extends SkyeyeBusinessService<VideoView> {

    void deleteAllVideoView(InputObject inputObject, OutputObject outputObject);

    void queryUserVideoView(InputObject inputObject, OutputObject outputObject);

    List<VideoView> queryVideoViewByUserId(String userId);

    void deleteByVideoId(String id);
}
