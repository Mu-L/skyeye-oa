/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.dao;

import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.forum.entity.ForumSensitiveWords;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ForumReportDao
 * @Description: 论坛敏感词管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ForumSensitiveWordsDao extends SkyeyeBaseMapper<ForumSensitiveWords> {

    List<Map<String, Object>> queryForumSensitiveWordsList(Map<String, Object> map);

    Map<String, Object> queryForumSensitiveWordsMationByName(Map<String, Object> map);

    int insertForumSensitiveWordsMation(Map<String, Object> map);

    int deleteForumSensitiveWordsById(Map<String, Object> map);

    Map<String, Object> selectForumSensitiveWordsById(Map<String, Object> map);

    int editForumSensitiveWordsMationById(Map<String, Object> map);

    List<Map<String, Object>> queryForumSensitiveWordsListAll();
}
