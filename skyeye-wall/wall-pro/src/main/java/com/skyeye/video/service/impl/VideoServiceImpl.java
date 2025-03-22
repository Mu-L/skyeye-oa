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
import com.skyeye.focus.service.FocusService;
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
import com.skyeye.videotag.service.VideoTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: VideoRecordServiceImpl
 * @Description: 视频管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
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

    @Autowired
    private FocusService focusService;

    @Autowired
    private VideoTagService videoTagService;

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Override
    public Video selectById(String id) {
        Video video = super.selectById(id);
        focusService.checkFocus(video);
        video.setCheckUpvote(videoRecordService.checkUpvoteOrCollectByUserId(video, CommonNumConstants.NUM_ONE));
        video.setCheckCollection(videoRecordService.checkUpvoteOrCollectByUserId(video, CommonNumConstants.NUM_TWO));
        videoTagService.setTagMationForVideoList(video);
        try {
            userService.setDataMation(video, Video::getCreateId);
        }catch (Exception e){
            iAuthUserService.setDataMation(video, Video::getCreateId);
        }
        return video;
    }

    @Override
    public void createPrepose(Video entity) {
        String localVideoPath = tPath.replace("images", StrUtil.EMPTY) + entity.getVideoSrc();
        // 获取视频时长
        int duration = getVideoDuration(localVideoPath);
        log.info(String.format(Locale.ROOT, "video id is %s, duration is %s", entity.getId(), duration));
        if (duration > 0) {
            entity.setVideoDuration(duration);
        }
    }

    private int getVideoDuration(String videoPath) {
        String ffmpegPath = tPath + "/util/ffmpeg.exe"; // FFmpeg路径
        List<String> commands = new ArrayList<>();
        commands.add(ffmpegPath);
        commands.add("-i");
        commands.add(videoPath);

        try {
            ProcessBuilder builder = new ProcessBuilder(commands);
            Process process = builder.start();

            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                 BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = errorReader.readLine()) != null) {
                    log.info("Error stream: {}", line);
                    if (line.contains("Duration")) {
                        log.info("Duration found in error stream");
                        // 提取时长信息
                        String durationStr = line.substring(line.indexOf("Duration: ") + 10, line.indexOf(",", line.indexOf("Duration: ")));
                        String[] parts = durationStr.split(":");
                        int hours = Integer.parseInt(parts[0]);
                        int minutes = Integer.parseInt(parts[1]);
                        double seconds = Double.parseDouble(parts[2]);
                        int totalSeconds = (int) (hours * 3600 + minutes * 60 + seconds);
                        log.info("视频时长为: {} 秒", totalSeconds);
                        return totalSeconds;
                    }
                }

                while ((line = outputReader.readLine()) != null) {
                    log.info("Output stream: {}", line);
                    if (line.contains("Duration")) {
                        log.info("Duration found in output stream");
                        // 提取时长信息
                        String durationStr = line.substring(line.indexOf("Duration: ") + 10, line.indexOf(",", line.indexOf("Duration: ")));
                        String[] parts = durationStr.split(":");
                        int hours = Integer.parseInt(parts[0]);
                        int minutes = Integer.parseInt(parts[1]);
                        double seconds = Double.parseDouble(parts[2]);
                        int totalSeconds = (int) (hours * 3600 + minutes * 60 + seconds);
                        log.info("视频时长为: {} 秒", totalSeconds);
                        return totalSeconds;
                    }
                }
            } finally {
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    log.error("FFmpeg command failed with exit code: {}", exitCode);
                }
                process.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to get video duration from: {}", videoPath, e);
        }
        return -1; // 获取失败
    }

    // TODO 待优化
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
        List<VideoRecord> supportVideos = videoRecordService.queryAllSupportOrCollect(CommonNumConstants.NUM_ONE);
        setUserVideoScores(supportVideos, userVideoScores, LIKE_SCORE);
        // 获取所有用户的收藏的视频
        List<VideoRecord> collectVideos = videoRecordService.queryAllSupportOrCollect(CommonNumConstants.NUM_TWO);
        setUserVideoScores(collectVideos, userVideoScores, COLLECT_SCORE);
        // 获取所有用户的评论
        List<VideoComment> commentVideos = videoCommentService.queryAllData();
        if (CollectionUtil.isNotEmpty(commentVideos)) {
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
        List<VideoView> viewList = videoViewService.queryAllData();
        setUserVideoScores(viewList, userVideoScores, VIEW_SCORE);
        if (CollectionUtil.isEmpty(userVideoScores)) {
            throw new CustomException("没有用户的行为记录");
        }
        // 2，计算视频之间的相似度
        Map<String, Map<String, Double>> similarityMap = buildSimilarityMatrix(userVideoScores);
        List<String> videoIds = recommendVideos(currentUserId, userVideoScores, similarityMap, 10);
        List<Video> videos = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(videoIds)) {
            for (String videoId : videoIds) {
                Video video = selectById(videoId);
                videos.add(video);
            }
            outputObject.setBeans(videos);
            outputObject.settotal(videos.size());
        } else {
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

    /**
     * 获取全部视频
     * 获取我，他点赞的视频 userId type=1
     * 获取我，他收藏的视频 userId type=2
     */
    @Override
    public void queryAllCollectSupportVideo(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String type = commonPageInfo.getType();
        String objectId = commonPageInfo.getObjectId();
        String keyword = commonPageInfo.getKeyword();
        if (StrUtil.isNotEmpty(type)) {
            Map<String, List<String>> map = videoRecordService.queryAllCollectSupportVideoIds(inputObject);
            if (CollectionUtil.isEmpty(map)) {
                return;
            }
            String[] videoIds = map.get("videoIds").toArray(new String[0]);
            String total = map.get("total").get(CommonNumConstants.NUM_ZERO);
            List<Video> videos = selectByIds(videoIds);
            for (Video video : videos) {
                video.setCheckUpvote(videoRecordService.checkUpvoteOrCollectByUserId(video, CommonNumConstants.NUM_ONE));
                video.setCheckCollection(videoRecordService.checkUpvoteOrCollectByUserId(video, CommonNumConstants.NUM_TWO));
            }
            videoTagService.setTagMationForVideoList(videos.toArray(new Video[0]));
            try {
                userService.setDataMation(videos, Video::getCreateId);
            }catch (Exception e){
                iAuthUserService.setDataMation(videos, Video::getCreateId);
            }
            outputObject.setBean(videos);
            outputObject.settotal(Integer.parseInt(total));
        } else {
            Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
            QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
            if (StrUtil.isNotEmpty(objectId)) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(Video::getCreateId), objectId);
            }
            if (StrUtil.isNotEmpty(keyword)) {
                queryWrapper.like(MybatisPlusUtil.toColumns(Video::getTopic), keyword);
            }
            queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Video::getCreateTime))
                    .orderByDesc(MybatisPlusUtil.toColumns(Video::getCollectionNum))
                    .orderByDesc(MybatisPlusUtil.toColumns(Video::getRemarkNum))
                    .orderByDesc(MybatisPlusUtil.toColumns(Video::getTasnNum))
                    .orderByDesc(MybatisPlusUtil.toColumns(Video::getVisitNum));
            List<Video> beans = list(queryWrapper);
            if (CollectionUtil.isEmpty(beans)) {
                return;
            }
            for (Video video : beans) {
                video.setCheckUpvote(videoRecordService.checkUpvoteOrCollectByUserId(video, CommonNumConstants.NUM_ONE));
                video.setCheckCollection(videoRecordService.checkUpvoteOrCollectByUserId(video, CommonNumConstants.NUM_TWO));
            }
            videoTagService.setTagMationForVideoList(beans.toArray(new Video[0]));
            try{
                userService.setDataMation(beans, Video::getCreateId);
            }catch (Exception e){
                iAuthUserService.setDataMation(beans, Video::getCreateId);
            }
            outputObject.setBeans(beans);
            outputObject.settotal(page.getTotal());
        }
    }

    private <T> void setUserVideoScores(List<T> list, Map<String, Map<String, Double>> userVideoScores, double weight) {
        if (CollectionUtil.isNotEmpty(list)) {
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
    private List<String> recommendVideos(String userId,
                                         Map<String, Map<String, Double>> userVideoScores,
                                         Map<String, Map<String, Double>> similarityMatrix,
                                         int topN) {
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
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void supportOrNotVideo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String videoId = map.get("videoId").toString();
        Video video = selectById(videoId);
        int supportNum = Integer.parseInt(video.getTasnNum());
        boolean isSupport = videoRecordService.checkSupportOrCollectByVideoId(videoId, CommonNumConstants.NUM_ONE);
        supportNum = isSupport ? supportNum - CommonNumConstants.NUM_ONE : supportNum + CommonNumConstants.NUM_ONE;
        video.setTasnNum(String.valueOf(supportNum));
        updateById(video);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void collectOrNotVideo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String videoId = map.get("videoId").toString();
        Video video = selectById(videoId);
        int collectNum = Integer.parseInt(video.getCollectionNum());
        boolean isCollect = videoRecordService.checkSupportOrCollectByVideoId(videoId, CommonNumConstants.NUM_TWO);
        collectNum = isCollect ? collectNum - CommonNumConstants.NUM_ONE : collectNum + CommonNumConstants.NUM_ONE;
        video.setTasnNum(String.valueOf(collectNum));
        updateById(video);
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
