package com.pjz.review.util;

public class RedisConstants {

    public static final String LOGIN_CODE_KEY = "login:code:";

    public static final Long LOGIN_CODE_TTL = 5L;

    public static final String LOGIN_TOKEN_KEY = "login:token:";

    public static final Long LOGIN_TOKEN_TTL = 60 * 12 * 7L;

}
