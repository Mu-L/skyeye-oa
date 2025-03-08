/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.chen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.chen.dao.DwAnChenFbkDao;
import com.skyeye.eve.chen.entity.DwAnChenFbk;
import com.skyeye.eve.chen.service.DwAnChenFbkService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DwAnChenFbkServiceImpl
 * @Description: 答卷矩阵填空题服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "答卷矩阵填空题", groupName = "答卷矩阵填空题")
public class DwAnChenFbkServiceImpl extends SkyeyeBusinessServiceImpl<DwAnChenFbkDao, DwAnChenFbk> implements DwAnChenFbkService {

    @Override
    public void queryDwAnChenFbkListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<DwAnChenFbk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<DwAnChenFbk> dwAnChenFbkList = list(queryWrapper);
        outputObject.setBean(dwAnChenFbkList);
        outputObject.settotal(dwAnChenFbkList.size());
    }

    @Override
    public List<DwAnChenFbk> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnChenFbk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenFbk::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<DwAnChenFbk> selectByQuId(String id) {
        QueryWrapper<DwAnChenFbk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenFbk::getQuId), id);
        return list(queryWrapper);
    }
}













