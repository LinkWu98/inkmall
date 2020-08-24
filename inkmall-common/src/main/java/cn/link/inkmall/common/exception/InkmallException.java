package cn.link.inkmall.common.exception;

import lombok.NoArgsConstructor;

/**
 * @Author: Link
 * @Date: 2020/6/1 10:12
 * @Version 1.0
 */
@NoArgsConstructor
public class InkmallException extends RuntimeException {

    public InkmallException(String msg) {
        super(msg);
    }

}
