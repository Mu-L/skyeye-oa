/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.datum.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.chapter.service.ChapterService;
import com.skyeye.school.datum.dao.DatumDao;
import com.skyeye.school.datum.entity.Datum;
import com.skyeye.school.datum.service.DatumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName: DatumServiceImpl
 * @Description: 资料信息管理服务层
 * @author: luyujia
 * @date: 2024/7/14 16:57
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "资料管理", groupName = "资料管理")
public class DatumServiceImpl extends SkyeyeBusinessServiceImpl<DatumDao, Datum> implements DatumService {

    @Autowired
    private ChapterService chapterService;

    @Override
    public Datum selectById(String id) {
        Datum datum = super.selectById(id);
        chapterService.setDataMation(datum, Datum::getChapterId);
        if (ObjectUtil.isNotEmpty(datum.getChapterMation())) {
            datum.getChapterMation().setName(String.format(Locale.ROOT, "第 %s 章 %s", datum.getChapterMation().getSection(), datum.getChapterMation().getName()));
        }
        iAuthUserService.setDataMation(datum, Datum::getCreateId);
        return datum;
    }

    @Override
    public void queryDatumListBySubjectClassesId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subjectClassesId = map.get("subjectClassesId").toString();
        QueryWrapper<Datum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Datum::getSubjectClassesId), subjectClassesId);
        List<Datum> datumList = list(queryWrapper);
        if (CollectionUtil.isEmpty(datumList)) {
            return;
        }
        chapterService.setDataMation(datumList, Datum::getChapterId);
        datumList.forEach(datum -> {
            if (ObjectUtil.isNotEmpty(datum.getChapterMation())) {
                datum.getChapterMation().setName(String.format(Locale.ROOT, "第 %s 章 %s", datum.getChapterMation().getSection(), datum.getChapterMation().getName()));
            }
        });
        iAuthUserService.setDataMation(datumList, Datum::getCreateId);
        outputObject.setBeans(datumList);
        outputObject.settotal(datumList.size());
    }

    @Override
    public Map<String, Double> queryDatumByChapterId(Long classNum, String... ids) {
        Map<String, Double> map = new HashMap<>();
        double sumSize = 0;
        double finishRate = 0;
        map.put("activeNum", sumSize);
        map.put("finishRate", finishRate);
        if(classNum == 0){
            return map;
        }
        for (String id : ids) {
            QueryWrapper<Datum> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(Datum::getChapterId), id);
            List<Datum> list = list(queryWrapper);
            if (CollectionUtil.isEmpty(list)) {
                continue;
            }
            sumSize += list.size();
            double rate = (double) list.size() / classNum;
            finishRate = finishRate + rate;
        }
        if(finishRate == 0 && ids.length > 1){
            finishRate = finishRate / ids.length;
        }
        map.put("finishRate", finishRate);
        return map;
    }

    @Override
    public Long queryClassDataNum(String id) {
        QueryWrapper<Datum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Datum::getSubjectClassesId), id);
        return count(queryWrapper);
    }
}