/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.circle.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.circle.dao.CircleDao;
import com.skyeye.circle.entity.Circle;
import com.skyeye.circle.service.CircleService;
import com.skyeye.circleview.service.CircleViewService;
import com.skyeye.common.WallConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.joincircle.entity.JoinCircle;
import com.skyeye.joincircle.service.JoinCircleService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.post.service.PostService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CircleServiceImpl
 * @Description: 圈子服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "圈子管理", groupName = "圈子管理")
public class CircleServiceImpl extends SkyeyeBusinessServiceImpl<CircleDao, Circle> implements CircleService {

    @Autowired
    private PostService postService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private CircleViewService circleViewService;

    @Autowired
    private JoinCircleService joinCircleService;

    @Override
    public void validatorEntity(Circle entity) {
        super.validatorEntity(entity);
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if(StrUtil.isEmpty(userToken)){
            throw new CustomException("请先完成登录！");
        }
    }

    @Override
    public void createPrepose(Circle circle) {
        String userIdentity = PutObject.getRequest().getHeader(WallConstants.USER_IDENTITY_KEY);
        circle.setLoginIdentity(userIdentity);
        circle.setViewNum(CommonNumConstants.NUM_ZERO);
        circle.setNum(CommonNumConstants.NUM_ZERO);
    }

    @Override
    public void createPostpose(Circle entity, String userId) {
        JoinCircle joinCircle = new JoinCircle();
        joinCircle.setCircleId(entity.getId());
        joinCircleService.createEntity(joinCircle, userId);
    }

