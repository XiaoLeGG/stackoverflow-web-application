package cn.edu.sustech.controller;

import cn.edu.sustech.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/api")
public class ApiController {

	@Autowired
	private ApiService apiService;

}
