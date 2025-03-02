/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.skyeye.common.constans.ForumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.MainPageDao;
import com.skyeye.eve.service.MainPageService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MainPageServiceImpl implements MainPageService {

    @Autowired
    private MainPageDao mainPageDao;

    @Autowired
    public JedisClientService jedisClient;

    /**
     * 获取本月考勤天数，我的文件数，我的论坛帖数
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryFourNumListByUserId(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        // 1.获取本月考勤天数
        String checkOnWorkNum = mainPageDao.queryCheckOnWorkNumByUserId(userId);
        // 2.获取我的文件数
        String diskCloudFileNum = mainPageDao.queryDiskCloudFileNumByUserId(userId);
        // 3.获取我的论坛帖数
        String forumNum = mainPageDao.queryForumNumByUserId(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("checkOnWorkNum", checkOnWorkNum);
        map.put("diskCloudFileNum", diskCloudFileNum);
        map.put("forumNum", forumNum);
        outputObject.setBean(map);
    }

}
