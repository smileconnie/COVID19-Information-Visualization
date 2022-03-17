package com.xjy.bean

case class CovidBean(
                      provinceName: String, //省份名称
                      provinceShortName: String, //省份短名
                      currentConfirmedCount: Int, //当前确诊人数
                      confirmedCount: Int, //累计确诊人数
                      suspectedCount: Int, //疑似病例人数
                      curedCount: Int, //治愈人数
                      deadCount: Int, //死亡人数
                      //comment;
                      locationId: Int, //位置id
                      parentLocationId: Int, //父位置id
                      statisticsData: String, //每一天的统计数据
                      highDangerCount: Int, //高风险地区数
                      midDangerCount: Int, //中风险地区数
                      detectOrgCount: Int, //核酸检测点数
                      vaccinationOrgCount: Int, //疫苗接种点数
                      cities: String, //下属城市
                      //dangerAreas;
                      cityName: String, //城市名称
                      //notShowCurrentConfirmedCount;
                      //currentConfirmedCountStr;
                      dateTime: String //爬取时间
                    )
