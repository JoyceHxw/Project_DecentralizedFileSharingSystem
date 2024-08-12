package com.example.decentralized_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.decentralized_backend.domain.TeamUser;
import com.example.decentralized_backend.service.TeamUserService;
import com.example.decentralized_backend.mapper.TeamUserMapper;
import org.springframework.stereotype.Service;

/**
* @author 81086
* @description 针对表【team_user(组和用户的关系表（多对多）)】的数据库操作Service实现
* @createDate 2024-04-28 18:51:36
*/
@Service
public class TeamUserServiceImpl extends ServiceImpl<TeamUserMapper, TeamUser>
    implements TeamUserService {

}




