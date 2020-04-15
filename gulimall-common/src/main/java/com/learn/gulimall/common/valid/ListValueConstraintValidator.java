package com.learn.gulimall.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * packageName = com.learn.gulimall.common.valid
 * author = Casey
 * Data = 2020/4/15 6:17 下午
 **/
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {
    private Set<Integer> set = new HashSet<>();

    //初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] values = constraintAnnotation.values();
        for (int value :
                values) {
            set.add(value);
        }

    }

    /**
     * 判断是否成功
     *
     * @param integer                    //需要校验的值
     * @param constraintValidatorContext // 校验的环境校验
     * @return true false
     */
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(integer);
    }
}
