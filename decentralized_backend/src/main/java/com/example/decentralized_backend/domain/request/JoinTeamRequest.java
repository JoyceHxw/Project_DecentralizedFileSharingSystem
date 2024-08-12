package com.example.decentralized_backend.domain.request;

import lombok.Data;

@Data
public class JoinTeamRequest {
    private Integer userId;
    private Integer teamId;
    private String password;
}
