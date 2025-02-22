/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machin.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.machin.entity.MachinChild;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MachinChildService
 * @Description: 加工单子单据服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/3 13:29
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MachinChildService extends SkyeyeBusinessService<MachinChild> {

    void deleteByParentId(String parentId);

    List<MachinChild> selectByParentId(String parentId);

    List<MachinChild> selectByParentId(List<String> parentIds);

    Map<String, List<MachinChild>> selectMapByParentId(List<String> parentIds);

    void saveList(String parentId, List<MachinChild> machinChildList);

}
