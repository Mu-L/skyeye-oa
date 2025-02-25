/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.xxljob;

import com.skyeye.common.util.DateAfterSpacePointTime;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.constans.ForumConstants;
import com.skyeye.eve.forum.dao.ForumContentDao;
import com.skyeye.eve.forum.service.ForumHotService;
import com.skyeye.jedis.JedisClientService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: HotForumQuartz
 * @Description: 每天凌晨两点去计算每日热门贴
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 23:09
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class HotForumQuartz {

    private static Logger log = LoggerFactory.getLogger(HotForumQuartz.class);

    @Autowired
    private JedisClientService jedisClient;

    @Autowired
    private ForumContentDao forumContentDao;

    @Autowired
    private ForumHotService forumHotService;

    /**
     * 定时器计算每日热门贴
     */
    @XxlJob("hotForumQuartz")
    public void editHotForumMation() {
        forumHotService.editHotForumMation();
    }
    /*public void editHotForumMation() {
        try {
            String yestoday = DateAfterSpacePointTime.getSpecifiedTime(
                DateAfterSpacePointTime.ONE_DAY.getType(), DateUtil.getTimeAndToString(), DateUtil.YYYY_MM_DD, DateAfterSpacePointTime.AroundType.BEFORE);
            String everydayBrowseKey = ForumConstants.forumEverydayBrowseIdsByTime(yestoday);
            if (!ToolUtil.isBlank(jedisClient.get(everydayBrowseKey))) {
                // 获取昨天被浏览过的帖子
                String str = jedisClient.get(everydayBrowseKey);
                String[] forumarr = str.split(",");
                List<Map<String, Object>> beans = new LinkedList<>();
                List<Map<String, Object>> list = new LinkedList<>();
                for (int i = 0, len = forumarr.length; i < len; i++) {
                    // 计算一天的浏览量
                    String browseNumsKey = ForumConstants.forumBrowseNumsByForumId(forumarr[i]);
                    String ybrowseNumsKey = ForumConstants.forumYesterdayBrowseNumsByForumId(forumarr[i]);
                    String bnownum = "0";
                    if (!ToolUtil.isBlank(jedisClient.get(browseNumsKey))) {
                        // 帖子当前的浏览量
                        bnownum = jedisClient.get(browseNumsKey);
                    }
                    String byestodaynum = "0";
                    if (!ToolUtil.isBlank(jedisClient.get(ybrowseNumsKey))) {
                        // 帖子昨天的浏览量
                        byestodaynum = jedisClient.get(ybrowseNumsKey);
                    }
                    // 帖子一天的浏览量
                    String bnum = String.valueOf(Integer.parseInt(bnownum) - Integer.parseInt(byestodaynum));

                    //计算一天的评论量
                    String commentNumsKey = ForumConstants.forumCommentNumsByForumId(forumarr[i]);
                    String ycommentNumsKey = ForumConstants.forumYesterdayCommentNumsByForumId(forumarr[i]);
                    String cnownum = "0";
                    if (!ToolUtil.isBlank(jedisClient.get(commentNumsKey))) {
                        // 帖子当前的评论量
                        cnownum = jedisClient.get(commentNumsKey);
                    }
                    String cyestodaynum = "0";
                    if (!ToolUtil.isBlank(jedisClient.get(ycommentNumsKey))) {
                        // 帖子昨天的评论量
                        cyestodaynum = jedisClient.get(ycommentNumsKey);
                    }
                    // 帖子一天的评论量
                    String cnum = String.valueOf(Integer.parseInt(cnownum) - Integer.parseInt(cyestodaynum));

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", ToolUtil.getSurFaceId());
                    map.put("forumId", forumarr[i]);
                    map.put("bnum", bnum);
                    map.put("cnum", cnum);
                    map.put("time", yestoday);
                    list.add(map);
                    // 更新浏览量
                    jedisClient.set(ybrowseNumsKey, bnownum);
                    // 更新评论量
                    jedisClient.set(ycommentNumsKey, cnownum);

                    String everyforumEverydayNums = ForumConstants.everyforumEverydayNumsByIdAndTime(forumarr[i], yestoday);
                    // 将每个帖子每天的浏览量+评论量存入redis中
                    jedisClient.set(everyforumEverydayNums, String.valueOf(Integer.parseInt(bnum) + Integer.parseInt(cnum)));

                    if (list.size() >= 20) {
                        // 每20条数据保存一次，将每天被浏览过的帖子存入统计表中
                        forumContentDao.insertForumStatisticsDayByList(list);
                        if (!beans.isEmpty()) {
                            list.addAll(beans);
                            beans.clear();
                        }
                        // 根据近七天的浏览量和评论量的算术平均值对list进行排序
                        list = sortListByNums(list, yestoday);
                        // 取前六条放入beans中
                        beans.addAll(list.subList(0, 6));
                        list.clear();
                    }
                }

                if (!list.isEmpty()) {
                    // 将每天被浏览过的帖子存入统计表中
                    forumContentDao.insertForumStatisticsDayByList(list);
                    if (!beans.isEmpty()) {
                        list.addAll(beans);
                        beans.clear();
                    }
                    // 根据近七天的浏览量和评论量的算术平均值对list进行排序
                    list = sortListByNums(list, yestoday);
                    int count = list.size();
                    int pageMaxSize = 6;
                    if (count < pageMaxSize) {
                        pageMaxSize = count;
                    }
                    // 取前六条放入beans中
                    beans.addAll(list.subList(0, pageMaxSize));
                }

                if (!beans.isEmpty()) {
                    forumContentDao.insertForumHotByList(beans);
                }
            }
            // 清空每天被浏览过的帖子
            jedisClient.del(everydayBrowseKey);
        } catch (Exception e) {
            log.warn("editHotForumMation error.", e);
        }
    }*/

    /**
     * 获取过去7天内的日期数组
     *
     * @return 日期数组
     * @throws ParseException
     */
    public static ArrayList<String> pastDay(String time) throws ParseException {
        ArrayList<String> pastDaysList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.YYYY_MM_DD);
        Date date = sdf.parse(time);
        for (int i = 6; i >= 0; i--) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - i);
            Date today = calendar.getTime();
            String result = sdf.format(today);
            pastDaysList.add(result);
        }
        return pastDaysList;
    }

    /**
     * 根据近七天的浏览量和评论量的算术平均值对list进行排序
     *
     * @return 日期数组
     * @throws ParseException
     */
    public List<Map<String, Object>> sortListByNums(List<Map<String, Object>> list, String time) throws ParseException {
        // 格式化小数
        DecimalFormat df = new DecimalFormat("0.00");
        for (Map<String, Object> m : list) {
            // 获取近七天的日期数组集合
            ArrayList<String> datelist = pastDay(time);
            int nums = 0;
            for (String date : datelist) {
                String everyforumEverydayNum = ForumConstants.everyforumEverydayNumsByIdAndTime(m.get("forumId").toString(), date);
                if (!ToolUtil.isBlank(jedisClient.get(everyforumEverydayNum))) {
                    // 获取每个帖子每天的浏览量+评论量
                    nums += Integer.parseInt(jedisClient.get(everyforumEverydayNum));
                }
            }
            // 将帖子近七天的评论量、浏览量的算术平均值保留两位小数放入map中
            m.put("nums", String.valueOf(df.format((float) nums / 7)));
        }
        // 按帖子近七天的评论量、浏览量的算术平均值给集合排序
        list.sort(new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                Integer m1num = (int) Float.parseFloat(m1.get("nums").toString()) * 100;
                Integer m2num = (int) Float.parseFloat(m2.get("nums").toString()) * 100;
                int flag = m1num.compareTo(m2num);
                return -flag; // 取反，按倒序排列
            }
        });
        return list;
    }

}
