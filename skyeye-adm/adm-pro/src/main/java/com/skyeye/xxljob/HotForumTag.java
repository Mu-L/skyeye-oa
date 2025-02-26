package com.skyeye.xxljob;

import com.skyeye.eve.forum.service.ForumHotService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HotForumTag {

    @Autowired
    private ForumHotService forumHotService;

    @XxlJob("queryHotForumTagList")
    public void queryHotForumTagList() {
        forumHotService.queryHotForumTagList();
    }
}
