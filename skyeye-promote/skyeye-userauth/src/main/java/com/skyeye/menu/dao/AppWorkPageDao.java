/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.menu.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.menu.entity.AppWorkPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AppWorkPageDao
 * @Description: 手机端菜单以及目录功能接口类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/10 23:19
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface AppWorkPageDao extends SkyeyeBaseMapper<AppWorkPage> {

    List<Map<String, Object>> queryAppWorkPageList(CommonPageInfo commonPageInfo);

    @IgnoreTenant
    List<String> queryAllChildIdsByParentId(@Param("ids") List<String> ids);

    List<Map<String, Object>> queryAllAppMenuList();

}
