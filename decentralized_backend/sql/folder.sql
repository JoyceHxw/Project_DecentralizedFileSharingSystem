
create table folder
(
    folder_id        int auto_increment comment '文件夹id'
        primary key,
    folder_name      varchar(255) not null comment '文件夹名称',
    parent_folder_id int          not null comment '父文件夹id',
    user_id          int          not null comment '创建者',
    constraint folder_user_user_id_fk
        foreign key (user_id) references user (user_id)
)
    comment '文件夹';
