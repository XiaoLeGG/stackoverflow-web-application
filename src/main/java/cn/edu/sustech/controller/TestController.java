package cn.edu.sustech.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
	
	@GetMapping("/hello")
	public String hello() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 100; ++i) {
			builder.append("a\n");
		}
		return builder.toString() + "hello";
	}
	
}
