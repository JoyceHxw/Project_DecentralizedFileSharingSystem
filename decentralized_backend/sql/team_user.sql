-- auto-generated definition
create table team_user
(
    id          int auto_increment comment '记录id'
        primary key,
    team_id    int                                not null comment '组id',
    user_id     int                                not null comment '用户id',
    is_creator  tinyint  default 0                 not null comment '是否是群组',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    is_deleted  tinyint  default 0                 not null comment '逻辑删除',
    constraint team_user_team_team_id_fk
        foreign key (team_id) references team (team_id),
    constraint team_user_user_user_id_fk
        foreign key (user_id) references user (user_id)
)
    comment '组和用户的关系表（多对多）';

