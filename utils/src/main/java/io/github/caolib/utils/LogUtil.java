package io.github.caolib.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtil {
    /**
     * 打印精简的堆栈信息（只保留类名和异常信息）
     */
    public static void logErr(Throwable e, String message) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        // 获取第一个异常位置
        if (stackTrace.length > 0) {
            String exceptionName = e.getClass().getSimpleName(); // 异常类
            StackTraceElement element = stackTrace[0]; // 异常堆栈元素
            String className = element.getClassName(); // 类名
            String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
            log.error("{}:{} ({}.java:{})", exceptionName, message, simpleClassName, element.getLineNumber());
        }
    }
}