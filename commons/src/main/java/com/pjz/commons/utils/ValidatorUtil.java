package com.pjz.commons.utils;

import java.util.regex.Pattern;

public class ValidatorUtil {

    // 中国手机号正则表达式
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    // 邮箱正则表达式
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,6}$";

    // 中国公民身份证正则表达式
    private static final String ID_CARD_REGEX = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[1-2]\\d|3[0-1])\\d{3}[0-9Xx]$";

    /**
     * 校验手机号是否合法
     *
     * @param phone 手机号
     * @return 是否合法
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return Pattern.matches(PHONE_REGEX, phone);
    }

    /**
     * 校验邮箱是否合法
     *
     * @param email 邮箱
     * @return 是否合法
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return Pattern.matches(EMAIL_REGEX, email);
    }

    /**
     * 校验中国公民身份证是否合法
     *
     * @param idCard 身份证号
     * @return 是否合法
     */
    public static boolean isValidIdCard(String idCard) {
        return Pattern.matches(ID_CARD_REGEX, idCard);
    }
}