    @Override
    public void deletePreExecution(Circle circle) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (!userId.equals(circle.getCreateId())) {
            throw new CustomException("无权限");
        }
    }

    @Override
    public void deletePostpose(String id) {
        postService.deleteByCircleId(id);
        materialService.deleteByCircleId(id);
        circleViewService.deleteCircleViewByCircleId(id);
        joinCircleService.deleteJoinByCircleId(id);
    }

    @Override
    public List<Circle> setUserIsJoin(List<Circle> circles) {
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if(StrUtil.isEmpty(userToken)){
            return circles.stream().map(circle -> {
                circle.setIsJoin(false);
                return circle;
            }).collect(Collectors.toList());
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        List<String> circleIds = circles.stream().map(Circle::getId).collect(Collectors.toList());
        Map<String,Boolean> checkMap =  joinCircleService.checkIsJoinCircle(circleIds, userId);
        List<Circle> bean = circles.stream().map((item) -> {
            item.setIsJoin(checkMap.get(item.getId()));
            return item;
        }).collect(Collectors.toList());
        return bean;
    }

    @Override
    public Circle selectById(String id) {
        Circle circle = super.selectById(id);
        if(StrUtil.isEmpty(circle.getId())){
            throw new CustomException("圈子已被删除");
        }
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if(StrUtil.isEmpty(userToken)){
            circle.setIsJoin(false);
        }else {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            circle.setIsJoin(joinCircleService.checkIsJoinCircle(id, userId));
        }
        return circle;
    }

    @Override
    public void updateViewNum(String circleId, Integer count) {
        Circle circle = selectById(circleId);
        UpdateWrapper<Circle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, circleId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Circle::getViewNum), count+circle.getViewNum());
        update(updateWrapper);
    }

    @Override
    public void updateJoinNum(String circleId, Integer joinNum) {
        UpdateWrapper<Circle> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, circleId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Circle::getNum), joinNum);
        update(updateWrapper);
    }

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        return queryCircleList(inputObject);
    }

    private List<Map<String, Object>> queryCircleList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        QueryWrapper<Circle> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            queryWrapper.like(MybatisPlusUtil.toColumns(Circle::getTitle), commonPageInfo.getKeyword());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Circle::getCreateTime));
        List<Circle> bean = new ArrayList<>();
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            String objectId = commonPageInfo.getObjectId();
            queryWrapper.eq(MybatisPlusUtil.toColumns(Circle::getCreateId), objectId);
            bean = list(queryWrapper);
            // 获取我加入的圈子记录
            List<JoinCircle> joinCircles = joinCircleService.queryMyJoinCircle(objectId);
            List<String> circleIds = joinCircles.stream().map(JoinCircle::getCircleId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(circleIds)) {
                List<Circle> circles = selectByIds(circleIds.toArray(new String[0]));
                bean.addAll(circles);
            }
        }
        if (CollectionUtil.isEmpty(bean)) {
            bean = list(queryWrapper);
        }
        // 设置是否加入
        List<Circle> circleList = setUserIsJoin(bean);
        return JSONUtil.toList(JSONUtil.toJsonStr(circleList), null);
    }

    @Override
    public void queryRelateCircles(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String circleId = params.get(CommonConstants.ID).toString();
        Circle circle = selectById(circleId);
        if (ObjectUtil.isEmpty(circle)) {
            throw new CustomException("圈子id有误");
        }
        QueryWrapper<Circle> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(CommonConstants.ID, circleId);
        List<Circle> bean = list(queryWrapper);
        if (CollectionUtil.isEmpty(bean)) {
            return;
        }
        Map<String, Double> map = new HashMap<>();
        for (Circle item : bean) {
            String topicName = item.getTitle();
            String description = item.getDescribe();
            BigInteger oldTopicItem = simHash(circle.getTitle(), 64);
            BigInteger newTopicItem = simHash(topicName, 64);
            BigInteger oldDesItem = simHash(circle.getDescribe(), 64);
            BigInteger newDesItem = simHash(description, 64);
            Double topicSim = getSimilar(oldTopicItem, newTopicItem);
            Double descriptionSim = getSimilar(oldDesItem, newDesItem);
            Double sum = topicSim * 0.4 + descriptionSim * 0.6;
            map.put(item.getId(), sum);
        }
        // 按照值排序
        List<Map.Entry<String, Double>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        if (list.size() >= 10) {
            list = list.subList(0, 10);
        }
        List<Circle> circleList = selectByIds(list.stream().map(Map.Entry::getKey).toArray(String[]::new));
        // 设置是否加入圈子
        List<Circle> beans = setUserIsJoin(circleList);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public void deleteCircle(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String circleId = params.get("id").toString();
        QueryWrapper<Circle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, circleId);
        remove(queryWrapper);
        deletePostpose(circleId);
    }

    // 过滤字符
    private String clearSpecialCharacters(String topicName) {
        // 将内容转换为小写
        topicName = StringUtils.lowerCase(topicName);
        // 过滤HTML标签
        topicName = Jsoup.clean(topicName, Safelist.none());
        // 过滤特殊字符
        String[] strings = {" ", "\n", "\r", "\t", "\\r", "\\n", "\\t", "&nbsp;", "&amp;", "&lt;", "&gt;", "&quot;", "&qpos;"};
        for (String string : strings) {
            topicName = topicName.replaceAll(string, "");
        }
        return topicName;
    }

    /**
     * Description:[计算单个分词的hash值]
     */
    private BigInteger getWordHash(String word) {
        if (StringUtils.isEmpty(word)) {
            // 如果分词为null，则默认hash为0
            return new BigInteger("0");
        } else {
            // 分词补位，如果过短会导致Hash算法失败
            while (word.length() < 3) {
                word = word + word.charAt(0);
            }
            // 分词位运算
            char[] wordArray = word.toCharArray();
            BigInteger x = BigInteger.valueOf(wordArray[0] << 7);
            BigInteger m = new BigInteger("1000003");
            // 初始桶pow运算
            BigInteger mask = new BigInteger("2").pow(64).subtract(new BigInteger("1"));
            for (char item : wordArray) {
                BigInteger temp = BigInteger.valueOf(item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(word.length())));
            //ILLEGAL_X
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    /**
     * Description:[分词计算向量]
     */
    private BigInteger simHash(String topicName, Integer hashCode) {
        // 清除特殊字符
        topicName = this.clearSpecialCharacters(topicName);
        int[] hashArray = new int[hashCode];
        // 对内容进行分词处理
        List<Term> terms = StandardTokenizer.segment(topicName);
        // 配置词性权重
        Map<String, Integer> weightMap = new HashMap<>(16, 0.75F);
        weightMap.put("n", 1);
        // 设置停用词
        Map<String, String> stopMap = new HashMap<>(16, 0.75F);
        stopMap.put("w", "");
        // 设置超频词上线
        Integer overCount = 5;
        // 设置分词统计量
        Map<String, Integer> wordMap = new HashMap<>(16, 0.75F);
        for (Term term : terms) {
            // 获取分词字符串
            String word = term.word;
            // 获取分词词性
            String nature = term.nature.toString();
            // 过滤超频词
            if (wordMap.containsKey(word)) {
                Integer count = wordMap.get(word);
                if (count > overCount) {
                    continue;
                } else {
                    wordMap.put(word, count + 1);
                }
            } else {
                wordMap.put(word, 1);
            }
            // 过滤停用词
            if (stopMap.containsKey(nature)) {
                continue;
            }
            // 计算单个分词的Hash值
            BigInteger wordHash = this.getWordHash(word);
            for (int i = 0; i < 64; i++) {
                // 向量位移
                BigInteger bitMask = new BigInteger("1").shiftLeft(i);
                // 对每个分词hash后的列进行判断，例如：1000...1，则数组的第一位和末尾一位加1,中间的62位减一，也就是，逢1加1，逢0减1，一直到把所有的分词hash列全部判断完
                // 设置初始权重
                Integer weight = 1;
                if (weightMap.containsKey(nature)) {
                    weight = weightMap.get(nature);
                }
                // 计算所有分词的向量
                if (wordHash.and(bitMask).signum() != 0) {
                    hashArray[i] += weight;
                } else {
                    hashArray[i] -= weight;
                }
            }
        }
        // 生成指纹
        BigInteger fingerPrint = new BigInteger("0");
        for (int i = 0; i < 64; i++) {
            if (hashArray[i] >= 0) {
                fingerPrint = fingerPrint.add(new BigInteger("1").shiftLeft(i));
            }
        }
        return fingerPrint;
    }

    /**
     * Description:[获取标题内容的海明距离]
     */
    private int getHammingDistance(BigInteger oldItem, BigInteger newItem) {
        // 求差集
        BigInteger subtract = new BigInteger("1").shiftLeft(64).subtract(new BigInteger("1"));
        // 求异或
        BigInteger xor = newItem.xor(oldItem).and(subtract);
        int total = 0;
        while (xor.signum() != 0) {
            total += 1;
            xor = xor.and(xor.subtract(new BigInteger("1")));
        }
        return total;
    }

    /**
     * Description:[获取标题内容的相似度]
     */
    private Double getSimilar(BigInteger oldItem, BigInteger newItem) {
        // 获取海明距离
        Double hammingDistance = (double) this.getHammingDistance(oldItem, newItem);
        // 求得海明距离百分比
        Double scale = (1 - hammingDistance / 64) * 100;
        Double formatScale = Double.parseDouble(String.format("%.2f", scale));
        return formatScale;
    }

    @Override
    public void queryUserCircleList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = commonPageInfo.getHolderId();
        String keyword = commonPageInfo.getKeyword();
        if(StrUtil.isEmpty(userId)){
            throw new CustomException("用户id不能为空");
        }
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Circle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Circle::getCreateId),userId)
                .orderByDesc(MybatisPlusUtil.toColumns(Circle::getCreateTime));
        if(StrUtil.isNotEmpty(keyword)){
            queryWrapper.like(MybatisPlusUtil.toColumns(Circle::getTitle),keyword);
        }
        List<Circle> circleList = list(queryWrapper);
        // 设置是否加入圈子
        setUserIsJoin(circleList);
        outputObject.setBeans(circleList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void updateCircleShareNum(String circleId, int num) {
        Circle circle = selectById(circleId);
        int shareNum = Integer.parseInt(circle.getShareNum()) + num;
        circle.setShareNum(String.valueOf(shareNum));
        updateById(circle);
    }
}
