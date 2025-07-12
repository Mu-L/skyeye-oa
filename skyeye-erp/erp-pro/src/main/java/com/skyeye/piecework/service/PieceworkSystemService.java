package com.skyeye.piecework.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.piecework.entity.PieceworkSystem;

public interface PieceworkSystemService extends SkyeyeBusinessService<PieceworkSystem> {

    void writePieceworkSystem(InputObject inputObject, OutputObject outputObject);

}
