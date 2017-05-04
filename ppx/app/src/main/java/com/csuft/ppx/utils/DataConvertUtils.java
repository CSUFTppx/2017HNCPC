package com.csuft.ppx.utils;

/**
 * Created by zf on 2017/5/2.
 */

public class DataConvertUtils {
    /**
     * byte数组转十六进制String
     *
     * @param src
     *            输入转换的数组
     * @return 结果
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
