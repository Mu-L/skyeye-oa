package com.skyeye.joincircle.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.joincircle.dao.JoinLimitDao;
import com.skyeye.joincircle.entity.JoinLimit;
import com.skyeye.joincircle.service.JoinLimitService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: JoinLimitServiceImpl
 * @Description: 加入圈子限制服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "加入圈子", groupName = "加入圈子")
public class JoinLimitServiceImpl extends SkyeyeBusinessServiceImpl<JoinLimitDao, JoinLimit> implements JoinLimitService {


    @Override
    public boolean checkIsAllowJoin(String circleId, String userId) {
        QueryWrapper<JoinLimit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JoinLimit::getObjectId),circleId)
                .eq(MybatisPlusUtil.toColumns(JoinLimit::getUserId),userId);
        return count(queryWrapper) < CommonNumConstants.NUM_TWO;
    }
}
