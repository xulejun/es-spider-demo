package com.xlj.esspider;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.support.CronTrigger;

/**
 * @author xlj
 * @date 2021/3/15
 */
//@SpringBootTest
public class CronTest {
    @Test
    public void cronTest(){
        String cron = "1 * * * * *";
        CronTrigger cronTrigger2 = new CronTrigger(cron);
        CronTrigger cronTrigger1 = new CronTrigger(cron);
        System.out.println(cronTrigger1.hashCode());
        System.out.println(cronTrigger2.hashCode());
    }
}
