package com.skyeye.eve.forum.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.forum.dao.ForumCommentDao;
import com.skyeye.eve.forum.entity.ForumComment;
import com.skyeye.eve.forum.service.ForumCommentService;
import com.skyeye.eve.service.IAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "论坛评论管理", groupName = "论坛评论管理")
public class ForumCommentServiceImpl extends SkyeyeBusinessServiceImpl<ForumCommentDao, ForumComment> implements ForumCommentService {

    @Autowired
    private ForumCommentService forumCommentService;

    @Autowired
    private IAuthUserService iAuthUserService;


    public void createPrepose(ForumComment forumComment) {
        String currentUserId = InputObject.getLogParamsStatic().get("id").toString();
        forumComment.setCommentId(currentUserId);
    }


    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        String forumId = inputObject.getParams().get("id").toString();
        QueryWrapper<ForumComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumComment::getForumId), forumId)
            .orderByDesc(MybatisPlusUtil.toColumns(ForumComment::getCreateTime));
        List<ForumComment> commentList = list(queryWrapper);
        List<Map<String, Object>> beans = commentList.stream().map(forumComment -> {
            return JSONUtil.<Map<String, Object>>toBean(JSONUtil.toJsonStr(forumComment), null);
        }).collect(Collectors.toList());
        // 设置评论人信息和回复人信息
        iAuthUserService.setMationForMap(beans, "commentId","commentNation");
        iAuthUserService.setMationForMap(beans, "replyId","replyNation");
        return beans;
    }

    @Override
    public Integer countNumByForumId(String forumId){
        QueryWrapper<ForumComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumComment::getForumId), forumId);
        long count = count(queryWrapper);
        return (int) count;
    }
}
