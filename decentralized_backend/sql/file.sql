create table file
(
    file_id      int auto_increment comment '文件id'
        primary key,
    user_id      int                                null comment '上传该文件的用户',
    user_name    varchar(255)                       null comment '用户昵称或用户名',
    filename     varchar(1024)                      not null comment '文件名',
    filepath     varchar(1024)                      not null comment '文件路径',
    reference    varchar(1024)                      not null comment '文件在swarm中的编号',
    batch_id     varchar(1024)                      not null comment '邮票的batch_id',
    is_encrypted tinyint                            null comment '是否加密：0未加密，1加密',
    is_expired   tinyint  default 0                 not null comment '是否过期（所附着的邮票过期，则文件会被清理）：0：有效，1：过期',
    folder_id    int                                not null comment '所属文件夹id',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    is_deleted   tinyint  default 0                 not null comment '逻辑删除',
    constraint file_folder_folder_id_fk
        foreign key (folder_id) references folder (folder_id),
    constraint file_user_user_id_fk
        foreign key (user_id) references user (user_id)
)
    comment '文件表';

