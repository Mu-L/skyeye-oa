/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DataCommonUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constans.ForumConstants;
import com.skyeye.eve.forum.dao.ForumSensitiveWordsDao;
import com.skyeye.eve.forum.entity.ForumSensitiveWords;
import com.skyeye.eve.forum.service.ForumSensitiveWordsService;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @ClassName: ForumSensitiveWordsServiceImpl
 * @Description: 论坛敏感词管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:52
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "论坛敏感词管理", groupName = "论坛敏感词管理")
public class ForumSensitiveWordsServiceImpl extends SkyeyeBusinessServiceImpl<ForumSensitiveWordsDao, ForumSensitiveWords> implements ForumSensitiveWordsService {

    @Autowired
    private ForumSensitiveWordsDao forumSensitiveWordsDao;

    @Autowired
    private JedisClientService jedisClient;

//    /**
//     * 查出所有论坛敏感词列表
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @Override
//    public void queryForumSensitiveWordsList(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
//        List<Map<String, Object>> beans = forumSensitiveWordsDao.queryForumSensitiveWordsList(map);
//        outputObject.setBeans(beans);
//        outputObject.settotal(pages.getTotal());
//    }

    public void validatorEntity(ForumSensitiveWords forumSensitiveWords) {
        super.validatorEntity(forumSensitiveWords);
        QueryWrapper<ForumSensitiveWords> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumSensitiveWords::getSensitiveWords), forumSensitiveWords.getSensitiveWords());
        ForumSensitiveWords one = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(one)) {
            throw new CustomException("该论坛敏感词名称已存在，请更换");
        }
    }
//    @Override
//    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
//    public void insertForumSensitiveWordsMation(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        Map<String, Object> bean = forumSensitiveWordsDao.queryForumSensitiveWordsMationByName(map);
//        if (!CollectionUtils.isEmpty(bean)) {
//            outputObject.setreturnMessage("该论坛敏感词名称已存在，请更换");
//        } else {
//            DataCommonUtil.setCommonData(map, inputObject.getLogParams().get("id").toString());
//            forumSensitiveWordsDao.insertForumSensitiveWordsMation(map);
//            jedisClient.del(ForumConstants.forumSensitiveWordsAll());
//        }
//    }

//    /**
//     * 删除论坛敏感词
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @Override
//    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
//    public void deleteForumSensitiveWordsById(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        forumSensitiveWordsDao.deleteForumSensitiveWordsById(map);
//        jedisClient.del(ForumConstants.forumSensitiveWordsAll());
//    }

//    /**
//     * 通过id查找对应的论坛敏感词信息
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @Override
//    public void selectForumSensitiveWordsById(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        Map<String, Object> bean = forumSensitiveWordsDao.selectForumSensitiveWordsById(map);
//        outputObject.setBean(bean);
//        outputObject.settotal(1);
//    }

//    /**
//     * 通过id编辑对应的论坛敏感词信息
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @Override
//    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
//    public void editForumSensitiveWordsMationById(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        Map<String, Object> bean = forumSensitiveWordsDao.queryForumSensitiveWordsMationByName(map);
//        if (!CollectionUtils.isEmpty(bean)) {
//            outputObject.setreturnMessage("该论坛敏感词名称已存在，请更换");
//        } else {
//            forumSensitiveWordsDao.editForumSensitiveWordsMationById(map);
//            jedisClient.del(ForumConstants.forumSensitiveWordsAll());
//        }
//    }
}
