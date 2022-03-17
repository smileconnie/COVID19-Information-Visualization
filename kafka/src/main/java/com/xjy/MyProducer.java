package com.xjy;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

/**
 * @Author ：xjy
 * @Desc ：创建生产者类
 */

public class MyProducer {
    public static void main(String[] args) {
        Properties prop = new Properties();
        //kafka连接地址
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.1.101:9092");
        //用于实现Serializer接口的密钥的串行器类。
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //生产者数据安全
        prop.put(ProducerConfig.ACKS_CONFIG,"-1");

        //创建生产者对象
        KafkaProducer<String,String> producer=new KafkaProducer<String, String>(prop);
        //生成10条数据
        for (int i = 0; i <10; i++) {
            //创建消息对象
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("test1", "hello world" + i);
            //调用生产者消息发送方法
            producer.send(producerRecord);
            try {
                //每条消息间隔100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
        System.out.println("game over!!!");
    }
}

