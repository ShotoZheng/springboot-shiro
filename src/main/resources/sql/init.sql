CREATE TABLE user (
  id varchar(32) COLLATE utf8_bin NOT NULL COMMENT '用户id',
  username varchar(64) COLLATE utf8_bin NOT NULL COMMENT '用户名',
  password varchar(32) COLLATE utf8_bin NOT NULL COMMENT '用户密码',
  status char(1) COLLATE utf8_bin DEFAULT '1' COMMENT '用户状态',
  PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

INSERT INTO user VALUES ('1', 'admin', '123456', '1');

CREATE TABLE role  (
   id INT(11) NOT NULL COMMENT '主键id',
   name VARCHAR(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '角色名称',
   PRIMARY KEY (id) USING BTREE
) ENGINE = INNODB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = COMPACT;

CREATE TABLE user_role  (
    user_id INT(10) NULL DEFAULT NULL COMMENT '用户id',
    role_id INT(10) NULL DEFAULT NULL COMMENT '角色id'
) ENGINE = INNODB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = COMPACT;

CREATE TABLE permission  (
 id INT(11) NOT NULL COMMENT '主键id',
 url VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '权限路径',
 name VARCHAR(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '权限名称',
 PRIMARY KEY (id) USING BTREE
) ENGINE = INNODB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = COMPACT;

CREATE TABLE role_permission  (
  roleid INT(10) NULL DEFAULT NULL COMMENT '角色id',
  pid INT(10) NULL DEFAULT NULL COMMENT '权限id'
) ENGINE = INNODB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = COMPACT;

insert into user (id, username, password) values('1','admin','038bdaf98f2037b31f1e75b5b4c9b26e');
insert into user (id, username, password) values('2','user','098d2c478e9c11555ce2823231e02ec1');

insert into role (id, name) values('1','admin');
insert into role (id, name) values('2','user');

insert into user_role (user_id, role_id) values('1','1');
insert into user_role (user_id, role_id) values('2','2');

insert into permission (id, url, name) values('1','/admin','admin:list');
insert into permission (id, url, name) values('2','/user','user:list');

insert into role_permission (roleid, pid) values('1','1');
insert into role_permission (roleid, pid) values('1','2');
insert into role_permission (roleid, pid) values('2','2');