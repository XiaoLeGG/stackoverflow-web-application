package cn.edu.sustech.controller;

import cn.edu.sustech.entity.Answer;
import cn.edu.sustech.entity.Question;
import cn.edu.sustech.service.AnswerService;
import cn.edu.sustech.service.QuestionService;

import java.time.Duration;
import java.time.Period;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/question")
public class QuestionController {
	
	@Autowired
	private QuestionService questionService;
	@Autowired
	private AnswerService answerService;
	
	@GetMapping("/no-answer/total")
	public int noAnswerQuery(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
		@RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		return questionService.noAnswerQuery(from, end).size();
	}
	@GetMapping("/with-answer/total")
	public int totalAnswerQuery(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
		@RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		return questionService.totalAnswerQuery(from, end).size();
	}
	@GetMapping("/no-answer/percentage")
	public double noAnswerPercentageQuery(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
		@RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Question> noAnswer = questionService.noAnswerQuery(from, end);
		List<Question> total = questionService.totalAnswerQuery(from, end);
		return (double)noAnswer.size() / total.size();
	}
	@GetMapping("/with-answer/percentage")
	public double totalAnswerPercentageQuery(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
		@RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Question> noAnswer = questionService.noAnswerQuery(from, end);
		List<Question> total = questionService.totalAnswerQuery(from, end);
		return (double)(total.size() - noAnswer.size()) / total.size();
	}
	@GetMapping("/answer/average")
	public double answerAverageQuery(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
		@RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Question> total = questionService.totalAnswerQuery(from, end);
		int sum = 0;
		for (Question question : total) {
			sum += question.getAnswerCount();
		}
		return (double)sum / total.size();
	}
	@GetMapping("/answer/max")
	public int answerMaxQuery(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
		@RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Question> total = questionService.totalAnswerQuery(from, end);
		int max = 0;
		for (Question question : total) {
			max = Math.max(max, question.getAnswerCount());
		}
		return max;
	}
	@GetMapping("/answer/distribution")
	public Map<Integer, Integer> answerDistributionQuery(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
													   @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Question> total = questionService.totalAnswerQuery(from, end);
		Map<Integer, Integer> distribution = new HashMap<Integer, Integer>();
		for (Question question : total) {
			int answerCount = question.getAnswerCount();
			if (distribution.containsKey(answerCount)) {
				distribution.put(answerCount, distribution.get(answerCount) + 1);
			} else {
				distribution.put(answerCount, 1);
			}
		}
		return distribution;
	}
	@GetMapping("/with-accepted-answer/total")
	public int withAcceptedAnswerQuery(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
		@RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Question> withAcceptedAnswer = questionService.withAnswerQuery(from, end);
		for (int i = 0; i < withAcceptedAnswer.size(); i++) {
			List<Answer> answers = answerService.queryWithAcceptedAnswerByQuestionID(withAcceptedAnswer.get(i).getQuestionId());
			if (answers.size() == 0) {
				withAcceptedAnswer.remove(i);
				i--;
			}
		}
		return withAcceptedAnswer.size();
	}
	@GetMapping("/with-accepted-answer/percentage")
	public double withAcceptedAnswerPercentageQuery(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
		@RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Question> withAcceptedAnswer = questionService.withAnswerQuery(from, end);
		for (int i = 0; i < withAcceptedAnswer.size(); i++) {
			List<Answer> answers = answerService.queryWithAcceptedAnswerByQuestionID(withAcceptedAnswer.get(i).getQuestionId());
			if (answers.size() == 0) {
				withAcceptedAnswer.remove(i);
				i--;
			}
		}
		return (double)withAcceptedAnswer.size() / questionService.totalAnswerQuery(from, end).size();
	}
	@GetMapping("/with-accepted-answer/resolution-distribution")
	public Map<Integer, Duration> resolutionDistributionQuery(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
															@RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Question> withAcceptedAnswer = questionService.withAnswerQuery(from, end);
		Map<Integer, Duration> distribution = new HashMap<Integer, Duration>();
		for (int i = 0; i < withAcceptedAnswer.size(); i++) {
			List<Answer> answers = answerService.queryWithAcceptedAnswerByQuestionID(withAcceptedAnswer.get(i).getQuestionId());
			if (answers.size() == 0) {
				withAcceptedAnswer.remove(i);
				i--;
			} else {
				long timeDifference = answers.get(0).getCreationDate().getTime() - withAcceptedAnswer.get(i).getCreationDate().getTime();
				Duration duration = Duration.ofMillis(timeDifference);
				distribution.put(withAcceptedAnswer.get(i).getQuestionId(), duration);
			}
		}
		return distribution;
	}
	@GetMapping("/with-accepted-answer/better-answer/total")
	public int withAcceptedAnswerBetterAnswerQuery(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
		@RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Question> withAcceptedAnswer = questionService.withAnswerQuery(from, end);
		int count = 0;
		for (int i = 0; i < withAcceptedAnswer.size(); i++) {
			List<Answer> answers = answerService.queryAllAnswerByQuestionID(withAcceptedAnswer.get(i).getQuestionId());
			if (answers.size() == 0) {
				withAcceptedAnswer.remove(i);
				i--;
			} else {
				for (int j = 1; j < answers.size(); j++) {
					if (answers.get(j).getScore() > answers.get(0).getScore()) {
						count++;
						break;
					}
				}
			}
		}
		return count;
	}
	@GetMapping("/with-accepted-answer/better-answer/percentage")
	public double withAcceptedAnswerBetterAnswerPercentageQuery(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
																@RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Question> withAcceptedAnswer = questionService.withAnswerQuery(from, end);
		int count = 0;
		for (int i = 0; i < withAcceptedAnswer.size(); i++) {
			List<Answer> answers = answerService.queryAllAnswerByQuestionID(withAcceptedAnswer.get(i).getQuestionId());
			if (answers.size() == 0) {
				withAcceptedAnswer.remove(i);
				i--;
			} else {
				for (int j = 1; j < answers.size(); j++) {
					if (answers.get(j).getScore() > answers.get(0).getScore()) {
						count++;
						break;
					}
				}
			}
		}
		return (double) count / questionService.totalAnswerQuery(from, end).size();
	}
}
