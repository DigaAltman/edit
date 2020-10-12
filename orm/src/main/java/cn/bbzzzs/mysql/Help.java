package cn.bbzzzs.mysql;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * mysql优化
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class Help {
        public static void main(String[] args) {
        SpringApplication.run(Help.class, args);
    }
}
