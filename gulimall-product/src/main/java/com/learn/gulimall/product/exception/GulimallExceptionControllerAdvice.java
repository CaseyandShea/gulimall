package com.learn.gulimall.product.exception;

import com.learn.gulimall.common.exception.BizCodeEnum;
import com.learn.gulimall.common.utils.R;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName = com.learn.gulimall.product.exception
 * author = Casey
 * Data = 2020/4/15 4:10 下午
 **/

@ControllerAdvice(basePackages = "com.learn.gulimall.product.controller")
@RestControllerAdvice(basePackages = "com.learn.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {

    /**
     * 统一处理MethodArgumentNotValidException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        BindingResult result = e.getBindingResult();
        Map<String, String> map = new HashMap<>();
        if (result.hasErrors()) {
            result.getFieldErrors().forEach(item -> {
                String message = item.getDefaultMessage();
                String field = item.getField();
                map.put(field, message);
            });
        }
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg()).put("errorData", map);

    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable) {
        throwable.printStackTrace();
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(), BizCodeEnum.UNKNOW_EXCEPTION.getMsg());

    }


}
