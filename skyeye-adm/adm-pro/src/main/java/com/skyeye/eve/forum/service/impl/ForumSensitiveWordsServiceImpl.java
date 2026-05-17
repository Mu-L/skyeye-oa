/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.forum.dao.ForumSensitiveWordsDao;
import com.skyeye.eve.forum.entity.ForumSensitiveWords;
import com.skyeye.eve.forum.service.ForumSensitiveWordsService;
import com.skyeye.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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

    public void validatorEntity(ForumSensitiveWords forumSensitiveWords) {
        super.validatorEntity(forumSensitiveWords);
        QueryWrapper<ForumSensitiveWords> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumSensitiveWords::getSensitiveWord), forumSensitiveWords.getSensitiveWord());

        if (StringUtils.isNotEmpty(forumSensitiveWords.getId())) {
            queryWrapper.ne(CommonConstants.ID, forumSensitiveWords.getId());
        }
        ForumSensitiveWords one = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(one)) {
            throw new CustomException("该敏感词已存在，请更换");
        }
    }

}
