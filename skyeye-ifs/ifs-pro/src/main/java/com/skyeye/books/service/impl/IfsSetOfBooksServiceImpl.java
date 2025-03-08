/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.books.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.books.dao.IfsSetOfBooksDao;
import com.skyeye.books.entity.SetOfBooks;
import com.skyeye.books.service.IfsSetOfBooksService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IfsSetOfBooksServiceImpl
 * @Description: 账套管理服务类
 * @author: skyeye云系列
 * @date: 2021/11/21 14:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "账套管理", groupName = "账套管理")
public class IfsSetOfBooksServiceImpl extends SkyeyeBusinessServiceImpl<IfsSetOfBooksDao, SetOfBooks> implements IfsSetOfBooksService {

    @Override
    public void validatorEntity(SetOfBooks entity) {
        super.validatorEntity(entity);
        if (DateUtil.compare(entity.getEndTime(), entity.getStartTime())) {
            // 结束时间早于开始时间
            throw new CustomException("结束时间不能早于开始时间");
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        for (Map<String, Object> bean : beans) {
            String startTime = bean.get("startTime").toString();
            String endTime = bean.get("endTime").toString();
            String currentTime = DateUtil.getYmdTimeAndToString();
            if (DateUtil.getDistanceDay(startTime, currentTime) >= 0 && DateUtil.getDistanceDay(currentTime, endTime) >= 0) {
                // startTime <= 当前时间 <= endTime
                bean.put("haveAccess", true);
            } else {
                bean.put("haveAccess", false);
            }
        }
        return beans;
    }

}

