package com.xjy.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ：xjy
 * @Desc ：封装响应给前端的数据的JavaBean(Controller会将该JavaBean转为json,前端要求该json中有data字段)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Object data;
    private Integer code;
    private String message;

    public static Result success(Object data){
        Result result = new Result();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static Result fail(){
        Result result = new Result();
        result.setCode(500);
        result.setMessage("fail");
        result.setData(null);
        return result;
    }
}
