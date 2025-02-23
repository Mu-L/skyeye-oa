/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.checkbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.checkbox.dao.DwAnCheckboxDao;
import com.skyeye.eve.checkbox.entity.DwAnCheckbox;
import com.skyeye.eve.checkbox.service.DwAnCheckboxService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DwAnCheckboxServiceImpl
 * @Description: 答卷多选题保存服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "答卷多选题保存", groupName = "答卷多选题保存", manageShow = false)
public class DwAnCheckboxServiceImpl extends SkyeyeBusinessServiceImpl<DwAnCheckboxDao, DwAnCheckbox> implements DwAnCheckboxService {

    @Override
    public List<DwAnCheckbox> selectAnCheckBoxByQuId(String id) {
        QueryWrapper<DwAnCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCheckbox::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public void queryDwAnCheckboxListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<DwAnCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<DwAnCheckbox> examAnCheckboxList = list(queryWrapper);
        outputObject.setBean(examAnCheckboxList);
        outputObject.settotal(examAnCheckboxList.size());
    }

    @Override
    public List<DwAnCheckbox> slectBySurveyId(String surveyId) {
        QueryWrapper<DwAnCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCheckbox::getBelongId), surveyId);
        return list(queryWrapper);
    }
}
