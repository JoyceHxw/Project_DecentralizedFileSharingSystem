create table account
(
    account_id      int auto_increment comment '账户id'
        primary key,
    account_address varchar(1024)                      not null comment '账户在区块链上的地址',
    user_id         int                                null comment '用户id',
    hostname        varchar(255)                       not null comment 'swarm运行的主机号',
    create_date     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_date     datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    is_deleted      tinyint  default 0                 not null comment '是否删除',
    constraint account_user_user_id_fk
        foreign key (user_id) references user (user_id)
)
    comment '账户表';

