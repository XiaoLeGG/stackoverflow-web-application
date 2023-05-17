package cn.edu.sustech.service;

import cn.edu.sustech.entity.Question;
import cn.edu.sustech.entity.Tag;
import cn.edu.sustech.entity.TagConnect;
import cn.edu.sustech.mapper.QuestionMapper;
import cn.edu.sustech.mapper.TagConnectMapper;
import cn.edu.sustech.mapper.TagMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagService {
	
	@Autowired
	private TagMapper tagMapper;
	
	@Autowired
	private TagConnectMapper connectMapper;
	
	public List<Tag> getTags() {
		return tagMapper.selectList(new QueryWrapper<>());
	}
	
	public List<Map<String, Object>> getTagCounts() {
		QueryWrapper<TagConnect> wrapper = new QueryWrapper<>();
		wrapper.select("tag_name", "count(*) as count")
			.groupBy("tag_name");
		return connectMapper.selectMaps(wrapper) ;
	}

	public List<Map<String, Object>> getTagGroups() {
		QueryWrapper<TagConnect> wrapper = new QueryWrapper<>();
		wrapper.select("question_id", "string_agg(tag_name, ',' ORDER BY tag_name) as tag_group")
				.groupBy("question_id");
		return connectMapper.selectMaps(wrapper);
	}

	public List<TagConnect> getTagsByQuestionID(int questionID) {
		QueryWrapper<TagConnect> wrapper = new QueryWrapper<>();
		wrapper.eq("question_id", questionID);
		return connectMapper.selectList(wrapper);
	}
}
