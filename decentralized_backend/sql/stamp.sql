create table stamp
(
    stamp_id    int auto_increment comment '邮票id'
        primary key,
    batch_id    varchar(1024)                      not null comment '邮票在区块链上的id',
    amount      bigint                             not null comment '一个块中包含的PLUR个数',
    depth       int                                not null comment '一个批次可以存储多少数据（块）',
    type        tinyint                            not null comment '邮票类型：0：不可变的；1：可变的',
    account_id  int                                not null comment '账户id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    is_expired  tinyint  default 0                 not null comment '是否过期：0：有效；1：过期',
    constraint stamp_account_account_id_fk
        foreign key (account_id) references account (account_id)
)
    comment '邮票表';
