package io.github.caolib.exception;

public class GitHubLoginException extends CommonException{
    public GitHubLoginException() {
        super("获取GitHub用户信息失败", 401);
    }

    public GitHubLoginException(String message, int code) {
        super(message, code);
    }

    public GitHubLoginException(String message, Throwable cause, int code) {
        super(message, cause, code);
    }

    public GitHubLoginException(Throwable cause, int code) {
        super(cause, code);
    }
}
