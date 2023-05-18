package cn.edu.sustech.service;

import cn.edu.sustech.entity.Answer;
import cn.edu.sustech.entity.Question;
import cn.edu.sustech.mapper.AnswerMapper;
import cn.edu.sustech.mapper.QuestionMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AnswerService {
	
	@Autowired
	private AnswerMapper mapper;
	public AnswerMapper getMapper() {
		return mapper;
	}
	public List<Answer> queryWithAcceptedAnswerByQuestionID(int questionID) {
		QueryWrapper<Answer> wrapper = new QueryWrapper<Answer>();
		wrapper
			.eq("question_id", questionID)
			.eq("is_accepted", true);
		return mapper.selectList(wrapper);
	}
	public List<Answer> queryAllAnswerByQuestionID(int questionID) {
		QueryWrapper<Answer> wrapper = new QueryWrapper<Answer>();
		wrapper
			.eq("question_id", questionID);
		return mapper.selectList(wrapper);
	}

	public List<Answer> queryAnswerByDate(Date from, Date end) {
		QueryWrapper<Answer> wrapper = new QueryWrapper<Answer>();
		wrapper
			.between("creation_date", from, end);
		return mapper.selectList(wrapper);
	}

	public List<Answer> queryAnswerByAnswerID(int answerID) {
		QueryWrapper<Answer> wrapper = new QueryWrapper<Answer>();
		wrapper
			.eq("answer_id", answerID);
		return mapper.selectList(wrapper);
	}

}
