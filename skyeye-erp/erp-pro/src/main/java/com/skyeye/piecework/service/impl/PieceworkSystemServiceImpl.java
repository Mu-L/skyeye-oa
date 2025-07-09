package com.skyeye.piecework.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.piecework.dao.PieceworkSystemDao;
import com.skyeye.piecework.entity.PieceworkSystem;
import com.skyeye.piecework.service.PieceworkSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@SkyeyeService(name = "计件数量或工时统计信息", groupName = "计件数量或工时统计信息", manageShow = false)
public class PieceworkSystemServiceImpl extends SkyeyeBusinessServiceImpl<PieceworkSystemDao, PieceworkSystem> implements PieceworkSystemService {

    @Override
    public void writePieceworkSystem(InputObject inputObject, OutputObject outputObject) {
        // 获取用户Id
        String staffId = inputObject.getLogParams().get("staffId").toString();
        // 获取用户信息
        Map<String, Object> staffMation = iAuthUserService.queryDataMationById(staffId);

    }
}
