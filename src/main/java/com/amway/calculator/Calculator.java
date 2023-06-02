package com.amway.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Stack;

import com.amway.calculator.constant.CalculatorConstant;
import com.amway.calculator.dto.CalculatorResult;


//计算器类
public class Calculator {

    private BigDecimal oldValue = null; // 前面累计计算值
    private BigDecimal newValue = null; // 新计算值
    private String currentOperator = null; // 当前操作符

    private Stack<BigDecimal> valueStack = new Stack<>(); // 计算数值
    private Stack<String> operatorStack = new Stack<>(); // 操作符
    private Stack<BigDecimal> resultStack = new Stack<>(); // 计算总值

    private void setNewValue(BigDecimal newValue) {
        if (oldValue == null) { // 未计算过,累计总值为第一个输入值
            oldValue = newValue;
        } else {
            this.newValue = newValue;
        }
    }

    private void setCurrentOperator(String currentOperator) {
        this.currentOperator = currentOperator;
    }

    //计算（=号触发）
    private CalculatorResult calculate() {
        //没有按照常规输入，设置默认值
        if (oldValue == null) oldValue = BigDecimal.ZERO;
        if (currentOperator == null) currentOperator = CalculatorConstant.CALCULATOR_ADD;
        if (newValue == null) newValue = BigDecimal.ZERO;

        try {
            return calcTwoValue(oldValue, currentOperator, newValue);
        } catch (Exception e) {
            CalculatorResult CalculatorResult = new CalculatorResult(CalculatorConstant.RESPONSE_MESSAGE_EXCEPTION, e.getMessage());
            return CalculatorResult;
        }
    }

    //undo操作
    private CalculatorResult undo() {
        BigDecimal undoPreValue = BigDecimal.ZERO;
        BigDecimal value1 = BigDecimal.ZERO;
        BigDecimal value2 = BigDecimal.ZERO;
        if (!resultStack.isEmpty() && resultStack.size() > 1) {
            //取第2个结果值
            resultStack.pop();
            undoPreValue = resultStack.peek();
            //取第2个操作符
            operatorStack.pop();
            currentOperator = operatorStack.peek();
            //取第3、4个计算值后重新放回
            valueStack.pop();
            valueStack.pop();
            value2 = valueStack.pop();
            value1 = valueStack.pop();
            valueStack.add(value1);
            valueStack.add(value2);
        }

        oldValue = undoPreValue;

        CalculatorResult CalculatorResult = new CalculatorResult(CalculatorConstant.RESPONSE_CODE_SUCCESS, CalculatorConstant.RESPONSE_MESSAGE_SUCCESS);
        CalculatorResult.setResult(oldValue);
        CalculatorResult.setOperator(currentOperator);
        CalculatorResult.setValueFirst(value1);
        CalculatorResult.setValueSecond(value2);
        return CalculatorResult;
    }

    //redo操作
    private CalculatorResult redo() {
        if (valueStack.isEmpty() || operatorStack.isEmpty()) {
            System.out.println("valueStack或operatorStack为空！");
            CalculatorResult CalculatorResult = new CalculatorResult(CalculatorConstant.RESPONSE_MESSAGE_EXCEPTION, "无法redo操作");
            return CalculatorResult;
        }

        BigDecimal redoOldValue = resultStack.peek();
        String redoOperator = operatorStack.peek();
        try {
            return calcTwoValue(redoOldValue, redoOperator, newValue);
        } catch (Exception e) {
            CalculatorResult CalculatorResult = new CalculatorResult(CalculatorConstant.RESPONSE_MESSAGE_EXCEPTION, e.getMessage());
            return CalculatorResult;
        }
    }

