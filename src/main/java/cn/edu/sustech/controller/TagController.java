package cn.edu.sustech.controller;

import cn.edu.sustech.entity.Question;
import cn.edu.sustech.entity.Tag;
import cn.edu.sustech.entity.TagConnect;
import cn.edu.sustech.service.QuestionService;
import cn.edu.sustech.service.TagService;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;

@RestController
@RequestMapping("/api/tag")
public class TagController {
	
	@Autowired
	private TagService tagService;
	@Autowired
	private QuestionService questionService;
	
	@GetMapping("/single-tag/all/name")
	public List<Tag> getAllTags() {
		return tagService.getTags();
	}

//	@GetMapping("/group-tag/all/name")
//	public List<String> getAllGroupTags() {
//		List<Map<String, Object>> tagGroups = tagService.getTagGroups();
//		Set<String> set = new HashSet<>();
//		for (Map<String, Object> tagGroup : tagGroups) {
//			set.add((String) tagGroup.get("tag_group"));
//		}
//		return new ArrayList<>(set);
//	}


	@GetMapping("/single-tag/all/count")
	public List<Map<String, Object>> getAllTagCounts() {
		return tagService.getTagCounts();
	}

//	@GetMapping("/group-tag/all/count")
//	public Map<String, Integer> getAllTagGroupCounts() {
//		List<Map<String, Object>> tagGroups = tagService.getTagGroups();
//		Map<String, Integer> map = new HashMap<>();
//		for (Map<String, Object> tagGroup : tagGroups) {
//			if (map.containsKey(tagGroup.get("tag_group"))) {
//				map.put((String) tagGroup.get("tag_group"), map.get(tagGroup.get("tag_group")) + 1);
//			} else {
//				map.put((String) tagGroup.get("tag_group"), 1);
//			}
//		}
//		return map;
//	}

	@GetMapping("/single-tag/count")
	public Map<String, Integer> getSingeTagCount(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
													  @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Question> total = questionService.allQuery(from, end);
		Map<String, Integer> map = new HashMap<>();
		for (int i = 0; i < total.size(); i++) {
			List<TagConnect> tags = tagService.getTagsByQuestionID(total.get(i).getQuestionId());
			for (TagConnect tagConnect : tags) {
				String tagName = tagConnect.getTagName();
				if (map.containsKey(tagName)) {
					map.put(tagName, map.get(tagName) + 1);
				} else {
					map.put(tagName, 1);
				}
			}
		}
		return map;
	}



	@GetMapping("/single-tag/upvote")
	public Map<String, Integer> getSingeTagUpvote(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
												 @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Question> total = questionService.allQuery(from, end);
		Map<String, Integer> map = new HashMap<>();
		for (int i = 0; i < total.size(); i++) {
			List<TagConnect> tags = tagService.getTagsByQuestionID(total.get(i).getQuestionId());
			for (TagConnect tagConnect : tags) {
				String tagName = tagConnect.getTagName();
				if (map.containsKey(tagName)) {
					map.put(tagName, map.get(tagName) + total.get(i).getScore());
				} else {
					map.put(tagName, total.get(i).getScore());
				}
			}
		}
		return map;
	}
	@GetMapping("/single-tag/view")
	public Map<String, Integer> getSingeTagView(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
												  @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end) {
		List<Question> total = questionService.allQuery(from, end);
		Map<String, Integer> map = new HashMap<>();
		for (int i = 0; i < total.size(); i++) {
			List<TagConnect> tags = tagService.getTagsByQuestionID(total.get(i).getQuestionId());
			for (TagConnect tagConnect : tags) {
				String tagName = tagConnect.getTagName();
				if (map.containsKey(tagName)) {
					map.put(tagName, map.get(tagName) + total.get(i).getViewCount());
				} else {
					map.put(tagName, total.get(i).getViewCount());
				}
			}
		}
		return map;
	}
}
