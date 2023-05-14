package cn.edu.sustech.service;

import cn.edu.sustech.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

	@Autowired
	private CommentMapper mapper;

}
