package com.skyeye.eve.score.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.orderby.entity.DwQuOrderby;
import com.skyeye.eve.score.dao.DwQuScoreDao;
import com.skyeye.eve.score.entity.DwQuScore;
import com.skyeye.eve.score.service.DwQuScoreService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: DwQuScoreServiceImpl
 * @Description: 公评分题行选项管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "评分题行选项管理", groupName = "评分题行选项管理")
public class DwQuScoreServiceImpl extends SkyeyeBusinessServiceImpl<DwQuScoreDao, DwQuScore> implements DwQuScoreService {

    @Override
    protected QueryWrapper<DwQuScore> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DwQuScore> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuScore::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<DwQuScore> score, String quId, String userId) {
        List<DwQuScore> quScore = new ArrayList<>();
        List<DwQuScore> editquScore = new ArrayList<>();
        for (int i = 0; i < score.size(); i++) {
            DwQuScore object = score.get(i);
            DwQuScore bean = new DwQuScore();
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
    protected void deletePreExecution(DwQuScore entity) {
        Integer visibility = entity.getVisibility();
        if (visibility.equals(CommonNumConstants.NUM_ONE)) {
            throw new CustomException("该选项已显示，请先隐藏再删除");
        }
    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<DwQuScore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwQuScore::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public List<DwQuScore> selectQuScore(String copyFromId) {
        QueryWrapper<DwQuScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuScore::getQuId), copyFromId);
//        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuScore::getVisibility), CommonNumConstants.NUM_ONE);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        if (StrUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<DwQuScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuScore::getBelongId), id);
        List<DwQuScore> list = list(queryWrapper);
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        list.forEach(item -> {
            String quId = item.getQuId();
            if (result.containsKey(quId)) {
                result.get(quId).add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
            } else {
                List<Map<String, Object>> tmp = new ArrayList<>();
                tmp.add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
                result.put(quId, tmp);
            }
        });
        return result;
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<DwQuScore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuScore::getQuId), quId);
        remove(updateWrapper);
    }

}
