/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.dao;

import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.forum.entity.ForumTag;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ForumTagDao
 * @Description: 论坛标签管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 9:09
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

public interface ForumTagDao extends SkyeyeBaseMapper<ForumTag> {

    List<Map<String, Object>> queryForumTagList(Map<String, Object> map);

    Map<String, Object> queryForumTagMationByName(Map<String, Object> map);

    int insertForumTagMation(Map<String, Object> map);

    Map<String, Object> queryForumTagBySimpleLevel(Map<String, Object> map);

    int deleteForumTagById(Map<String, Object> map);

    int updateUpForumTagById(Map<String, Object> map);

    int updateDownForumTagById(Map<String, Object> map);

    Map<String, Object> selectForumTagById(Map<String, Object> map);

    int editForumTagMationById(Map<String, Object> map);

    Map<String, Object> queryForumTagUpMationById(Map<String, Object> map);

    int editForumTagMationOrderNumUpById(Map<String, Object> map);

    Map<String, Object> queryForumTagDownMationById(Map<String, Object> map);

    Map<String, Object> queryForumTagStateById(Map<String, Object> map);

    List<Map<String, Object>> queryForumTagUpStateList(Map<String, Object> map);

}
