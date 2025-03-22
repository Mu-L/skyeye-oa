/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: WallConstants
 * @Description: 表白墙模块常量类
 * @author: skyeye云系列--卫志强
 * @date: 2023/9/5 9:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class WallConstants {

    // 是或否的枚举类
    public static enum YesORNo {
        YES("1", "是"),
        NO("2", "否");

        private String value;
        private String name;

        YesORNo(String value, String name) {
            this.value = value;
            this.name = name;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public static List<Map> getList() {
            List<Map> list = new ArrayList<>();
            for (YesORNo q : YesORNo.values()) {
                Map item = new HashMap<>();
                item.put("dictKey", q.getValue());
                item.put("dictValue", q.getName());
                list.add(item);
            }
            return list;
        }

        public static String getValueByName(String name) {
            for (YesORNo q : YesORNo.values()) {
                if (q.getName().equals(name)) {
                    return q.getValue();
                }
            }
            return "";
        }

        public static String getNameByValue(String value) {
            for (YesORNo q : YesORNo.values()) {
                if (q.getValue().equals(value)) {
                    return q.getName();
                }
            }
            return "";
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    // 用户身份的key常量
    public static final String USER_IDENTITY_KEY = "userIdentity";

}
