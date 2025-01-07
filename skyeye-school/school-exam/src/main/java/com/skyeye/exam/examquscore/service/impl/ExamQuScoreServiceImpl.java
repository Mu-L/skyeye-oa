package com.skyeye.exam.examquscore.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examquscore.dao.ExamQuScoreDao;
import com.skyeye.exam.examquscore.entity.ExamQuScore;
import com.skyeye.exam.examquscore.service.ExamQuScoreService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamQuScoreServiceImpl
 * @Description: 公评分题行选项管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "评分题行选项管理", groupName = "评分题行选项管理")
public class ExamQuScoreServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuScoreDao, ExamQuScore> implements ExamQuScoreService {

    @Override
    protected QueryWrapper<ExamQuScore> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ExamQuScore> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuScore::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<ExamQuScore> score, String quId, String userId) {
        List<ExamQuScore> quScore = new ArrayList<>();
        List<ExamQuScore> editquScore = new ArrayList<>();
        for (int i = 0; i < score.size(); i++) {
            ExamQuScore object = score.get(i);
            ExamQuScore bean = new ExamQuScore();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            bean.setOptionTitle(object.getOptionTitle());
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quScore.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquScore.add(bean);
            }
        }
        if (!quScore.isEmpty()) {
            createEntity(quScore, userId);
        }
        if (!editquScore.isEmpty()) {
            updateEntity(editquScore, userId);
        }
        quScore.addAll(editquScore);
    }

    @Override
    protected void deletePreExecution(ExamQuScore entity) {
        Integer visibility = entity.getVisibility();
        if (visibility.equals(CommonNumConstants.NUM_ONE)) {
            throw new CustomException("该选项已显示，请先隐藏再删除");
        }
    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<ExamQuScore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuScore::getId), id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamQuScore::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public List<ExamQuScore> selectQuScore(String copyFromId) {
        QueryWrapper<ExamQuScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuScore::getQuId), copyFromId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuScore::getVisibility), CommonNumConstants.NUM_ONE);
        return list(queryWrapper);
    }

}
