package com.skyeye.videocomment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.video.entity.Video;
import com.skyeye.video.service.VideoService;
import com.skyeye.videocomment.dao.VideoCommentDao;
import com.skyeye.videocomment.entity.VideoComment;
import com.skyeye.videocomment.service.VideoCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: VideoCommentServiceImpl
 * @Description: 视频评论业务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "视频评论管理", groupName = "视频评论管理")
public class VideoCommentServiceImpl extends SkyeyeBusinessServiceImpl<VideoCommentDao, VideoComment> implements VideoCommentService {

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoCommentService videoCommentService;

    // validatorEntity 前置执行
    @Override
        public void validatorEntity(VideoComment entity) {
        super.validatorEntity(entity);
        // 获取实体类中的parentId属性值
        String parentId = entity.getParentId();
        // 判断传入的parentId是否为空
            //1.查找这条评论的 parentId
        VideoComment videoComment = videoCommentService.selectById(parentId);
        if (StrUtil.isNotEmpty(videoComment.getParentId())){
            throw new CustomException("不可评论");
        }
    }

    //    重写queryPageDataList方法
    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryVideoCommentList(commonPageInfo);
        List<String> ids = beans.stream()
                .map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        videoCommentService.setMationForMap(beans, "createId", "createMation");
        videoCommentService.setMationForMap(beans, "userId", "userMation");
        return beans;
    }


    //    用户所创建的所有评论记录
    @Override
    public List<VideoComment> queryVideoCommentList(String userId) {
        QueryWrapper<VideoComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoComment::getCreateId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(VideoComment::getCreateTime));
        return list(queryWrapper);
    }


    @Override
    public void queryVideoCommentList(InputObject inputObject, OutputObject outputObject) {
        List<VideoComment> videoCommentList = queryAllData();
        outputObject.setBean(videoCommentList);
        outputObject.settotal(videoCommentList.size());

    }

    // 新增评论  评论+1
    @Transactional  //事务
    @Override
    // createPostpose后置执行  entity(新插入的数据)   userId(用户id)
    protected void createPostpose(VideoComment entity, String userId) {
        // 获取 新插入数据的 视频id
        String videoId = entity.getVideoId();
        // 获取 videoService视频表相应的id
        Video video = videoService.selectById(videoId);
        // 获取评论数量转成int
        Integer remarkNum = Integer.parseInt(video.getRemarkNum());
        // 相加
        remarkNum++;
        // 把获取的数据插入评论数量里面
        video.setRemarkNum(String.valueOf(remarkNum));
        //更新表
        videoService.updateEntity(video, userId);
    }


    @Transactional  //事务
    @Override
    public void deleteById(InputObject inputObject, OutputObject outputObject) {
        //拿到id
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        // 用户id
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        VideoComment videoComment = selectById(id);
        super.deleteById(id);
        //
        String videoId = videoComment.getVideoId();
        //根据 videoId 获取评论数量
        Video video = videoService.selectById(videoId);
        Integer videoRemarkNum = Integer.parseInt(video.getRemarkNum());
        videoRemarkNum--;
        // 更新表
        video.setRemarkNum(String.valueOf(videoRemarkNum));
        videoService.updateEntity(video, userId);
    }


//    //重写新增的方法
//    @Override
//    protected void validatorEntity(T entity) {
//        String id = (String) ReflectUtil.getFieldValue(entity, "id");
//        QueryWrapper<T> wrapper = this.queryWrapper(entity, id);
//        if (wrapper != null) {
//            T result = (CommonInfo)this.getOne(wrapper);
//            if (ObjectUtil.isNotEmpty(result)) {
//                throw new RuntimeException("The same data exists.");
//            }
//        }

}
