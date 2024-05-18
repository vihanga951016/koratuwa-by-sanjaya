package com.ssd.koratuwabackend.common.http;

import com.ssd.koratuwabackend.common.constants.ApplicationConstant;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class HttpResponse<T> {
    private Date timestamp = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
    private String message;
    private T data;

    public HttpResponse<T> responseOk(T data){
        message = ApplicationConstant.RESPONSE_OK;
        this.data = data;
        return this;
    }

    public HttpResponse<T> responseOk(String msg, T data){
        message = msg;
        this.data = data;
        return this;
    }

    public HttpResponse<T> responseFail(T data){
        message = (String) data;
        this.data = data;
        return this;
    }

    public HttpResponse() {
    }

    public HttpResponse(String message, T data){
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ResponseWrapper{"
                + "message='" + message + '\''
                + ", data=" + data
                + '}';
    }
}
