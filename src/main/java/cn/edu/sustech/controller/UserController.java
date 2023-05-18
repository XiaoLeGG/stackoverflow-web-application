package cn.edu.sustech.controller;

import cn.edu.sustech.entity.Answer;
import cn.edu.sustech.entity.Comment;
import cn.edu.sustech.entity.Question;
import cn.edu.sustech.entity.User;
import cn.edu.sustech.service.AnswerService;
import cn.edu.sustech.service.CommentService;
import cn.edu.sustech.service.QuestionService;
import cn.edu.sustech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private AnswerService answerService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private QuestionService questionService;

	@GetMapping("/post-answer-distribution")
	public Map<Integer, Integer> getPostAnswerDistribution(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
														  @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Answer> answers = answerService.queryAnswerByDate(from, end);
		Map<String, HashSet<String>> map = new HashMap<>();
		for (Answer answer : answers) {
			if (map.containsKey(String.valueOf(answer.getQuestionId()))) {
				map.get(String.valueOf(answer.getQuestionId())).add(String.valueOf(answer.getAccountId()));
			} else {
				HashSet<String> set = new HashSet<>();
				set.add(String.valueOf(answer.getAccountId()));
				map.put(String.valueOf(answer.getQuestionId()), set);
			}
		}
		Map<Integer, Integer> result = new HashMap<>();
		for (String key : map.keySet()) {
			if (result.containsKey(map.get(key).size())) {
				result.put(map.get(key).size(), result.get(map.get(key).size()) + 1);
			} else {
				result.put(map.get(key).size(), 1);
			}
		}
		return result;
	}


	@GetMapping("/post-comment-distribution")
	public Map<Integer, Integer> getPostCommentDistribution(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
														   @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Comment> comments = commentService.queryCommentByDate(from, end);
		Map<String, HashSet<String>> map = new HashMap<>();
		for (Comment comment : comments) {
			List<Answer> answers = answerService.queryAnswerByAnswerID(comment.getPostId());
			if (answers != null) {
				for (Answer answer : answers) {
					if (map.containsKey(String.valueOf(answer.getQuestionId()))) {
						map.get(String.valueOf(answer.getQuestionId())).add(String.valueOf(comment.getAccountId()));
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(String.valueOf(comment.getAccountId()));
						map.put(String.valueOf(answer.getQuestionId()), set);
					}
				}
			} else {
				if (map.containsKey(String.valueOf(comment.getPostId()))) {
					map.get(String.valueOf(comment.getPostId())).add(String.valueOf(comment.getAccountId()));
				} else {
					HashSet<String> set = new HashSet<>();
					set.add(String.valueOf(comment.getAccountId()));
					map.put(String.valueOf(comment.getPostId()), set);
				}
			}
		}
		Map<Integer, Integer> result = new HashMap<>();
		for (String key : map.keySet()) {
			if (result.containsKey(map.get(key).size())) {
				result.put(map.get(key).size(), result.get(map.get(key).size()) + 1);
			} else {
				result.put(map.get(key).size(), 1);
			}
		}
		return result;
	}
	@GetMapping("/participation-distribution")
	public Map<Integer, Integer> getParticipationDistribution(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
															@RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Answer> answers = answerService.queryAnswerByDate(from, end);
		Map<String, HashSet<String>> map = new HashMap<>();
		for (Answer answer : answers) {
			if (map.containsKey(String.valueOf(answer.getQuestionId()))) {
				map.get(String.valueOf(answer.getQuestionId())).add(String.valueOf(answer.getAccountId()));
			} else {
				HashSet<String> set = new HashSet<>();
				set.add(String.valueOf(answer.getAccountId()));
				map.put(String.valueOf(answer.getQuestionId()), set);
			}
		}
		List<Comment> comments = commentService.queryCommentByDate(from, end);
		for (Comment comment : comments) {
			answers = answerService.queryAnswerByAnswerID(comment.getPostId());
			if (answers != null) {
				for (Answer answer : answers) {
					if (map.containsKey(String.valueOf(answer.getQuestionId()))) {
						map.get(String.valueOf(answer.getQuestionId())).add(String.valueOf(comment.getAccountId()));
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(String.valueOf(comment.getAccountId()));
						map.put(String.valueOf(answer.getQuestionId()), set);
					}
				}
			} else {
				if (map.containsKey(String.valueOf(comment.getPostId()))) {
					map.get(String.valueOf(comment.getPostId())).add(String.valueOf(comment.getAccountId()));
				} else {
					HashSet<String> set = new HashSet<>();
					set.add(String.valueOf(comment.getAccountId()));
					map.put(String.valueOf(comment.getPostId()), set);
				}
			}
		}
		Map<Integer, Integer> result = new HashMap<>();
		for (String key : map.keySet()) {
			if (result.containsKey(map.get(key).size())) {
				result.put(map.get(key).size(), result.get(map.get(key).size()) + 1);
			} else {
				result.put(map.get(key).size(), 1);
			}
		}
		return result;
	}
	@GetMapping("/activity")
	public Map<Integer, Integer> getUserActivity(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
												 @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		Map<Integer, Integer> map = new HashMap<>();
		List<Question> questions = questionService.allQuery(from, end);
		for (Question question: questions) {
			if (map.containsKey(question.getAccountId())) {
				map.put(question.getAccountId(), map.get(question.getAccountId()) + 1);
			} else {
				map.put(question.getAccountId(), 1);
			}
		}
		List<Answer> answers = answerService.queryAnswerByDate(from, end);
		for (Answer answer: answers) {
			if (map.containsKey(answer.getAccountId())) {
				map.put(answer.getAccountId(), map.get(answer.getAccountId()) + 1);
			} else {
				map.put(answer.getAccountId(), 1);
			}
		}
		List<Comment> comments = commentService.queryCommentByDate(from, end);
		for (Comment comment: comments) {
			if (map.containsKey(comment.getAccountId())) {
				map.put(comment.getAccountId(), map.get(comment.getAccountId()) + 1);
			} else {
				map.put(comment.getAccountId(), 1);
			}
		}
		return map;
	}
}
