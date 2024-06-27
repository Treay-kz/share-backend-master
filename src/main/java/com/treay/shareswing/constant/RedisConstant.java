package com.treay.shareswing.constant;

public interface RedisConstant {
    /**
     * 发送邮件的锁的key
     */
 String SEND_EMAIL_KEY = "shareswing:user:sendEmail:";
    /**
     * 验证码的key
     */
   String EMAIL_KEY = "shareswing:user:email:";

}
