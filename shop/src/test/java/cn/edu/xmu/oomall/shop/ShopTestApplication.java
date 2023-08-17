//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop;

import cn.edu.xmu.javaee.core.jpa.SelectiveUpdateJpaRepositoryImpl;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@ComponentScan(basePackages = {"cn.edu.xmu.javaee.core",
        "cn.edu.xmu.oomall.shop"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {RocketMQTransactionListener.class,SpringBootApplication.class }),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {RocketMQTemplate.class}),
        })
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories(value = "cn.edu.xmu.javaee.core.jpa", repositoryBaseClass = SelectiveUpdateJpaRepositoryImpl.class, basePackages = "cn.edu.xmu.oomall.shop.mapper")
@EnableMongoRepositories(basePackages = "cn.edu.xmu.oomall.shop.mapper")
@EnableFeignClients
@EnableDiscoveryClient
public class ShopTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
}