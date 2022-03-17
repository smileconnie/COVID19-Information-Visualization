package com.xjy.controller;

import com.xjy.bean.Result;
import com.xjy.mapper.Covid19Mapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author ：xjy
 * @Desc ：用来接收前端数据请求的Controller
 */
@Api(tags = "疫情信息")
@RestController//=@Controller+@ResponseBody //表示该类是SpringBoot的一个Controller,且返回的数据为json格式
@RequestMapping("covid19")
public class Covid19Controller {
    @Autowired
    private Covid19Mapper covid19Mapper;

    @ApiOperation("测试接口")
    @GetMapping("test")
    public String test(){
        return  "test successful";
    }
    /**
     * 接收前端请求返回全国疫情汇总数据
     */
    @CrossOrigin(origins = "*",maxAge = 3600)
    @RequestMapping("getNationalData")
    public Result getNationalData(){
        //System.out.println("接收到前端发起的获取json数据的请求，后续需要查询MySQL将数据返回");
        String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        Map<String,Object> data = covid19Mapper.getNationalData(datetime).get(0);
        Result result = Result.success(data);
        return result;
    }
    /**
     * 查询全国各省份累计确诊数据并返回
     */
    @CrossOrigin(origins = "*",maxAge = 3600)
    @RequestMapping("getNationalMapData")
    public Result getNationalMapData(){
        String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        List<Map<String,Object>> data = covid19Mapper.getNationalMapData(datetime);
        Result result = Result.success(data);
        return result;
    }
    /**
     * 查询全国每一天的疫情数据并返回
     */
    @CrossOrigin(origins = "*",maxAge = 3600)
    @RequestMapping("getCovidTimeData")
    public Result getCovidTimeData(){
        List<Map<String,Object>> data = covid19Mapper.getCovidTimeData();
        Result result = Result.success(data);
        return result;
    }
    /**
     * 查询各省份境外输入病例数量
     */
    @CrossOrigin(origins = "*",maxAge = 3600)
    @RequestMapping("getCovidImportData")
    public Result getCovidImportData(){
        String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        List<Map<String, Object>> data = covid19Mapper.getCovidImportData(datetime);
        return  Result.success(data);
    }
    /**
     * 查询各物资使用情况
     */
    @CrossOrigin(origins = "*",maxAge = 3600)
    @RequestMapping("getCovidMaterial")
    public Result getCovidMaterial(){
        List<Map<String, Object>> data = covid19Mapper.getCovidMaterial();
        return Result.success(data);
    }
    /**
     * 查询高风险地区
     */
    @CrossOrigin(origins = "*",maxAge = 3600)
    @RequestMapping("getHighDangerData")
    public Result getHighDangerData(){
        String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        List<Map<String, Object>> data = covid19Mapper.getHighDangerData(datetime);
        return Result.success(data);
    }
    /**
     * 查询中风险地区
     */
    @CrossOrigin(origins = "*",maxAge = 3600)
    @RequestMapping("getMidDangerData")
    public Result getMidDangerData(){
        String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        List<Map<String, Object>> data = covid19Mapper.getMidDangerData(datetime);
        return Result.success(data);
    }
    /**
     * 查询核酸检测点数
     */
    @CrossOrigin(origins = "*",maxAge = 3600)
    @RequestMapping("getDetectOrgData")
    public Result getDetectOrgData(){
        String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        List<Map<String, Object>> data = covid19Mapper.getDetectOrgData(datetime);
        return Result.success(data);
    }
    /**
     * 查询疫苗接种点数
     */
    @CrossOrigin(origins = "*",maxAge = 3600)
    @RequestMapping("getVaccinationOrgData")
    public Result getVaccinationOrgData(){
        String datetime = FastDateFormat.getInstance("yyyy-MM-dd").format(System.currentTimeMillis());
        List<Map<String, Object>> data = covid19Mapper.getVaccinationOrgData(datetime);
        return Result.success(data);
    }
}
