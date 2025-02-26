package com.skyeye.eve.forum.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateAfterSpacePointTime;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.forum.dao.ForumHotDao;
import com.skyeye.eve.forum.entity.ForumContent;
import com.skyeye.eve.forum.entity.ForumHot;
import com.skyeye.eve.forum.service.ForumContentService;
import com.skyeye.eve.forum.service.ForumHotService;
import com.skyeye.eve.service.IAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ForumHotServiceImpl
 * @Description: 论坛热门帖子管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:52
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "论坛热门帖子管理", groupName = "论坛热门帖子管理")
public class ForumHotServiceImpl extends SkyeyeBusinessServiceImpl<ForumHotDao, ForumHot> implements ForumHotService {

    @Autowired
    private ForumContentService forumContentService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public void queryHotForumList(InputObject inputObject, OutputObject outputObject) {
        // 获取今天热门的帖子
        QueryWrapper<ForumHot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumHot::getCreateTime), DateUtil.getYmdTimeAndToString());
        List<ForumHot> list = list(queryWrapper);
        // 拿到对应的帖子id
        List<String> ids = list.stream().map(ForumHot::getForumId).collect(Collectors.toList());
        // 查询帖子信息
        QueryWrapper<ForumContent> queryContent = new QueryWrapper<>();
        queryContent.in(CommonConstants.ID, ids);
        List<ForumContent> forumContents = forumContentService.list(queryContent);

        iAuthUserService.setDataMation(forumContents, ForumContent::getCreateId);
        List<ForumContent> beans = forumContents.stream().map(item -> {
            if (item.getAnonymous() == CommonNumConstants.NUM_TWO) {
                // 匿名
                Map<String, Object> createMation = item.getCreateMation();
                createMation.put("name", "匿名用户");
                createMation.put("picture", "/images/upload/wallPost/1726212288676.jpg");
            }
            return item;
        }).collect(Collectors.toList());
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public void  editHotForumMation(){
        // 取出前三十天的帖子
        String beforeDay = getBeforeOrFutureDay(-29);
        String today = DateUtil.getTimeAndToString();
        QueryWrapper<ForumContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumContent::getState), CommonNumConstants.NUM_ONE)
                .gt(MybatisPlusUtil.toColumns(ForumContent::getBrowseNum), CommonNumConstants.NUM_ONE)
                .between(MybatisPlusUtil.toColumns(ForumContent::getCreateTime), beforeDay, today)
                .orderByDesc(MybatisPlusUtil.toColumns(ForumContent::getCreateTime));
        List<ForumContent> forumContents = forumContentService.list(queryWrapper);

        // 取出 浏览量、评论量
        Map<String,List<Integer>> listMap = new HashMap<>();
        for (ForumContent forumContent : forumContents){
            List<Integer> nums = new ArrayList<>();
            nums.add(Integer.valueOf(forumContent.getBrowseNum()));
            nums.add(Integer.valueOf(forumContent.getCommentNum()));
            listMap.put(forumContent.getId(),nums);
        }
        // 计算权重
        Map<String,Double> weightMap = new HashMap<>();
        listMap.forEach((k,v)->{
            weightMap.put(k, v.get(CommonNumConstants.NUM_ZERO)*0.7 + v.get(CommonNumConstants.NUM_ONE)*0.3);
        });
        // 排序权重
        List<String> ids = new ArrayList<>();
        Set<Map.Entry<String, Double>> entries = weightMap.entrySet();
        CollectionUtil.sort(entries, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        for (Map.Entry<String, Double> entry : entries) {
            ids.add(entry.getKey());
        }
        // 取出前10 的帖子
        if(ids.size() > 10){
            ids = ids.subList(0,10);
        }
        List<ForumHot> beans = new ArrayList<>();
        for(int i = 0;i<ids.size();i++){
            ForumHot forumHot = new ForumHot();
            forumHot.setForumId(ids.get(i));
            forumHot.setUpdateTime(today);
            beans.add(forumHot);
        }
        createEntity(beans,null);
    }

    public String getBeforeOrFutureDay(int num) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, num);
        Date m = c.getTime();
        return format.format(m);
    }
}
