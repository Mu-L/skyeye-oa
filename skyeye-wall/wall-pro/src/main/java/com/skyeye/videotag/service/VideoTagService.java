package com.skyeye.videotag.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.video.entity.Video;
import com.skyeye.videotag.entity.VideoTag;

import java.util.List;

/**
 * @ClassName: VideoTagService
 * @Description: 视频标签服务接口
 * @author: skyeye云系列--lqy
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

public interface VideoTagService extends SkyeyeBusinessService<VideoTag> {
    void setTagMationForVideoList(Video... beans);

    void queryAllVideoTagList(InputObject inputObject, OutputObject outputObject);
}
