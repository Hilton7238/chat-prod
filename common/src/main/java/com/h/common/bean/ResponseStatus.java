package com.h.common.bean;

public enum ResponseStatus {
    //200 success  204 flush token 205authorizefail 209 loginfail 208 REGISTERFAIL(register fail) 207 USEREXIT(USER EXIT) 206 WARNCODE
    //210 change password fail 500service error
    AUTHORIZE_FAIL("authorize fail", 401), SUCCESS("SUCCESS", 200), Login_FaIl("login fail", 402), REGISTER_FAIL("register error", 403),
    USER_EXIT("user exit", 405), WARN_CODE("warn code", 406), CHANGE_PASSWORD_FAIL("that email don't bind uid", 407), FLUSH_TOKEN("flush token", 204),
    SERVICE_ERROR("service error", 500), CODE_TIME_OUT("CODE TIME OUT", 400), MAIL_ALREADY_EXIST("MAIL EXIT", 408);
    private String msg;
    private int code;

    ResponseStatus(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    ResponseStatus() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
