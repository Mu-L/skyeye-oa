package com.skyeye.video.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.user.service.UserService;
import com.skyeye.video.dao.VideoDao;
import com.skyeye.video.entity.Video;
import com.skyeye.video.entity.VideoRecord;
import com.skyeye.video.entity.VideoView;
import com.skyeye.video.service.VideoRecordService;
import com.skyeye.video.service.VideoService;
import com.skyeye.video.service.VideoViewService;
import com.skyeye.videocomment.entity.VideoComment;
import com.skyeye.videocomment.service.VideoCommentService;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @ClassName: VideoRecordServiceImpl
 * @Description: 视频管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "视频管理", groupName = "视频管理")
public class VideoServiceImpl extends SkyeyeBusinessServiceImpl<VideoDao, Video> implements VideoService {

    @Autowired
    private VideoRecordService videoRecordService;

    @Autowired
    private UserService userService;

    @Autowired
    private VideoViewService videoViewService;

    @Autowired
    private VideoCommentService videoCommentService;

    @Override
    public Video selectById(String id) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Video video = super.selectById(id);
        setCheckUpvote(video, userId);
        setCheckCollection(video, userId);
        userService.setDataMation(video, Video::getCreateId);
        return video;
    }

    // 检验当前登录人是否对视频点赞
    private void setCheckUpvote(Video video, String userId) {
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getVideoId), video.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag), CommonNumConstants.NUM_ONE);
        video.setCheckUpvote(videoRecordService.count(queryWrapper) > CommonNumConstants.NUM_ZERO);
    }

    // 检验当前登录人是否对视频收藏
    private void setCheckCollection(Video video, String userId) {
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getVideoId), video.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag), CommonNumConstants.NUM_TWO);
        video.setCheckCollection(videoRecordService.count(queryWrapper) > CommonNumConstants.NUM_ZERO);
    }

    private void checkUpvoteAndCollection(List<Video> videoList, String userId) {
        for (Video video : videoList) {
            setCheckUpvote(video, userId);
            setCheckCollection(video, userId);
        }
    }

    @Transactional
    @Override
    public void createPostpose(Video entity, String userId) {
        // 获取视频时长
        String videoUrl = entity.getVideoSrc();
        String ffmpeg_path = "172.18.92.41:7000/dev/fileBase/";
        List<String> commands = new ArrayList<>();
        commands.add(ffmpeg_path);
        commands.add("-i");
        commands.add(videoUrl);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            final Process p = builder.start();

            //从输入流中读取视频信息
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            //从视频信息中解析时长
            String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
            Pattern pattern = Pattern.compile(regexDuration);
            Matcher m = pattern.matcher(sb.toString());
            if (m.find()) {
                int time = getTimelen(m.group(1));
                entity.setVideoDuration(time);
                updateEntity(entity,userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static int getTimelen(String timelen) {
        int min = 0;
        String strs[] = timelen.split(":");
        if (strs[0].compareTo("0") > 0) {
            min += Integer.valueOf(strs[0]) * 60 * 60;//秒
        }
        if (strs[1].compareTo("0") > 0) {
            min += Integer.valueOf(strs[1]) * 60;
        }
        if (strs[2].compareTo("0") > 0) {
            min += Math.round(Float.valueOf(strs[2]));
        }
        return min;
    }

        @Override
    public void queryAllVideoList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String objectId = commonPageInfo.getObjectId();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(objectId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Video::getCreateId), objectId);
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Video::getCreateTime));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Video::getVisitNum));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Video::getTasnNum));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Video::getCollectionNum));
        List<Video> bean = list(queryWrapper);
        checkUpvoteAndCollection(bean, userId);
        userService.setDataMation(bean, Video::getCreateId);
        outputObject.setBeans(bean);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryRecommendVideoList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        // 定义行为的评分权重
        double VIEW_SCORE = 0.1; // 浏览
        double LIKE_SCORE = 3.0; // 点赞
        double COLLECT_SCORE = 5.0; // 收藏
        double COMMENT_SCORE = 2.0; // 评论
        String currentUserId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        Map<String, Map<String, Double>> userVideoScores = new HashMap<>();
        // 获取所有用户的点赞的视频
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag), CommonNumConstants.NUM_ONE);
        List<VideoRecord> supportVideos = videoRecordService.list(queryWrapper);
        setUserVideoScores(supportVideos,userVideoScores,LIKE_SCORE);
        // 获取所有用户的收藏的视频
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag), CommonNumConstants.NUM_TWO);
        List<VideoRecord> collectVideos = videoRecordService.list(queryWrapper);
        setUserVideoScores(collectVideos,userVideoScores,COLLECT_SCORE);
        // 获取所有用户的评论
        QueryWrapper<VideoComment> queryComment = new QueryWrapper<>();
        List<VideoComment> commentVideos = videoCommentService.list(queryComment);
        if(CollectionUtil.isNotEmpty(commentVideos)){
            for (VideoComment videoComment : commentVideos) {
                String userId = videoComment.getCreateId();
                String videoId = videoComment.getVideoId();
                Map<String, Double> userVideoScore = userVideoScores.getOrDefault(userId, new HashMap<>());
                double score = userVideoScore.getOrDefault(videoId, 0.0) + COMMENT_SCORE;
                userVideoScore.put(videoId, score);
                userVideoScores.put(userId, userVideoScore);
            }
        }
        // 获取所有用户的浏览记录
        QueryWrapper<VideoView> queryView = new QueryWrapper<>();
        List<VideoView> viewList = videoViewService.list(queryView);
        setUserVideoScores(viewList,userVideoScores,VIEW_SCORE);
        if(CollectionUtil.isEmpty(userVideoScores)){
            throw new CustomException("没有用户的行为记录");
        }
        // 2，计算视频之间的相似度
        Map<String, Map<String, Double>> similarityMap= buildSimilarityMatrix(userVideoScores);
        List<String> videoIds = recommendVideos(currentUserId, userVideoScores, similarityMap, 10);
        List<Video> videos = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(videoIds)){
            for (String videoId : videoIds) {
                Video video = selectById(videoId);
                videos.add(video);
            }
            outputObject.setBeans(videos);
            outputObject.settotal(videos.size());
        }else {
            Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
            QueryWrapper<Video> queryVideo = new QueryWrapper<>();
            queryVideo.orderByDesc(MybatisPlusUtil.toColumns(Video::getCreateTime))
                    .orderByDesc(MybatisPlusUtil.toColumns(Video::getCollectionNum))
                    .orderByDesc(MybatisPlusUtil.toColumns(Video::getTasnNum))
                    .orderByDesc(MybatisPlusUtil.toColumns(Video::getVisitNum));
            videos = list(queryVideo);
            outputObject.setBeans(videos);
            outputObject.settotal(page.getTotal());
        }

    }
    private<T> void setUserVideoScores(List<T> list, Map<String, Map<String, Double>> userVideoScores, double weight){
        if(CollectionUtil.isNotEmpty(list)){
            for (T t : list) {
                String userId = BeanUtil.getProperty(t, "userId");
                String videoId = BeanUtil.getProperty(t, "videoId");
                // 如果用户已经存在，则获取其评分Map，否则创建一个新的评分Map
                Map<String, Double> userVideoScore = userVideoScores.getOrDefault(userId, new HashMap<>());
                // 如果视频已经存在，则累加评分，否则初始化评分
                double score = userVideoScore.getOrDefault(videoId, 0.0) + weight;
                // 更新用户对视频的评分
                userVideoScore.put(videoId, score);
                // 将更新后的评分Map放回用户评分中
                userVideoScores.put(userId, userVideoScore);
            }
        }
    }

    // 计算两个视频之间的余弦相似度
    private static double cosineSimilarity(Map<String, Double> scores1, Map<String, Double> scores2) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        Set<String> commonUsers = new HashSet<>(scores1.keySet());
        commonUsers.retainAll(scores2.keySet());
        for (String user : commonUsers) {
            dotProduct += scores1.get(user) * scores2.get(user);
        }
        for (double score : scores1.values()) {
            normA += Math.pow(score, 2);
        }
        for (double score : scores2.values()) {
            normB += Math.pow(score, 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    //构建视频之间的相似度矩阵
    private Map<String, Map<String, Double>> buildSimilarityMatrix(Map<String, Map<String, Double>> userVideoScores) {
        Map<String, Map<String, Double>> similarityMatrix = new HashMap<>();

        // 获取所有视频ID
        Set<String> allVideoIds = new HashSet<>();
        for (Map<String, Double> userScores : userVideoScores.values()) {
            allVideoIds.addAll(userScores.keySet());
        }

        List<String> videoIds = new ArrayList<>(allVideoIds);

        // 计算视频之间的相似度
        for (int i = 0; i < videoIds.size(); i++) {
            for (int j = i + 1; j < videoIds.size(); j++) {
                String videoId1 = videoIds.get(i);
                String videoId2 = videoIds.get(j);

                Map<String, Double> scores1 = userVideoScores.values().stream()
                        .filter(scores -> scores.containsKey(videoId1))
                        .collect(Collectors.toMap(
                                map -> map.keySet().iterator().next(), // 键
                                map -> map.values().iterator().next(), // 值
                                (existingValue, newValue) -> existingValue // 合并函数：保留第一个值
                        ));
                Map<String, Double> scores2 = userVideoScores.values().stream()
                        .filter(scores -> scores.containsKey(videoId2))
                        .collect(Collectors.toMap(
                                map -> map.keySet().iterator().next(), // 键
                                map -> map.values().iterator().next(), // 值
                                (existingValue, newValue) -> existingValue // 合并函数：保留第一个值
                        ));

                double similarity = cosineSimilarity(scores1, scores2);

                similarityMatrix.computeIfAbsent(videoId1, k -> new HashMap<>()).put(videoId2, similarity);
                similarityMatrix.computeIfAbsent(videoId2, k -> new HashMap<>()).put(videoId1, similarity);
            }
        }
        return similarityMatrix;
    }

    // 为用户生成推荐列表
    private  List<String> recommendVideos(String userId,
                                          Map<String, Map<String, Double>> userVideoScores,
                                          Map<String, Map<String, Double>> similarityMatrix,
                                          int topN){
        // 获取用户对视频的评分
        Map<String, Double> userScores = userVideoScores.get(userId);
        if (CollectionUtil.isEmpty(userScores)) {
            return Collections.emptyList(); // 如果用户没有评分记录，返回空列表
        }

        // 找到用户评分最高的视频
        String mostLikedVideo = Collections.max(userScores.entrySet(), Map.Entry.comparingByValue()).getKey();

        // 获取与该视频相似度最高的其他视频
        Map<String, Double> similarities = similarityMatrix.get(mostLikedVideo);
        if (CollectionUtil.isEmpty(similarities)) {
            return Collections.emptyList(); // 如果没有相似视频，返回空列表
        }

        // 按相似度排序并推荐
        return similarities.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 点赞或取消点赞
     */
    @Override
    @Transactional
    public void supportOrNotVideo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String videoId = map.get("videoId").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Video video = selectById(videoId);
        int supportNum = Integer.parseInt(video.getTasnNum());
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getVideoId), videoId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), userId);
        // 过滤到单一的点赞视频
        List<VideoRecord> videoRecordList = videoRecordService.list(queryWrapper).stream()
                .filter(item -> item.getCtFlag() == CommonNumConstants.NUM_ONE)
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(videoRecordList)) {
            //  不为空证明已经点赞了——进行取消点赞——删除记录表 点赞数减1
            supportNum--;
            video.setTasnNum(String.valueOf(supportNum));
            updateById(video);
            videoRecordService.removeById(videoRecordList.get(0).getId());
        } else {
            // 点赞数加1
            supportNum++;
            video.setTasnNum(String.valueOf(supportNum));
            updateById(video);
            // 为空则进行点赞记录
            VideoRecord videoRecord = new VideoRecord();
            videoRecord.setVideoId(videoId);
            videoRecord.setUserId(userId);
            videoRecord.setCtFlag(CommonNumConstants.NUM_ONE);
            videoRecord.setCreateTime(LocalDateTime.now().toString());
            videoRecordService.createEntity(videoRecord, userId);
        }
    }

    @Override
    @Transactional
    public void collectOrNotVideo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String videoId = map.get("videoId").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Video video = selectById(videoId);
        int collectNum = Integer.parseInt(video.getCollectionNum());
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getVideoId), videoId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), userId);
        List<VideoRecord> videoRecordList = videoRecordService.list(queryWrapper).stream()
                .filter(item -> item.getCtFlag() == CommonNumConstants.NUM_TWO)
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(videoRecordList)) {
            // 不为空——取消收藏——删除记录,收藏数-1
            collectNum--;
            video.setCollectionNum(String.valueOf(collectNum));
            updateById(video);
            videoRecordService.removeById(videoRecordList.get(0).getId());
        } else {
            // 收藏数加1
            collectNum++;
            video.setCollectionNum(String.valueOf(collectNum));
            updateById(video);
            // 为空则_进行收藏记录
            VideoRecord videoRecord = new VideoRecord();
            videoRecord.setVideoId(videoId);
            videoRecord.setUserId(userId);
            videoRecord.setCtFlag(CommonNumConstants.NUM_TWO);
            videoRecord.setCreateTime(LocalDateTime.now().toString());
            videoRecordService.createEntity(videoRecord, userId);
        }
    }

    @Override
    public void queryAllSupportVideo(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String objectId = commonPageInfo.getObjectId();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(objectId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), objectId);
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(VideoRecord::getCreateTime));
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag));
        List<VideoRecord> videoRecordList = videoRecordService.list(queryWrapper).stream()
                .filter(item -> item.getCtFlag() == CommonNumConstants.NUM_ONE)
                .collect(Collectors.toList());
        List<Video> supportList = new ArrayList<>();
        for (VideoRecord videoRecord : videoRecordList) {
            Video video = selectById(videoRecord.getVideoId());
            setCheckCollection(video, userId);
            video.setCheckUpvote(true);
            supportList.add(video);
        }
        userService.setDataMation(supportList, Video::getCreateId);
        outputObject.setBeans(supportList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryAllCollectVideo(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String objectId = commonPageInfo.getObjectId();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(objectId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), objectId);
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(VideoRecord::getCreateTime));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag));
        List<VideoRecord> videoRecordList = videoRecordService.list(queryWrapper).stream()
                .filter(item -> item.getCtFlag() == CommonNumConstants.NUM_TWO)
                .collect(Collectors.toList());
        List<Video> collectList = new ArrayList<>();
        for (VideoRecord videoRecord : videoRecordList) {
            Video video = selectById(videoRecord.getVideoId());
            video.setCheckCollection(true);
            setCheckUpvote(video, userId);
            collectList.add(video);
        }
        userService.setDataMation(collectList, Video::getCreateId);
        outputObject.setBeans(collectList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void refreshVisitVideo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String videoId = map.get("videoId").toString();
        Video video = selectById(videoId);
        int visitNum = Integer.parseInt(video.getVisitNum());
        visitNum++;
        video.setVisitNum(String.valueOf(visitNum));
        updateById(video);
    }

    @Override
    protected void deletePreExecution(Video entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (!userId.equals(entity.getCreateId())) {
            throw new CustomException("无权限");
        }
    }
}
