package cn.edu.sustech.service;

import cn.edu.sustech.mapper.AnswerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {
	
	@Autowired
	private AnswerMapper mapper;
	
}
