package cn.edu.sustech.service;

import cn.edu.sustech.entity.Tag;
import cn.edu.sustech.entity.TagConnect;
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
	private TagMapper mapper;
	
	@Autowired
	private TagConnectMapper connectMapper;
	
	public List<Tag> getTags() {
		return mapper.selectList(new QueryWrapper<>());
	}
	
	public List<Map<String, Object>> getTagCounts() {
		QueryWrapper<TagConnect> wrapper = new QueryWrapper<>();
		wrapper.select("tag_name", "count(*) as count")
			.groupBy("tag_name");
		return connectMapper.selectMaps(wrapper) ;
	}
	
}
