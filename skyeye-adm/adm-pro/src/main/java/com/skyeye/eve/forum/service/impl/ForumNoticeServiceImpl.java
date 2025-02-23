package com.skyeye.eve.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.forum.classenum.NoticeStateEnum;
import com.skyeye.eve.forum.dao.ForumNoticeDao;
import com.skyeye.eve.forum.entity.ForumContent;
import com.skyeye.eve.forum.entity.ForumNotice;
import com.skyeye.eve.forum.service.ForumContentService;
import com.skyeye.eve.forum.service.ForumNoticeService;
import com.skyeye.eve.service.IAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;



/**
 * @ClassName: ForumNoticeServiceImpl
 * @Description: 论坛通知管理实现层
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/9 9:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "论坛通知管理", groupName = "论坛通知管理")
public class ForumNoticeServiceImpl extends SkyeyeBusinessServiceImpl<ForumNoticeDao, ForumNotice> implements ForumNoticeService {

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private ForumContentService forumContentService;

    @Override
    public void queryMyNoticeList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ForumNotice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumNotice::getState), NoticeStateEnum.NOT_READ.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumNotice::getCreateId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ForumNotice::getCreateTime));
        List<ForumNotice> bean = list(queryWrapper);
        iAuthUserService.setName(bean, "createId", "createName");
        iAuthUserService.setName(bean, "updateId", "updateName");
        for (ForumNotice forumNotice : bean) {
            ForumContent forumContent = forumContentService.selectById(forumNotice.getForumId());
            forumNotice.setForumContentMation(forumContent);
        }
        outputObject.setBeans(bean);
        outputObject.settotal(page.getTotal());
    }
}
