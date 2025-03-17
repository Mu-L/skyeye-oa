package com.skyeye.rest.school.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.school.rest.ISchoolRest;
import com.skyeye.rest.school.service.ISchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ISchoolServiceImpl extends IServiceImpl implements ISchoolService {

    @Autowired
    private ISchoolRest iSchoolRest;

    @Override
    public List<Map<String,Object>>  querySchoolStudentMation(String no){
        return ExecuteFeignClient.get(()-> iSchoolRest.querySchoolStudentListByNo(no)).getRows();
    }


}
