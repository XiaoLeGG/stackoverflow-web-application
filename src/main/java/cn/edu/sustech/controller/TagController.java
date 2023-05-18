package cn.edu.sustech.controller;

import cn.edu.sustech.entity.Question;
import cn.edu.sustech.entity.Tag;
import cn.edu.sustech.entity.TagConnect;
import cn.edu.sustech.service.QuestionService;
import cn.edu.sustech.service.TagService;

import java.util.*;
import java.util.stream.Collectors;

import edu.stanford.nlp.util.CollectionUtils;
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

	@GetMapping("/group-tag/all/name")
	public List<String> getAllGroupTags() {
		List<Map<String, Object>> tagGroups = tagService.getTagGroups();
		Set<String> set = new HashSet<>();
		for (Map<String, Object> tagGroup : tagGroups) {
			set.add((String) tagGroup.get("tag_group"));
		}
		return new ArrayList<>(set);
	}


	@GetMapping("/single-tag/all/count")
	public List<Map<String, Object>> getAllTagCounts() {
		return tagService.getTagCounts();
	}

	private static List<List<String>> getSubListsOfSize(List<String> list, int size) {
		List<String> nowList = list.stream().distinct().sorted().collect(Collectors.toList());
		List<List<String>> result = new ArrayList<>();
		for (int i = 0; i < (1 << nowList.size()); i++) {
			if (Integer.bitCount(i) == size) {
				List<String> subList = new ArrayList<>();
				for (int j = 0; j < nowList.size(); j++) {
					if ((i & (1 << j)) != 0) {
						subList.add(nowList.get(j));
					}
				}
				result.add(subList);
			}
		}
		return result;
	}
	@GetMapping("/group-tag/all/count")
	public Map<String, Integer> getAllTagGroupCounts(@RequestParam("size") int size) {
		List<Map<String, Object>> tagGroups = tagService.getTagGroups();
		Map<String, Integer> map = new HashMap<>();
		for (Map<String, Object> tagGroup : tagGroups) {
			List<String> tags = new ArrayList<>();
			for (String tag : ((String) tagGroup.get("tag_group")).split(",")) {
				tags.add(tag);
			}
			List<List<String>> subListsOfSize = getSubListsOfSize(tags, size);
			for (List<String> subList : subListsOfSize) {
				String key = subList.stream().sorted().collect(Collectors.joining(","));
				if (map.containsKey(key)) {
					map.put(key, map.get(key) + 1);
				} else {
					map.put(key, 1);
				}
			}
		}
		return map;
	}

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


	@GetMapping("group-tag/count")
	public Map<String, Integer> getAllTagGroupCounts(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
													 @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end, @RequestParam("size") int size) {
		List<Question> total = questionService.allQuery(from, end);
		Map<String, Integer> map = new HashMap<>();
		for (int i = 0; i < total.size(); i++) {
			List<TagConnect> tags = tagService.getTagsByQuestionID(total.get(i).getQuestionId());
			List<String> tagNames = new ArrayList<>();
			for (TagConnect tagConnect : tags) {
				tagNames.add(tagConnect.getTagName());
			}
			List<List<String>> subListsOfSize = getSubListsOfSize(tagNames, size);
			for (List<String> subList : subListsOfSize) {
				String key = subList.stream().sorted().collect(Collectors.joining(","));
				if (map.containsKey(key)) {
					map.put(key, map.get(key) + 1);
				} else {
					map.put(key, 1);
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

	@GetMapping("/group-tag/upvote")
	public Map<String, Integer> getGroupTagUpvote(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
													 @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end, @RequestParam("size") int size) {
		List<Question> total = questionService.allQuery(from, end);
		Map<String, Integer> map = new HashMap<>();
		for (int i = 0; i < total.size(); i++) {
			List<TagConnect> tags = tagService.getTagsByQuestionID(total.get(i).getQuestionId());
			List<String> tagNames = new ArrayList<>();
			for (TagConnect tagConnect : tags) {
				tagNames.add(tagConnect.getTagName());
			}
			List<List<String>> subListsOfSize = getSubListsOfSize(tagNames, size);
			for (List<String> subList : subListsOfSize) {
				String key = subList.stream().sorted().collect(Collectors.joining(","));
				if (map.containsKey(key)) {
					map.put(key, map.get(key) + total.get(i).getScore());
				} else {
					map.put(key, total.get(i).getScore());
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

	@GetMapping("/group-tag/view")
	public Map<String, Integer> getGroupTagView(@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
														 @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date end, @RequestParam("size") int size) {
		List<Question> total = questionService.allQuery(from, end);
		Map<String, Integer> map = new HashMap<>();
		for (int i = 0; i < total.size(); i++) {
			List<TagConnect> tags = tagService.getTagsByQuestionID(total.get(i).getQuestionId());
			List<String> tagNames = new ArrayList<>();
			for (TagConnect tagConnect : tags) {
				tagNames.add(tagConnect.getTagName());
			}
			List<List<String>> subListsOfSize = getSubListsOfSize(tagNames, size);
			for (List<String> subList : subListsOfSize) {
				String key = subList.stream().sorted().collect(Collectors.joining(","));
				if (map.containsKey(key)) {
					map.put(key, map.get(key) + total.get(i).getViewCount());
				} else {
					map.put(key, total.get(i).getViewCount());
				}
			}
		}
		return map;
	}
}
