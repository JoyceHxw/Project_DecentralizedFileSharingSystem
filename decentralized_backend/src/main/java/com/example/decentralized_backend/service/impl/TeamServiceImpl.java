package com.example.decentralized_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.decentralized_backend.domain.*;
import com.example.decentralized_backend.domain.request.FolderCreateRequest;
import com.example.decentralized_backend.domain.request.JoinTeamRequest;
import com.example.decentralized_backend.domain.response.FindTeamResponse;
import com.example.decentralized_backend.exception.BusinessException;
import com.example.decentralized_backend.mapper.FolderMapper;
import com.example.decentralized_backend.mapper.TeamUserMapper;
import com.example.decentralized_backend.service.FolderService;
import com.example.decentralized_backend.service.TeamService;
import com.example.decentralized_backend.mapper.TeamMapper;
import com.example.decentralized_backend.service.TeamUserService;
import com.example.decentralized_backend.service.UserService;
import com.example.decentralized_backend.utils.Result;
import com.example.decentralized_backend.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
* @author 81086
* @description 针对表【team(群组表)】的数据库操作Service实现
* @createDate 2024-04-28 18:45:45
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamUserService teamUserService;

    @Autowired
    private TeamUserMapper teamUserMapper;

    @Autowired
    private FolderService folderService;

    @Autowired
    private FolderMapper folderMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result createTeam(Team team, HttpServletRequest request) {
        Integer userId = team.getUserId();
        String teamName = team.getTeamName();
        String teamPassword = team.getTeamPassword();
        //检验是否是当前作者
        userService.verifyAuth(userId,request);
        User currentUser = userService.getCurrentUser(request);
        //检查参数为空
        if(teamName==null || teamPassword==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        if(Strings.isBlank(teamName)){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍名称不能为空");
        }
        //检验密码是否为六位数字
        if(teamPassword.length()!=6){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"密码必须为六位数字");
        }
        for(int i=0; i<teamPassword.length(); i++){
            if(!Character.isDigit(teamPassword.charAt(i))){
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"密码必须为六位数字");
            }
        }
        //检验名称是否重复
        LambdaQueryWrapper<Team> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getTeamName,teamName);
        Team teamExist = teamMapper.selectOne(queryWrapper);
        if(teamExist !=null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"群组名已存在");
        }
        //创建群组的根目录
        FolderCreateRequest folderCreateRequest=new FolderCreateRequest();
        folderCreateRequest.setFolderName("/");
        folderCreateRequest.setParentFolderId(0);
        folderCreateRequest.setUserId(userId);
        Integer rootFolderId = folderService.createFolder(folderCreateRequest, request);
        //保存到数据库
        team.setUserName(currentUser.getNickname()==null? currentUser.getUsername():currentUser.getNickname());
        team.setRootFolderId(rootFolderId);
        boolean save = this.save(team);
        if(!save){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库插入失败");
        }
        //保存在关系数据库
        TeamUser teamUser=new TeamUser();
        teamUser.setUserId(userId);
        teamUser.setTeamId(team.getTeamId());
        teamUser.setIsCreator(1);
        boolean save1 = teamUserService.save(teamUser);
        if(!save1){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库插入失败");
        }
        return Result.ok(null,"创建群组成功");
    }

    @Override
    public Result findByName(Integer userId, String teamName, Boolean isMy,HttpServletRequest request) {
        //检验登录
        userService.verifyAuth(userId,request);
        //查询数据库
        LambdaQueryWrapper<Team> queryWrapper=new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(teamName)){
            queryWrapper.like(Team::getTeamName,teamName);
        }
        Set<Integer> myTeamSet = new HashSet<>();
        LambdaQueryWrapper<TeamUser> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(TeamUser::getUserId, userId);
        for (TeamUser teamUser : teamUserMapper.selectList(queryWrapper1)) {
            myTeamSet.add(teamUser.getTeamId());
        }
        //我加入的群组，需要从关系表中找
        if(isMy){
            if(!myTeamSet.isEmpty()){
                //不能为空
                queryWrapper.in(Team::getTeamId,myTeamSet);
            }
            else{
                //返回空数组
                return Result.ok(new ArrayList<>());
            }
        }
        List<FindTeamResponse> responseList=new ArrayList<>();
        for (Team team : teamMapper.selectList(queryWrapper)) {
            FindTeamResponse response=new FindTeamResponse();
            response.setTeam(team);
            response.setIsJoined(myTeamSet.contains(team.getTeamId()));
            responseList.add(response);
        }
        return Result.ok(responseList);
    }

    @Override
    public Result joinTeam(JoinTeamRequest joinTeamRequest, HttpServletRequest request) {
        Integer userId = joinTeamRequest.getUserId();
        Integer teamId = joinTeamRequest.getTeamId();
        String password = joinTeamRequest.getPassword();
        //检验登录
        userService.verifyAuth(userId,request);
        //检验参数不为空
        if(teamId==null || password==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        //检查群组是否存在
        LambdaQueryWrapper<Team> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getTeamId,teamId);
        Team team = teamMapper.selectOne(queryWrapper);
        if(team==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"群组不存在");
        }
        //检查是否已经加入队伍
        LambdaQueryWrapper<TeamUser> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(TeamUser::getTeamId,teamId);
        boolean isFlag=false;
        for (TeamUser teamUser : teamUserMapper.selectList(queryWrapper1)) {
            if(Objects.equals(userId,teamUser.getUserId())){
                isFlag=true;
                break;
            }
        }
        if(isFlag){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"不能重复加入群组");
        }
        //检查密码是否正确
        if(!Objects.equals(password,team.getTeamPassword())){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"进群密码错误");
        }
        //创建关系保存数据库
        TeamUser teamUser=new TeamUser();
        teamUser.setUserId(userId);
        teamUser.setTeamId(teamId);
        teamUser.setIsCreator(0);
        boolean save = teamUserService.save(teamUser);
        if(!save){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库插入失败");
        }
        return Result.ok(null,"加入成功");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result quitTeam(Integer userId, Integer teamId, HttpServletRequest request) {
        //检查登录
        userService.verifyAuth(userId,request);
        //检查参数
        if(teamId==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        //检查是否处于队伍中
        LambdaQueryWrapper<TeamUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(TeamUser::getUserId,userId);
        queryWrapper.eq(TeamUser::getTeamId,teamId);
        TeamUser teamUser = teamUserMapper.selectOne(queryWrapper);
        if(teamUser==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"不在该队伍中");
        }
        //删除这条记录
        int i = teamUserMapper.deleteById(teamUser);
        if(i==0){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库删除错误");
        }
        //检擦队伍剩余人数，如果为0，则解散群组
        LambdaQueryWrapper<TeamUser> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(TeamUser::getTeamId,teamId);
        queryWrapper1.orderByAsc(TeamUser::getCreateTime);
        List<TeamUser> teamUsers = teamUserMapper.selectList(queryWrapper1);
        if(teamUsers.isEmpty()){
            //先删除根目录
            Team team = teamMapper.selectById(teamId);
            if(team==null){
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"群组不存在");
            }
            int i2 = folderMapper.deleteById(team.getRootFolderId());
            if(i2==0){
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库删除错误");
            }
            //删除群组表
            int i1 = teamMapper.deleteById(teamId);
            if(i1==0){
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库删除错误");
            }
            return Result.ok(null,"退群成功");
        }
        //检查是否是创建者，如果是则群主自动转让给第二个加入群组的人
        if(Objects.equals(teamUser.getIsCreator(),1)){
            LambdaQueryWrapper<Team> queryWrapper2=new LambdaQueryWrapper<>();
            queryWrapper2.eq(Team::getTeamId,teamId);
            Team team = teamMapper.selectOne(queryWrapper2);
            team.setUserId(teamUsers.get(0).getUserId());
            //更新群组表
            boolean update = this.updateById(team);
            if(!update){
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库更新失败");
            }
        }
        return Result.ok(null,"退群成功");
    }

    @Override
    public Result updateTeam(Team team, HttpServletRequest request) {
        Integer userId = team.getUserId();
        String teamName = team.getTeamName();
        Integer teamId = team.getTeamId();
        userService.verifyAuth(userId,request);
        //检验队伍是否存在
        if(teamId==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        LambdaQueryWrapper<Team> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getTeamId,teamId);
        Team team1 = teamMapper.selectOne(queryWrapper);
        if(team1==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍不存在");
        }
        //检验是否是创建者
        if(!Objects.equals(team1.getUserId(),userId)){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"没有编辑权限");
        }
        //队伍名称不能为空
        if(Strings.isBlank(teamName)){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍名称不能为空");
        }
        //检验名称是否重复
        LambdaQueryWrapper<Team> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.ne(Team::getTeamId,teamId);
        queryWrapper1.eq(Team::getTeamName,teamName);
        Team team2 = teamMapper.selectOne(queryWrapper1);
        if(team2!=null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"队伍名称重复");
        }
        // 获取当前时间的毫秒数
        long currentTimeMillis = System.currentTimeMillis();
        // 创建Date对象表示当前时间
        Date currentDate = new Date(currentTimeMillis);
        team.setUpdateTime(currentDate);
        //更新数据库
        int i=teamMapper.updateById(team);
        if(i==0){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库更新失败");
        }
        return Result.ok(null,"更新成功");
    }
}




