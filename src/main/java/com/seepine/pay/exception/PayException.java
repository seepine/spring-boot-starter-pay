package com.seepine.pay.exception;

import com.seepine.pay.entity.payjs.PayJsRes;

/**
 * @author seepine
 */
public class PayException extends RuntimeException {
    String message;
    PayJsRes payJsRes;

    public PayException(String message, PayJsRes payJsRes) {
        this.message = message;
        this.payJsRes = payJsRes;
    }
    public PayException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public PayJsRes getPayJsRes() {
        return this.payJsRes;
    }
}
