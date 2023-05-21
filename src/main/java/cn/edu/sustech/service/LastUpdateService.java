package cn.edu.sustech.service;

import cn.edu.sustech.mapper.LastUpdateMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LastUpdateService {

  @Autowired private LastUpdateMapper lastUpdateMapper;

  public Date lastUpdateTime() {
    return lastUpdateMapper.selectOne(new QueryWrapper<>()).getLastUpdateTime();
  }
}
