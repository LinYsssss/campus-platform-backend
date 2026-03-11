package com.campus.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class CampusSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusSystemApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  一体化智慧校园系统启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
