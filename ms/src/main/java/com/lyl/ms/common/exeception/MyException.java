package com.lyl.ms.common.exeception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 罗亚龙
 * @date 2022/1/16 15:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MyException extends RuntimeException{

    private String msg;

    public MyException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
