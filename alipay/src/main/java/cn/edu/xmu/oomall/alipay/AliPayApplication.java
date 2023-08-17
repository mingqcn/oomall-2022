package cn.edu.xmu.oomall.alipay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall.core", "cn.edu.xmu.oomall.alipay"})
@EnableConfigurationProperties
@MapperScan("cn.edu.xmu.oomall.alipay.mapper")
@EnableFeignClients(basePackages = "cn.edu.xmu.oomall.alipay.microservice")
public class AliPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AliPayApplication.class, args);
    }

}

