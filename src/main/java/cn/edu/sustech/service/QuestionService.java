package cn.edu.sustech.service;

import cn.edu.sustech.entity.Question;
import cn.edu.sustech.mapper.QuestionMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
	
	@Autowired
	private QuestionMapper mapper;
	
	public List<Question> noAnswerQuery(Date from, Date end) {
		QueryWrapper<Question> wrapper = new QueryWrapper<Question>();
		wrapper
			.ge("creation_date", from)
			.le("creation_date", end);
		return mapper.selectList(wrapper);
	}
	
	
	
	
}
