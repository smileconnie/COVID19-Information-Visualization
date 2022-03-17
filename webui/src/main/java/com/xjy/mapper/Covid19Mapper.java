package com.xjy.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Author ：xjy
 * @Desc ：TODO
 */
@Mapper
public interface Covid19Mapper {
    /**
     * 查询全国疫情汇总数据
     * @param datetime
     * @return
     */
    @Select("select `datetime`, `currentConfirmedCount`, `confirmedCount`, `suspectedCount`, `curedCount`, `deadCount` from covid19_1 where datetime = #{datetime}")
    List<Map<String,Object>> getNationalData(String datetime);

    /**
     * 查询全国各省份疫情累计确诊数据
     * @param datetime
     * @return 省份名称，累计确诊数
     */
    @Select("select `provinceShortName` as `name`, `confirmedCount` as `value` from covid19_2 where datetime = #{datetime}")
    List<Map<String, Object>> getNationalMapData(String datetime);

    /**
     * 查询全国每一天的疫情数据
     * @return
     */
    @Select("select `dateId`,`confirmedIncr` as `新增确诊`,`confirmedCount` as `累计确诊`,`suspectedCount` as `疑似病例`,`curedCount` as `累计治愈`,`deadCount` as `累计死亡` from covid19_3")
    List<Map<String, Object>> getCovidTimeData();

    /**
     * 查询全国各省份境外输入病例数量
     * @param datetime
     * @return
     */
    @Select("select `provinceShortName` as `name`,`confirmedCount` as `value` from covid19_4 where datetime = #{datetime} order by `value` desc limit 10")
    List<Map<String, Object>> getCovidImportData(String datetime);

    /**
     * 查询防疫物资使用情况
     * @return
     */
    @Select("select `name`, `purchase` as `采购`, `allocation` as `下拨`, `donation` as `捐赠`, `consumption` as `消耗`, `demand` as `需求`, `stock` as `库存` from covid19_material order by `库存` desc limit 4")
    List<Map<String, Object>> getCovidMaterial();

    /**
     * 查询各省市高风险地区数
     * @return datetime
     */
    @Select("select `provinceShortName` as `name`,`highDangerCount` as `value` from covid19_6 where datetime = #{datetime} order by `value` desc limit 3")
    List<Map<String, Object>> getHighDangerData(String datetime);

    /**
     * 查询各省市中风险地区数
     * @return datetime
     */
    @Select("select `provinceShortName` as `name`,`midDangerCount` as `value` from covid19_6 where datetime = #{datetime} order by `value` desc limit 10")
    List<Map<String, Object>> getMidDangerData(String datetime);

    /**
     * 查询各省市核酸检测点数
     * @return datetime
     */
    @Select("select `provinceShortName` as `name`,`detectOrgCount` as `value` from covid19_7 where datetime = #{datetime} order by `value` desc limit 5")
    List<Map<String, Object>> getDetectOrgData(String datetime);

    /**
     * 查询各省市疫苗接种点数
     * @return datetime
     */
    @Select("select `provinceShortName` as `name`,`vaccinationOrgCount` as `value` from covid19_7 where datetime = #{datetime}")
    List<Map<String, Object>> getVaccinationOrgData(String datetime);
}
