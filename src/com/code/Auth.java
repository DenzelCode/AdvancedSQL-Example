package com.code;

import java.util.HashMap;
import java.util.Map;

public class Auth {

    private static boolean authenticated = false;

    private static Map<String, Object> user;

    public static void setAuthenticated(boolean authenticated) {
        Auth.authenticated = authenticated;
    }

    public static boolean isAutenticated() {
        return authenticated;
    }

    public static Map<String, Object> getUser() {
        return user;
    }

    public static void setUser(Map<String, Object> user) {
        Auth.user = user;
    }
}
