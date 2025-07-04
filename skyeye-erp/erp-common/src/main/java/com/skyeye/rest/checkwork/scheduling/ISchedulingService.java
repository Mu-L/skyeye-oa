package com.skyeye.rest.checkwork.scheduling;

import com.skyeye.base.rest.service.IService;
import com.skyeye.common.object.ResultEntity;

public interface ISchedulingService extends IService {
    void deleteSchedulingByWorkId(String workId);
}
