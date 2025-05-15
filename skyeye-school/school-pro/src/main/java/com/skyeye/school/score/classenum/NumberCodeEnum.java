/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.classenum;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.school.score.entity.ScoreTypeChild;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: NumberCodeEnum
 * @Description: 学校成绩类型枚举
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/11 13:17
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum NumberCodeEnum implements SkyeyeEnumClass {

    ALL("0", "总成绩", true, false),
    CUSTOM("1", "自定义成绩", true, true),
    WORK("2", "作业成绩", true, false),
    TEST("3", "测试成绩", true, false),
    USUAL("5", "平时成绩", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static List<ScoreTypeChild> getScoreTypeList(String subjectId, String subClassLinkId) {
        List<ScoreTypeChild> scoreTypeList = new ArrayList<>();
        String className = NumberCodeEnum.class.getName();
        for (NumberCodeEnum e : values()) {
            ScoreTypeChild scoreTypeChild = new ScoreTypeChild();
            scoreTypeChild.setName(e.getValue());
            scoreTypeChild.setParentId(StrUtil.EMPTY);
            scoreTypeChild.setProportion(CommonNumConstants.NUM_ZERO.toString());
            scoreTypeChild.setSubjectId(subjectId);
            scoreTypeChild.setSubClassLinkId(subClassLinkId);
            scoreTypeChild.setNameLinkId(e.getKey());
            scoreTypeChild.setNameLinkKey(className);
            scoreTypeList.add(scoreTypeChild);
        }
        return scoreTypeList;
    }

    public static List<Integer> getKeysButAll(){
        List<Integer> list = new ArrayList<>();
        for (NumberCodeEnum e : NumberCodeEnum.values()) {
            String key = e.getKey();
            if (!key.equals(CommonNumConstants.NUM_ZERO.toString())){
                list.add(Integer.parseInt(key));
            }
        }
        return list;
    }
}
