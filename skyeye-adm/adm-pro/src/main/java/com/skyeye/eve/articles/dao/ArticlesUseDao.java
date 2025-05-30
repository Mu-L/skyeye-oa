/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.articles.dao;

import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.articles.entity.ArticlesUse;
import com.skyeye.eve.dao.SkyeyeBaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ArticlesUseDao
 * @Description: 用品领用申请数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 9:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ArticlesUseDao extends SkyeyeBaseMapper<ArticlesUse> {

    List<Map<String, Object>> queryMyArticlesList(CommonPageInfo pageInfo);

}
