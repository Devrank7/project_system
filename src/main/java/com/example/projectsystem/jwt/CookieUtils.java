package com.example.projectsystem.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtils {

    public static final String JWT_COOKIE_NAME = "jwt";

    public static void setCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(3 * 60); // 30 minutes
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JWT_COOKIE_NAME.equals(cookie.getName())) {

                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    public static boolean deleteCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    return true;
                }
            }
        }
        return false;
    }

}
