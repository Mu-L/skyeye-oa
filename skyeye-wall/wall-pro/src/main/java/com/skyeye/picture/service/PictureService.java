package com.skyeye.picture.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.picture.entity.Picture;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: PictureService
 * @Description: 图片服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PictureService extends SkyeyeBusinessService<Picture> {

    Map<String, List<Picture>> getPictureMapListByIds(List<String> postIds);

    List<Picture> queryLinkListByPostId(String id);

    void deleteByPostId(String id);

    void deleteByPostIds(List<String> postIds);

    void deleteByCommentIds(List<String> ids);

    Picture getPictureByObjectId(String commentId);
}