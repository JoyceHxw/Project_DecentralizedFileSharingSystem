create table users
(
    user_id     int auto_increment comment '用户id'
        primary key,
    username    varchar(255)                       not null comment '用户名',
    password    varchar(255)                       not null comment '密码',
    nickname    varchar(255)                       null comment '昵称',
    avatar      varchar(1024)                      null comment '头像',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    is_deleted  tinyint  default 0                 not null comment '是否删除'
)
    comment '用户表';