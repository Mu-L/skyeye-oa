package com.skyeye.exam.examqumultfillblank.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
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
import com.skyeye.eve.examquestion.classenum.CheckTypes;
import com.skyeye.exam.examqumultfillblank.dao.ExamQuMultiFillblankDao;
import com.skyeye.exam.examqumultfillblank.entity.ExamQuMultiFillblank;
import com.skyeye.exam.examqumultfillblank.service.ExamQuMultiFillblankService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "多行填空题管理", groupName = "多行填空题管理")
public class ExamQuMultiFillblankControllerImpl extends SkyeyeBusinessServiceImpl<ExamQuMultiFillblankDao, ExamQuMultiFillblank> implements ExamQuMultiFillblankService {

    @Override
    protected QueryWrapper<ExamQuMultiFillblank> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ExamQuMultiFillblank> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuMultiFillblank::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<ExamQuMultiFillblank> list, String quId, String userId) {
        List<ExamQuMultiFillblank> quMultiFillblank = new ArrayList<>();
        List<ExamQuMultiFillblank> editquMultiFillblank = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ExamQuMultiFillblank object = list.get(i);
            ExamQuMultiFillblank bean = new ExamQuMultiFillblank();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            bean.setOptionTitle(object.getOptionTitle());
            bean.setIsDefaultAnswer(object.getIsDefaultAnswer());
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quMultiFillblank.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquMultiFillblank.add(bean);
            }
        }
        if (!quMultiFillblank.isEmpty()) {
            createEntity(quMultiFillblank, userId);
        }
        if (!editquMultiFillblank.isEmpty()) {
            updateEntity(editquMultiFillblank, userId);
        }
    }

    @Override
    protected void deletePreExecution(ExamQuMultiFillblank entity) {
        Integer visibility = entity.getVisibility();
        if (visibility.equals(CommonNumConstants.NUM_ONE)){
            throw new CustomException("该选项已显示，请先隐藏再删除");
        }
    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<ExamQuMultiFillblank> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuMultiFillblank::getId),id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamQuMultiFillblank::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<ExamQuMultiFillblank> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuMultiFillblank::getQuId),quId);
        remove(updateWrapper);
    }

    @Override
    public List<ExamQuMultiFillblank> selectQuMultiFillblank(String copyFromId) {
        QueryWrapper<ExamQuMultiFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuMultiFillblank::getQuId),copyFromId);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        if (StrUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuMultiFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuMultiFillblank::getBelongId),id);
        List<ExamQuMultiFillblank> list = list(queryWrapper);
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
