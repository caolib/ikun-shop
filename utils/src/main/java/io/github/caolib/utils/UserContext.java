package io.github.caolib.utils;

/**
 * 用户上下文信息
 */
public class UserContext {
    private static final ThreadLocal<Long> id = new ThreadLocal<>();
    private static final ThreadLocal<String> identity = new ThreadLocal<>();

    /**
     * 保存当前登录用户信息到ThreadLocal
     * @param userId 用户id
     */
    public static void setUser(Long userId,String i) {
        id.set(userId);
        identity.set(i);
    }

    /**
     * 获取当前登录用户信息
     * @return 用户id
     */
    public static Long getUserId() {
        return id.get();
    }

    /**
     * 获取当前登录用户身份
     */
    public static String getIdentity() {
        return identity.get();
    }

    /**
     * 移除当前登录用户信息
     */
    public static void removeUser(){
        id.remove();
        identity.remove();
    }
}
