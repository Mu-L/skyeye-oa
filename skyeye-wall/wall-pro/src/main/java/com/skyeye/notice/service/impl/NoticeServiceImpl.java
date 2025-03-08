package com.skyeye.notice.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.comment.entity.Comment;
import com.skyeye.comment.service.CommentService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.notice.dao.NoticeDao;
import com.skyeye.notice.entity.Notice;
import com.skyeye.notice.noticeenum.NoticeTypeEnum;
import com.skyeye.notice.noticeenum.ReadEnum;
import com.skyeye.notice.noticeenum.TypeEnum;
import com.skyeye.notice.service.NoticeService;
import com.skyeye.picture.entity.Picture;
import com.skyeye.picture.service.PictureService;
import com.skyeye.user.service.UserService;
import com.skyeye.videocomment.entity.VideoComment;
import com.skyeye.videocomment.service.VideoCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: NoticeServiceImpl
 * @Description: 通知信息服务实现层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/24 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "通知信息管理", groupName = "通知信息管理")
public class NoticeServiceImpl extends SkyeyeBusinessServiceImpl<NoticeDao, Notice> implements NoticeService {

    @Autowired
    private UserService userService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private VideoCommentService videoCommentService;

    @Autowired
    private CommentService commentService;

    @Override
    protected void createPrepose(Notice entity) {
        entity.setState(ReadEnum.UNREAD.getKey());
    }

    @Override
    public String createEntity(Notice entity, String userId) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getReceiveId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getType), TypeEnum.LIKE.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getObjectId),entity.getObjectId());
        long length = count(queryWrapper);
        if (length > CommonNumConstants.NUM_ZERO) {
            return StrUtil.EMPTY;
        }
        return super.createEntity(entity, userId);
    }

    @Override
    public Notice selectById(String id) {
        Notice notice = super.selectById(id);
        userService.setDataMation(notice, Notice::getSendId);
        userService.setDataMation(notice, Notice::getReceiveId);
        return notice;
    }

    @Override
    protected void deletePreExecution(Notice entity) {
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        if(!userId.equals(entity.getReceiveId())) {
            throw new CustomException("无权限");
        }
    }

    @Override
    public void queryNoticeByType(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Integer type = Integer.valueOf(commonPageInfo.getType());
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(commonPageInfo.getType())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getNoticeType), type);
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getReceiveId), userId)
                .orderByDesc(MybatisPlusUtil.toColumns(Notice::getCreateTime));
        List<Notice> bean = list(queryWrapper);
        // 如果是评论通知找一下图片
        for (Notice notice : bean) {
            if (notice.getType() == TypeEnum.COMMENT.getKey()) {
                // 判断这个评论是否被删除
                if (notice.getNoticeType() == NoticeTypeEnum.TYPE_VIDEO.getKey()) {
                    VideoComment videoComment = videoCommentService.selectById(notice.getObjectId());
                    if (ObjectUtil.isEmpty(videoComment)) {
                        notice.setContent("该评论已被删除");
                    } else {
                        setCommentPicture(notice);
                    }
                }
                if (notice.getNoticeType() == NoticeTypeEnum.TYPE_WALL.getKey()) {
                    Comment comment = commentService.selectById(notice.getObjectId());
                    if (ObjectUtil.isEmpty(comment)) {
                        notice.setContent("该评论已被删除");
                    } else {
                        setCommentPicture(notice);
                    }
                }
            }
        }
        outputObject.setBeans(bean);
        outputObject.settotal(page.getTotal());
    }

    private void setCommentPicture(Notice notice) {
        QueryWrapper<Picture> queryPicture = new QueryWrapper<>();
        queryPicture.eq(MybatisPlusUtil.toColumns(Picture::getObjectId), notice.getObjectId());
        Picture one = pictureService.getOne(queryPicture);
        if (ObjectUtil.isNotEmpty(one)) {
            notice.setPicture(one);
        }
    }

    @Override
    public void updateStateById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get(CommonConstants.ID).toString();
        UpdateWrapper<Notice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Notice::getState), ReadEnum.READ.getKey());
        update(updateWrapper);
    }

    @Override
    public void queryUnReadNum(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getParams().get(CommonConstants.ID).toString();
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getReceiveId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getState), ReadEnum.UNREAD.getKey());
        List<Notice> bean = list(queryWrapper);
        // 按分类分组
        Map<Integer, List<Notice>> map = bean.stream().collect(Collectors.groupingBy(Notice::getNoticeType));
        // 计算各组数量
        Map<Integer, Long> countMap = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));
        outputObject.setBean(countMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

}
