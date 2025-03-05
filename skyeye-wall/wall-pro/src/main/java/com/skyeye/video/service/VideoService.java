package com.skyeye.video.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.video.entity.Video;

/**
 * @ClassName: VideoService
 * @Description: 视频管理服务接口层
 * @author: lqy
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface VideoService extends SkyeyeBusinessService<Video> {
    void supportOrNotVideo(InputObject inputObject, OutputObject outputObject);

    void collectOrNotVideo(InputObject inputObject, OutputObject outputObject);

    void queryAllSupportVideo(InputObject inputObject, OutputObject outputObject);

    void queryAllCollectVideo(InputObject inputObject, OutputObject outputObject);

    void refreshVisitVideo(InputObject inputObject, OutputObject outputObject);

    void queryAllVideoList(InputObject inputObject, OutputObject outputObject);
}