    /**
     * 两数计算
     *
     * @param preValue    前面已累计值
     * @param curOperator 当前操作
     * @param newValue    新输入值
     * @return 计算结果
     */
    private CalculatorResult calcTwoValue(BigDecimal preValue, String curOperator, BigDecimal newValue) throws Exception {
        BigDecimal res = BigDecimal.ZERO;
        if (preValue == null) {
            preValue = this.oldValue;
        }
        curOperator = curOperator == null ? CalculatorConstant.CALCULATOR_ADD : curOperator;
        //针对加减乘除的不同处理
        switch (curOperator) {
            case CalculatorConstant.CALCULATOR_ADD:
                res = preValue.add(newValue);
                break;
            case CalculatorConstant.CALCULATOR_SUBTRACT:
                res = preValue.subtract(newValue).setScale(CalculatorConstant.CALCULATOR_SCALE, RoundingMode.HALF_UP);
                break;
            case CalculatorConstant.CALCULATOR_MULTIPLY:
                res = preValue.multiply(newValue).setScale(CalculatorConstant.CALCULATOR_SCALE, RoundingMode.HALF_UP);
                break;
            case CalculatorConstant.CALCULATOR_DIVIDE:
                if (BigDecimal.ZERO.equals(newValue)) {
                    throw new Exception("操作不规范，被除数不能为0");
                }
                res = preValue.divide(newValue, RoundingMode.HALF_UP);
                break;
        }

        this.oldValue = res;
        resultStack.add(res);
        operatorStack.add(curOperator);
        valueStack.add(preValue);
        valueStack.add(newValue);

        //响应结果填充
        CalculatorResult CalculatorResult = new CalculatorResult(CalculatorConstant.RESPONSE_CODE_SUCCESS, CalculatorConstant.RESPONSE_MESSAGE_SUCCESS);
        CalculatorResult.setResult(res);
        CalculatorResult.setOperator(curOperator);
        CalculatorResult.setValueFirst(preValue);
        CalculatorResult.setValueSecond(newValue);
        return CalculatorResult;
    }

    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        //直接等号
        CalculatorResult CalculatorResult = calculator.calculate();
        System.out.println("无输入直接等号:" + CalculatorResult.toString());

        //输入一个计算值
        calculator = new Calculator();
        calculator.setNewValue(new BigDecimal(10));
        CalculatorResult = calculator.calculate();
        System.out.println("输入一个计算值:" + CalculatorResult.toString());

        //输入计算值、计算符
        calculator = new Calculator();
        calculator.setNewValue(new BigDecimal(10));
        calculator.setNewValue(new BigDecimal(5));
        calculator.setCurrentOperator("-");
        CalculatorResult = calculator.calculate();
        System.out.println("输入计算值、计算符:" + CalculatorResult.toString());

        //redo +
        calculator = new Calculator();
        calculator.setNewValue(new BigDecimal(5));
        calculator.setNewValue(new BigDecimal(5));
        calculator.setCurrentOperator("+");
        CalculatorResult = calculator.calculate();
        System.out.println("redo + calculate:" + CalculatorResult.toString());
        for (int i = 0; i < 2; i++) {
            CalculatorResult = calculator.redo();
            System.out.println("redo +:" + CalculatorResult.toString());
        }

        //redo -
        calculator = new Calculator();
        calculator.setNewValue(new BigDecimal(5));
        calculator.setNewValue(new BigDecimal(5));
        calculator.setCurrentOperator("-");
        CalculatorResult = calculator.calculate();
        System.out.println("redo - calculate:" + CalculatorResult.toString());
        for (int i = 0; i < 2; i++) {
            CalculatorResult = calculator.redo();
            System.out.println("redo -:" + CalculatorResult.toString());
        }

        //undo
        calculator = new Calculator();
        calculator.setNewValue(new BigDecimal(5));
        calculator.setNewValue(new BigDecimal(5));
        calculator.setCurrentOperator("+");
        CalculatorResult = calculator.calculate();
        System.out.println("undo +:" + CalculatorResult.toString());

        calculator.setNewValue(new BigDecimal(5));
        calculator.setCurrentOperator("-");
        CalculatorResult = calculator.calculate();
        System.out.println("undo -:" + CalculatorResult.toString());

        calculator.setNewValue(new BigDecimal(2));
        calculator.setCurrentOperator("-");
        CalculatorResult = calculator.calculate();
        System.out.println("undo -:" + CalculatorResult.toString());

        for (int i = 0; i < 3; i++) {
            CalculatorResult = calculator.undo();
            System.out.println("undo:" + CalculatorResult.toString());
        }
    }
}