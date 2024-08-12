-- auto-generated definition
create table team
(
    team_id       int auto_increment comment '组id'
        primary key,
    team_name     varchar(512)                       not null comment '组名',
    user_id        int                                not null comment '创建人id',
    team_password varchar(128)                       not null comment '加入组时的密码，六位数字',
    description    varchar(1024)                      null comment '群组介绍',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null comment '修改时间',
    is_deleted     tinyint  default 0                 not null comment '逻辑删除',
    constraint team_user_user_id_fk
        foreign key (user_id) references user (user_id)
)
    comment '群组表';

