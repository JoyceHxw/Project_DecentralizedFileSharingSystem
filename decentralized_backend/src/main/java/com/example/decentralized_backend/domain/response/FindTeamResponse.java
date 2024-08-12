package com.example.decentralized_backend.domain.response;

import com.example.decentralized_backend.domain.Team;
import lombok.Data;

@Data
public class FindTeamResponse {
    private Team team;
    private Boolean isJoined;
}
