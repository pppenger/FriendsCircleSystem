package com.pppenger.microblog.config;

import javax.naming.AuthenticationException;

/**
 * 声明一个验证码异常，用于抛出特定的验证码异常
 */
public class VerifyCodeException extends AuthenticationException {
    public VerifyCodeException(String msg) {
        super(msg);
    }
}