package com.skyeye.eve.forum.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.eve.forum.dao.ForumNoticeDao;
import com.skyeye.eve.forum.entity.ForumNotice;
import com.skyeye.eve.forum.service.ForumNoticeService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "论坛通知管理", groupName = "论坛通知管理")
public class ForumNoticeServiceImpl extends SkyeyeBusinessServiceImpl<ForumNoticeDao, ForumNotice> implements ForumNoticeService {
}
