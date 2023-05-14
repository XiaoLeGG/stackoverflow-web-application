package cn.edu.sustech.controller;

import cn.edu.sustech.entity.Tag;
import cn.edu.sustech.service.TagService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tag")
public class TagController {
	
	@Autowired
	private TagService service;
	
	@GetMapping("/all")
	public List<Tag> getAllTags() {
		return service.getTags();
	}
	
	@GetMapping("/count-all")
	public List<Map<String, Object>> getTagCounts() {
		return service.getTagCounts();
	}

}
