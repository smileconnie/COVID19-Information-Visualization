package com.xjy.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ：xjy
 * @Desc ：用来封装防疫物资的JavaBean
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialBean {
    private String name;//物资名称
    private String from;//物资来源
    private Integer num;//物资数量
}
