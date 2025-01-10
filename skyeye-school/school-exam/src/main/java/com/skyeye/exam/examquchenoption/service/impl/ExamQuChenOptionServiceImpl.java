package com.skyeye.exam.examquchenoption.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examquchencolumn.entity.ExamQuChenColumn;
import com.skyeye.exam.examquchenoption.dao.ExamQuChenOptionDao;
import com.skyeye.exam.examquchenoption.entity.ExamQuChenOption;
import com.skyeye.exam.examquchenoption.service.ExamQuChenOptionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "矩陈题-题选项管理", groupName = "矩陈题-题选项管理")
public class ExamQuChenOptionServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuChenOptionDao, ExamQuChenOption> implements ExamQuChenOptionService {

    @Override
    public void saveList(List<ExamQuChenOption> list, String quId, String userId) {
        List<ExamQuChenOption> quOption = new ArrayList<>();
        List<ExamQuChenOption> editquOption = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ExamQuChenOption object = list.get(i);
            ExamQuChenOption bean = new ExamQuChenOption();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(object.getQuId());
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quOption.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquOption.add(bean);
            }
        }
        if (!quOption.isEmpty()) {
            createEntity(quOption, userId);
        }
        if (!editquOption.isEmpty()) {
            updateEntity(editquOption, userId);
        }
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        QueryWrapper<ExamQuChenOption> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenOption::getBelongId), id);
        List<ExamQuChenOption> list = list(queryWrapper);
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
