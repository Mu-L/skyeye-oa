package com.skyeye.eve.chen.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.chen.dao.DwAnChenScoreDao;
import com.skyeye.eve.chen.entity.DwAnChenScore;
import com.skyeye.eve.chen.service.DwAnChenScoreService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: DwAnChenScoreServiceImpl
 * @Description: 答卷矩阵评分题服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */

@Service
@SkyeyeService(name = "答卷矩阵评分题", groupName = "答卷矩阵评分题", manageShow = false)
public class DwAnChenScoreServiceImpl extends SkyeyeBusinessServiceImpl<DwAnChenScoreDao, DwAnChenScore> implements DwAnChenScoreService {


    @Override
    protected void createPostpose(DwAnChenScore entity, String userId) {
        List<DwAnChenScore> dFillblankAn = entity.getDwChenScoreAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, userId);
        }
    }

    @Override
    protected void updatePostpose(DwAnChenScore entity, String userId) {
        List<DwAnChenScore> chenCheckboxAn = entity.getDwChenScoreAn();
        QueryWrapper<DwAnChenScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(CommonConstants.ID,  entity.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenScore::getBelongId), entity.getBelongId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenScore::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenScore::getBelongAnswerId), entity.getBelongAnswerId());
        List<DwAnChenScore> dwAnChenCheckboxList = list(queryWrapper);//数据库数据
        List<DwAnChenScore> NoIdChenCheckbox = chenCheckboxAn.stream().filter(
                e -> StrUtil.isEmpty(e.getId())).collect(Collectors.toList());//id为空的数据
        List<DwAnChenScore> YesIdChenCheckbox = chenCheckboxAn.stream().filter(
                e -> StrUtil.isNotEmpty(e.getId())).collect(Collectors.toList());//id不为空的数据
        Set<String> yesIdSet = YesIdChenCheckbox.stream().map(DwAnChenScore::getId).collect(Collectors.toSet());
        List<DwAnChenScore> result = dwAnChenCheckboxList.stream().filter(
                e -> !yesIdSet.contains(e.getId())).collect(Collectors.toList());
        List<String> TodeleteIds = result.stream().map(DwAnChenScore::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(TodeleteIds)) {
            deleteById(TodeleteIds);
        }
        if (CollectionUtil.isNotEmpty(NoIdChenCheckbox)) {
            super.createEntity(NoIdChenCheckbox, userId);
        }
        if (CollectionUtil.isNotEmpty(YesIdChenCheckbox)) {
            super.updateEntity(YesIdChenCheckbox, userId);
        }
    }

    @Override
    public DwAnChenScore selectById(String id) {
        DwAnChenScore dwAnChenCheckbox = super.selectById(id);
        String belongAnswerId = dwAnChenCheckbox.getBelongAnswerId();
        String belongId = dwAnChenCheckbox.getBelongId();
        String quId = dwAnChenCheckbox.getQuId();
        QueryWrapper<DwAnChenScore> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnChenScore::getBelongAnswerId), belongAnswerId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnChenScore::getBelongId), belongId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnChenScore::getQuId), quId);
        queryWrapper1.ne(CommonConstants.ID, id);
        dwAnChenCheckbox.setDwChenScoreAn(list(queryWrapper1));
        return dwAnChenCheckbox;
    }

    @Override
    public void queryDwAnChenScoreListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<DwAnChenScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<DwAnChenScore> dwAnChenScoreList = list(queryWrapper);
        outputObject.setBean(dwAnChenScoreList);
        outputObject.settotal(dwAnChenScoreList.size());
    }

    @Override
    public List<DwAnChenScore> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnChenScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenScore::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<DwAnChenScore> slectByQuId(String id) {
        QueryWrapper<DwAnChenScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenScore::getQuId), id);
        return list(queryWrapper);
    }

}

