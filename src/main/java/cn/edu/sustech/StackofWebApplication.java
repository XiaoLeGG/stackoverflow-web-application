package cn.edu.sustech;

import cn.edu.sustech.service.ApiService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
@MapperScan("cn.edu.sustech.mapper")
public class StackofWebApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(StackofWebApplication.class, args);
	}
	
}
