package com.skyeye.exam.examquorderby.service.impl;

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
import com.skyeye.exam.examquorderby.dao.ExamQuOrderbyDao;
import com.skyeye.exam.examquorderby.entity.ExamQuOrderby;
import com.skyeye.exam.examquorderby.service.ExamQuOrderbyService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "排序题行选项管理", groupName = "排序题行选项管理")
public class ExamQuOrderbyServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuOrderbyDao, ExamQuOrderby> implements ExamQuOrderbyService {

    @Override
    protected QueryWrapper<ExamQuOrderby> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ExamQuOrderby> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuOrderby::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<ExamQuOrderby> score, String quId, String userId) {
        List<ExamQuOrderby> quOrderBy = new ArrayList<>();
        List<ExamQuOrderby> editquOrderBy = new ArrayList<>();
        for (int i = 0; i < score.size(); i++) {
            ExamQuOrderby object = score.get(i);
            ExamQuOrderby bean = new ExamQuOrderby();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quOrderBy.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquOrderBy.add(bean);
            }
        }
        if (!quOrderBy.isEmpty()) {
            createEntity(quOrderBy, userId);
        }
        if (!editquOrderBy.isEmpty()) {
            updateEntity(editquOrderBy, userId);
        }
        quOrderBy.addAll(editquOrderBy);
    }

    @Override
    protected void deletePreExecution(ExamQuOrderby entity) {
        Integer visibility = entity.getVisibility();
        if (visibility.equals(CommonNumConstants.NUM_ONE)) {
            throw new CustomException("该选项已显示，请先隐藏再删除");
        }
    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<ExamQuOrderby> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuOrderby::getId), id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamQuOrderby::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<ExamQuOrderby> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuOrderby::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<ExamQuOrderby> selectQuOrderby(String copyFromId) {
        QueryWrapper<ExamQuOrderby> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuOrderby::getQuId), copyFromId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuOrderby::getVisibility), CommonNumConstants.NUM_ONE);
        return list(queryWrapper);
    }
}
