package com.skyeye.exam.examquorderby.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.naming.pojo.healthcheck.impl.Mysql;
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
import com.skyeye.exam.examquorderby.dao.ExamQuOrderbyDao;
import com.skyeye.exam.examquorderby.entity.ExamQuOrderby;
import com.skyeye.exam.examquorderby.service.ExamQuOrderbyService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
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
            bean.setOptionTitle(object.getOptionTitle());
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
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<ExamQuOrderby> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
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
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ExamQuOrderby::getOrderById));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        if (StrUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuOrderby> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuOrderby::getBelongId),id);
        List<ExamQuOrderby> list = list(queryWrapper);
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        list.forEach(item->{
            String quId = item.getQuId();
            if(result.containsKey(quId)){
                result.get(quId).add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
            }else {
                List<Map<String, Object>> tmp = new ArrayList<>();
                tmp.add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
                result.put(quId,tmp);
            }
        });
        return result;
    }
}
