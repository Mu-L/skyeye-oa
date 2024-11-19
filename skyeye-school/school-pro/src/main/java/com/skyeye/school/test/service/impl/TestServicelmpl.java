package com.skyeye.school.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.test.dao.TestDao;
import com.skyeye.school.test.entity.Test;
import com.skyeye.school.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "测试案例",groupName = "测试案例")
//
public class TestServicelmpl extends SkyeyeBusinessServiceImpl<TestDao,Test> implements TestService {

    @Autowired
    private TestService testService;

//    // 分页
//    @Override
//    protected List<Map<String,Object>> queryPageDataList(InputObject inputObject){
//        //  获取分页信息
//        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
//        //  selectTestList  查询数据列表：
//        List<Map<String,Object>> beans = skyeyeBaseMapper.selectTestList(commonPageInfo);
//        //  返回
//        return beans;
//    }


    @Override
    protected QueryWrapper<Test> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Test>  queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Test::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    // 全部数据
    @Override
    public void queryAlltestall(InputObject inputObject, OutputObject outputObject) {
        // 查询所有数据
        List<Test> tests = queryAllData();
//        将查询到的 List<Test> 类型的列表 tests 作为参数传入
//        将查询到的所有Test对象设置到输出对象 outputObject 中
        outputObject.setBean(tests);
        outputObject.settotal(tests.size());
    }

    @Override
    public void validatorEntity(Test test){
        //数据校验
        //新增用户年龄不能小于等于0 大于等于100 返回年龄输入错误
        if (test.getAge() <= 0 || test.getAge() >= 100) {
            throw new RuntimeException("年龄输入错误");
        }
        //成绩不能大于100  返回请输入正确的成绩
        if (test.getChengji() > 100 ) {
            throw new RuntimeException("请输入正确的成绩");}
        }


    @Override
    public void createPrepose(Test entity) {
        entity.setChengji(88);
    }

}



