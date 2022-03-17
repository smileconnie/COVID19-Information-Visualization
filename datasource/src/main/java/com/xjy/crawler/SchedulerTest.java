package com.xjy.crawler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author ：xjy
 * @Desc ：演示定时任务
 */

//@Component//表示将该类交给Spring管理，作为Spring容器中的对象
public class SchedulerTest {
    public static void main(String[] args){
        //演示JDK中自带的定时任务API
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("每隔1s执行一次");
            }
        },1000,1000);
    }

    //演示SpringBoot中提供的定时任务工具
    //@Scheduled(initialDelay = 1000,fixedDelay = 1000)
    //@Scheduled(cron = "0/1 * * * * ?")//每隔1s执行
    //@Scheduled(cron = "0 0 8 * * ?")//每天的8点定时执行
    public void scheduler(){
        //System.out.println("@Scheduled每隔1s执行一次");
        System.out.println("@Scheduled-cron每隔1s执行一次");
    }
}
