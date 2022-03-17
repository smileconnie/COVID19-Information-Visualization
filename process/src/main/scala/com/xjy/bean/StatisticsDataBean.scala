package com.xjy.bean
/**
 * @Author ：xjy
 * @Desc ：用来封装省份每一天的统计数据
 */
case class StatisticsDataBean(
                             var dateId:String,
                             var provinceShortName:String,
                             var locationId:Int,
                             var confirmedCount:Int,
                             var currentConfirmedCount:Int,
                             var currentConfirmedIncr:Int,
                             var confirmedIncr:Int,
                             var curedCount: Int,
                             var curedIncr: Int,
                             var suspectedCount: Int,
                             var suspectedCountIncr: Int,
                             var deadCount: Int,
                             var deadIncr: Int
                             )
