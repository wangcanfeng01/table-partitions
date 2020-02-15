package com.wcf.util.tp.common.utils;

import org.springframework.util.ObjectUtils;

public class ColNameTranslate {
    public static String toCamel(String info) {
        if (ObjectUtils.isEmpty(info)) {
            return info;
        }
        if (!info.contains("_")) {
            return info;
        }
        String[] strs = info.split("_");
        if (strs.length > 1) {
            StringBuilder sb = new StringBuilder(strs[0]);
            for (int i = 1; i < strs.length; i++) {
                String first = strs[i].substring(0, 1).toUpperCase();
                sb.append(first).append(strs[i].substring(1));
            }
            info = sb.toString();
        }
        return info;
    }
}
