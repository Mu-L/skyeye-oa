/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personrequire.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: PersonRequire
 * @Description: 人员需求申请实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/4/9 18:30
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "boss_person_require", autoResultMap = true)
@ApiModel("人员需求申请实体类")
public class PersonRequire extends SkyeyeFlowable {

    @TableField(value = "recruit_department_id")
    @ApiModelProperty(value = "招聘部门id", required = "required")
    private String recruitDepartmentId;

    @TableField(exist = false)
    @Property(value = "招聘部门信息")
    private Map<String, Object> recruitDepartmentMation;

    @TableField(value = "recruit_job_id")
    @ApiModelProperty(value = "招聘岗位id", required = "required")
    private String recruitJobId;

    @TableField(exist = false)
    @Property(value = "招聘岗位信息")
    private Map<String, Object> recruitJobMation;

    @TableField(value = "recruit_num")
    @ApiModelProperty(value = "招聘人数", required = "required")
    private Integer recruitNum;

    @TableField(value = "wages")
    @ApiModelProperty(value = "薪资范围", required = "required")
    private String wages;

    @TableField(value = "job_require")
    @ApiModelProperty(value = "岗位要求", required = "required")
    private String jobRequire;

    @TableField(value = "remark")
    @ApiModelProperty(value = "说明备注")
    private String remark;

    @TableField(value = "person_liable", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "人员需求的责任人")
    private List<String> personLiable;

    @TableField(exist = false)
    @Property(value = "人员需求的责任人信息")
    private List<Map<String, Object>> personLiableMation;

    @TableField(value = "recruited_num")
    @Property(value = "已招聘人数")
    private Integer recruitedNum;

}
