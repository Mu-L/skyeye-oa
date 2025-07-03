/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.dao.StickyNotesDao;
import com.skyeye.eve.entity.sticky.StickyNotes;
import com.skyeye.eve.service.StickyNotesService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: StickyNotesServiceImpl
 * @Description: 便签管理服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/6/23 9:01
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "便签管理", groupName = "便签管理")
public class StickyNotesServiceImpl extends SkyeyeBusinessServiceImpl<StickyNotesDao, StickyNotes> implements StickyNotesService {

    @Override
    public void queryStickyNotesList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<StickyNotes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(StickyNotes::getCreateId), inputObject.getLogParams().get("id").toString());
        List<StickyNotes> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

}
