package com.xjy.generator;

import com.alibaba.fastjson.JSON;
import com.xjy.bean.MaterialBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @Author ：xjy
 * @Desc ：使用程序模拟生成疫情数据
 */
@Component
public class Covid19DataGenerator {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //@Scheduled(initialDelay = 1000, fixedDelay = 1000 * 10)
    public void generator() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            MaterialBean materialBean = new MaterialBean(materialName[random.nextInt(materialName.length)], materialFrom[random.nextInt(materialFrom.length)], random.nextInt(1000));
            System.out.println(materialBean);
            //将生成的疫情物资数据转换为jsonStr再发送到Kafka
            String jsonStr = JSON.toJSONString(materialBean);
            kafkaTemplate.send("covid19_material",random.nextInt(3),jsonStr);
        }
    }

    //物资名称
    private String[] materialName = new String[]{"N95口罩/个", "医用外科口罩/个", "84消毒液/瓶", "电子体温计/个", "一次性手套/副", "护目镜/副", "医用防护服/套"};

    //物资来源
    private String[] materialFrom = new String[]{"采购", "下拨", "捐赠", "消耗", "需求"};

}
