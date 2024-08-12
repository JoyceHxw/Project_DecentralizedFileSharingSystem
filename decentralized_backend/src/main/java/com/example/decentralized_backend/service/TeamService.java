package com.example.decentralized_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.decentralized_backend.domain.Team;
import com.example.decentralized_backend.domain.request.JoinTeamRequest;
import com.example.decentralized_backend.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;

/**
* @author 81086
* @description 针对表【team(群组表)】的数据库操作Service
* @createDate 2024-04-28 18:45:45
*/
public interface TeamService extends IService<Team> {

    Result createTeam(Team team, HttpServletRequest request);

    Result findByName(Integer userId, String teamName, Boolean isMy, HttpServletRequest request);

    Result joinTeam(JoinTeamRequest joinTeamRequest, HttpServletRequest request);

    Result quitTeam(Integer userId, Integer teamId, HttpServletRequest request);

    Result updateTeam(Team team, HttpServletRequest request);
}
