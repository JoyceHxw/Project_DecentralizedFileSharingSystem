package com.example.decentralized_backend.controller;

import com.example.decentralized_backend.domain.Team;
import com.example.decentralized_backend.domain.request.JoinTeamRequest;
import com.example.decentralized_backend.exception.BusinessException;
import com.example.decentralized_backend.service.TeamService;
import com.example.decentralized_backend.utils.Result;
import com.example.decentralized_backend.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
@RestController
@RequestMapping("team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    /**
     * 创建群组
     * @param team 群组参数
     * @param request 请求参数
     * @return result
     */
    @PostMapping("create")
    public Result createTeam(@RequestBody Team team, HttpServletRequest request){
        if(team ==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return teamService.createTeam(team, request);
    }

    /**
     * 根据群组名查找群组
     * @param userId 用户id
     * @param teamName 搜索名称
     * @param request 请求参数
     * @return result
     */
    @GetMapping("find")
    public Result findByName(@RequestParam Integer userId, @RequestParam String teamName, @RequestParam Boolean isMy, HttpServletRequest request){
        return teamService.findByName(userId,teamName,isMy,request);
    }

    /**
     * 加入队伍操作
     * @param joinTeamRequest 加入队伍参数
     * @param request 请求参数
     * @return result
     */
    @PostMapping("join")
    public Result joinTeam(@RequestBody JoinTeamRequest joinTeamRequest, HttpServletRequest request){
        if(joinTeamRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return teamService.joinTeam(joinTeamRequest,request);
    }

    /**
     * 退出队伍
     * @param userId 用户id
     * @param teamId 队伍id
     * @param request 请求参数
     * @return result
     */
    @PostMapping("quit")
    public Result quitTeam(@RequestParam Integer userId, @RequestParam Integer teamId, HttpServletRequest request){
        return teamService.quitTeam(userId,teamId,request);
    }

    /**
     * 更新队伍信息
     * @param team 队伍信息
     * @param request 请求参数
     * @return result
     */
    @PostMapping("update")
    public Result updateTeam(@RequestBody Team team, HttpServletRequest request){
        if(team==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return teamService.updateTeam(team,request);
    }

}
