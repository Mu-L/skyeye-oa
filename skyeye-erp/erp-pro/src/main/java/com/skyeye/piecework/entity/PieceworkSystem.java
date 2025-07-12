package com.skyeye.piecework.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "erp_piecework_system")
@ApiModel("计件数量或工时统计实体类")
public class PieceworkSystem extends BaseGeneralInfo {

    @TableField("job_number")
    @ApiModelProperty("员工工号")
    private String jobNumber;

    @TableField("department_id")
    @ApiModelProperty("部门id")
    private String departmentId;

    @TableField("farm_id")
    @ApiModelProperty("车间id")
    private String farmId;

    @TableField("farm_station_id")
    @ApiModelProperty("车间工位id")
    private String farmStationId;

    @TableField("day_mouth")
    @ApiModelProperty("年月（yyyy-MM）")
    private String dayMouth;

    @TableField("is_num_time")
    @ApiModelProperty("1 按照计件数量 2 按照工时")
    private Integer isNumTime;

    @TableField("total_num_price")
    @ApiModelProperty("计件工一件的价格")
    private BigDecimal totalNumPrice;

    @TableField("all_num")
    @ApiModelProperty("总数量")
    private BigDecimal allNum;

    @TableField("total_time_price")
    @ApiModelProperty("小时工一小时价钱")
    private String totalTimePrice;

    @TableField("all_time")
    @ApiModelProperty("总工时")
    private String allTime;

    @TableField("all_price")
    @ApiModelProperty("总金额")
    private String allPrice;

    @TableField("one_day_num")
    @ApiModelProperty("1日报工数量/工时")
    private String oneDayNum;

    @TableField("two_day_num")
    @ApiModelProperty("2日报工数量/工时")
    private String twoDayNum;

    @TableField("three_day_num")
    @ApiModelProperty("3日报工数量/工时")
    private String threeDayNum;

    @TableField("four_day_num")
    @ApiModelProperty("4日报工数量/工时")
    private String fourDayNum;

    @TableField("five_day_num")
    @ApiModelProperty("5日报工数量/工时")
    private String fiveDayNum;

    @TableField("six_day_num")
    @ApiModelProperty("6日报工数量/工时")
    private String sixDayNum;

    @TableField("seven_day_num")
    @ApiModelProperty("7日报工数量/工时")
    private String sevenDayNum;

    @TableField("eight_day_num")
    @ApiModelProperty("8日报工数量/工时")
    private String eightDayNum;

    @TableField("nine_day_num")
    @ApiModelProperty("9日报工数量/工时")
    private String nineDayNum;

    @TableField("ten_day_num")
    @ApiModelProperty("10日报工数量/工时")
    private String tenDayNum;

    @TableField("eleven_day_num")
    @ApiModelProperty("11日报工数量/工时")
    private String elevenDayNum;

    @TableField("twelve_day_num")
    @ApiModelProperty("12日报工数量/工时")
    private String twelveDayNum;

    @TableField("thirteen_day_num")
    @ApiModelProperty("13日报工数量/工时")
    private String thirteenDayNum;

    @TableField("fourteen_day_num")
    @ApiModelProperty("14日报工数量/工时")
    private String fourteenDayNum;

    @TableField("fifteen_day_num")
    @ApiModelProperty("15日报工数量/工时")
    private String fifteenDayNum;

    @TableField("sixteen_day_num")
    @ApiModelProperty("16日报工数量/工时")
    private String sixteenDayNum;

    @TableField("seventeen_day_num")
    @ApiModelProperty("17日报工数量/工时")
    private String seventeenDayNum;

    @TableField("eighteen_day_num")
    @ApiModelProperty("18日报工数量/工时")
    private String eighteenDayNum;

    @TableField("nineteen_day_num")
    @ApiModelProperty("19日报工数量/工时")
    private String nineteenDayNum;

    @TableField("twenty_day_num")
    @ApiModelProperty("20日报工数量/工时")
    private String twentyDayNum;

    @TableField("twenty_one_day_num")
    @ApiModelProperty("21日报工数量/工时")
    private String twentyOneDayNum;

    @TableField("twenty_two_day_num")
    @ApiModelProperty("22日报工数量/工时")
    private String twentyTwoDayNum;

    @TableField("twenty_three_day_num")
    @ApiModelProperty("23日报工数量/工时")
    private String twentyThreeDayNum;

    @TableField("twenty_four_day_num")
    @ApiModelProperty("24日报工数量/工时")
    private String twentyFourDayNum;

    @TableField("twenty_five_day_num")
    @ApiModelProperty("25日报工数量/工时")
    private String twentyFiveDayNum;

    @TableField("twenty_six_day_num")
    @ApiModelProperty("26日报工数量/工时")
    private String twentySixDayNum;

    @TableField("twenty_seven_day_num")
    @ApiModelProperty("27日报工数量/工时")
    private String twentySevenDayNum;

    @TableField("twenty_eight_day_num")
    @ApiModelProperty("28日报工数量/工时")
    private String twentyEightDayNum;

    @TableField("twenty_nine_day_num")
    @ApiModelProperty("29日报工数量/工时")
    private String twentyNineDayNum;

    @TableField("thirty_day_num")
    @ApiModelProperty("30日报工数量/工时")
    private String thirtyDayNum;

    @TableField("thirty_one_day_num")
    @ApiModelProperty("31日报工数量/工时")
    private String thirtyOneDayNum;
}
