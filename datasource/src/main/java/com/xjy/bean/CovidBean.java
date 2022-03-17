package com.xjy.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ：xjy
 * @Desc ：用来封装各省市疫情数据的JavaBean
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CovidBean {
    private String provinceName;//省份名称
    private String provinceShortName;//省份短名
    private Integer currentConfirmedCount;//当前确诊人数
    private Integer confirmedCount;//累计确诊人数
    private Integer suspectedCount;//疑似病例人数
    private Integer curedCount;//治愈人数
    private Integer deadCount;//死亡人数
    //private String comment;
    private Integer locationId;//位置id
    private Integer parentLocationId;//父位置id
    private String statisticsData;//每一天的统计数据
    private Integer highDangerCount;//高风险地区数
    private Integer midDangerCount;//中风险地区数
    private Integer detectOrgCount;//核酸检测点数
    private Integer vaccinationOrgCount;//疫苗接种点数
    private String cities;//下属城市
    //private String dangerAreas;
    private String cityName;//城市名称
    //private String notShowCurrentConfirmedCount;
    //private String currentConfirmedCountStr;
    private String dateTime;//爬取时间
}