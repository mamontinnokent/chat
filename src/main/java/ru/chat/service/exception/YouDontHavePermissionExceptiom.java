package ru.chat.service.exception;

public class YouDontHavePermissionExceptiom extends Throwable {
    public YouDontHavePermissionExceptiom(String message) {
        super(message);
    }
}
