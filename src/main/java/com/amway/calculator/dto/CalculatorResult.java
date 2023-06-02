package com.amway.calculator.dto;

import java.math.BigDecimal;

public class CalculatorResult {


    private int code;    //错误码，0为成功
    private String msg;  //错误信息

    private BigDecimal result;      // 累计计算值
    private String operator;        // 计算符
    private BigDecimal valueFirst;  // 第一个计算值
    private BigDecimal valueSecond; // 第二个计算值

    public CalculatorResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public BigDecimal getValueFirst() {
        return valueFirst;
    }

    public void setValueFirst(BigDecimal valueFirst) {
        this.valueFirst = valueFirst;
    }

    public BigDecimal getValueSecond() {
        return valueSecond;
    }

    public void setValueSecond(BigDecimal valueSecond) {
        this.valueSecond = valueSecond;
    }

    public String toString() {
        StringBuffer strb = new StringBuffer();
        strb.append(",operator=").append(operator)
                .append(",valueFirst=").append(valueFirst)
                .append(",valueSecond=").append(valueSecond)
                .append("result=").append(result);
        return strb.toString();
    }
}

