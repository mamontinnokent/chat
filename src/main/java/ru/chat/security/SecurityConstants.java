package ru.chat.security;

public class SecurityConstants {

    public static final long EXPIRAION_TIME = 600_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String SECRET = "SecretChatWord";
    public static final String HEADER_STRING = "Authorization";
    public static final String CONTENT_TYPE = "application/json";

    public static final String SIGN_UP_URLS = "/api/v1/auth/**";

}
