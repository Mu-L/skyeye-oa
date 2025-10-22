/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.notice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.doc.notice.dao.NoticeDao;
import com.skyeye.doc.notice.entity.Notice;
import com.skyeye.doc.notice.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: NoticeServiceImpl
 * @Description: 消息通知服务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/21 20:52
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "消息通知", groupName = "消息通知", tenant = TenantEnum.PLATE)
public class NoticeServiceImpl extends SkyeyeBusinessServiceImpl<NoticeDao, Notice> implements NoticeService {

    @Override
    public void addNotice(String receiveId, String title, String content) {
        Notice notice = new Notice();
        notice.setReceiveId(receiveId);
        notice.setName(title);
        notice.setRemark(content);
        notice.setIsRead(WhetherEnum.DISABLE_USING.getKey());
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        createEntity(notice, userId);
    }

    @Override
    protected QueryWrapper<Notice> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Notice> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getReceiveId), userId);
        return queryWrapper;
    }

    @Override
    public void queryNotReadNoticeNum(InputObject inputObject, OutputObject outputObject) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getReceiveId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getIsRead), WhetherEnum.DISABLE_USING.getKey());
        long count = count(queryWrapper);
        outputObject.settotal(count);
    }

    @Override
    public void markAllNoticeAsRead(InputObject inputObject, OutputObject outputObject) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 查询未读消息
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getReceiveId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getIsRead), WhetherEnum.DISABLE_USING.getKey());
        List<Notice> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        // 更新消息状态为已读
        UpdateWrapper<Notice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(Notice::getReceiveId), userId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(Notice::getIsRead), WhetherEnum.DISABLE_USING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(Notice::getIsRead), WhetherEnum.ENABLE_USING.getKey());
        update(updateWrapper);
        // 刷新缓存
        List<String> ids = list.stream().map(Notice::getId).collect(Collectors.toList());
        refreshCache(ids);
    }

    @Override
    public Notice selectById(String id) {
        Notice notice = super.selectById(id);
        if (notice.getIsRead().equals(WhetherEnum.DISABLE_USING.getKey())) {
            // 更新消息状态为已读
            UpdateWrapper<Notice> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, notice.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(Notice::getIsRead), WhetherEnum.ENABLE_USING.getKey());
            update(updateWrapper);
            refreshCache(id);
        }
        return notice;
    }
}
