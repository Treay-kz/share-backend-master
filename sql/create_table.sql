# 数据库初始化



-- 创建库
create database if not exists share_db;

-- 切换库
use share_db;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    codingId     varchar(256)                           null comment '内部编号（学号）',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    phone		 varchar(256) 							null comment '手机号码',
    email 		 varchar(256) 							null comment '邮箱',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'

) comment '用户' collate = utf8mb4_unicode_ci;

-- 文件表
create table if not exists notice
(
    id              bigint auto_increment comment '主键'
        primary key,
    userId          bigint                             null comment '上传人id',
    fileUrl         bigint                             null comment '文件路径',
    fileType        bigint                             null comment '文件类型（word、ppt、pdf、txt）',
    fileSize    	bigint 							   null comment '文件大小',
    fileStatus		int                            not null comment '文件状态:0-待审核 1-审核未通过 2-已发布',
    createTime      datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete        tinyint  default 0                 null comment '是否删除'
)   comment '文件表' collate = utf8mb4_unicode_ci;


-- 文章表
create table if not exists article
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    articleStatus   int  default 0                not null comment '文章状态: 0-待审核 1-审核未通过 2-已发布',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'

) comment '文章' collate = utf8mb4_unicode_ci;

-- 帖子表
create table if not exists tag
(
    id         bigint auto_increment comment '主键'
        primary key,
    tagName    varchar(256)                       null comment '标签名称',
    parentId   bigint                             null comment '父标签id ',
    isParent   tinyint                            null comment '是否为父标签 0-不是，1-父标签',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 null comment '是否删除',
    constraint unique_tagName
        unique (tagName)
)  comment '标签表' collate = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists article_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    articleId  bigint                             not null comment '文章 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '文章点赞';


-- 帖子收藏表（硬删除）
create table if not exists article_favour
(
    id         bigint auto_increment comment 'id' primary key,
    articleId  bigint                             not null comment '文章 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'

) comment '文章收藏';

