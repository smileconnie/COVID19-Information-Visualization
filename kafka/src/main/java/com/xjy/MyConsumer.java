package com.xjy;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.Collections;
import java.util.Properties;

/**
 * @Author ：xjy
 * @Desc ：创建消费者类
 */

public class MyConsumer {
    public static void main(String[] args) {
        Properties prop=new Properties();
        //kafka连接地址
        prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.1.101:9092");
        prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //会话响应的时间，超过这个时间kafka可以选择放弃消费或者消费下一条消息
        prop.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,"30000");
        //false:非自动提交偏移量
        prop.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false");
        //自动提交偏移量周期
        prop.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"1000");
        //earliest：拉取最早的数据
        //latest：拉取最新的数据
        //none：报错
        prop.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        //一个消费者组G1里只有一个消费者
        prop.put(ConsumerConfig.GROUP_ID_CONFIG,"G1");

        //创建kafka消费者对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(prop);
        //设置自动分配topic与消费者对象
        consumer.subscribe(Collections.singleton("test1"));

        while (true){
            //消费数据,一次10条
            ConsumerRecords<String, String> poll = consumer.poll(10);
            //遍历输出
            for (ConsumerRecord<String, String> record : poll) {
                System.out.println(record.offset()+"\t"+record.key()+"\t"+record.value());
            }
        }
    }
}

