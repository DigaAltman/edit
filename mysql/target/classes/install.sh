#!/bin/bash
# 1.检查当前运行环境是否安装了 wget 命令工具, 如果没有安装, 则安装 wget
if [ `rpm -qa | grep 'wget' | wc -l` -le 0 ];then
    echo "[ERROR] wget 未安装, 即将安装wget依赖"
    yum install -y wget
fi

# 2.检查当前是否存在 mysql 安装依赖
if [ `ll mysql57-community-release-el7-8.noarch.rpm | wc - l` -le 0 ];then
    echo '[ERROR] mysql依赖不存在,即将下载mysql依赖'
    wget http://repo.mysql.com/mysql57-community-release-el7-8.noarch.rpm
fi

echo "[INFO] 已经下载mysql依赖, 开启解压依赖"
rpm -ivh mysql57-community-release-el7-8.noarch.rpm

rm -rf mysql57-community-release-el7-8.noarch.rpm

echo "[INFO] 开始安装 MySQL"
yum install -y mysql-community-server

echo "[INFO] 启动 MySQL"
service mysqld start

# [启动MySQL会报错...]
# ERROR 1045 (28000): Access denied for user 'root'@'localhost' (using password: NO)
#
# vim /etc/my.cnf
#
# 在mysqld下添加一个跳过验证
# [mysqld]
# skip-grant-tables
#
# 重启 mysql, 命令是: service mysqld restart
#
#
# 登录mysql
# mysql -uroot
#
# 切换到mysql数据库
# use mysql
#
# 修改mysql密码
# update user set authentication_string=password('root_1234') where user='root' and Host='localhost';
#
#
# 初始化 8 个数据库
# create database db1 default charset utf8 collate utf8_general_ci;
# create database db2 default charset utf8 collate utf8_general_ci;
# create database db3 default charset utf8 collate utf8_general_ci;
# create database db4 default charset utf8 collate utf8_general_ci;
# create database db5 default charset utf8 collate utf8_general_ci;
# create database db6 default charset utf8 collate utf8_general_ci;
# create database db7 default charset utf8 collate utf8_general_ci;
# create database db8 default charset utf8 collate utf8_general_ci;
#
# 创建 8 个用户
# CREATE USER 'db1'@'localhost'  IDENTIFIED BY 'db1_1234';
# CREATE USER 'db1'@'%'  IDENTIFIED BY 'db1_1234';
#
# CREATE USER 'db2'@'localhost'  IDENTIFIED BY 'db2_1234';
# CREATE USER 'db2'@'%'  IDENTIFIED BY 'db2_1234';
#
# CREATE USER 'db3'@'localhost'  IDENTIFIED BY 'db3_1234';
# CREATE USER 'db3'@'%'  IDENTIFIED BY 'db3_1234';
#
# CREATE USER 'db4'@'localhost'  IDENTIFIED BY 'db4_1234';
# CREATE USER 'db4'@'%'  IDENTIFIED BY 'db4_1234';
#
# CREATE USER 'db5'@'localhost'  IDENTIFIED BY 'db5_1234';
# CREATE USER 'db5'@'%'  IDENTIFIED BY 'db5_1234';
#
# CREATE USER 'db6'@'localhost'  IDENTIFIED BY 'db6_1234';
# CREATE USER 'db6'@'%'  IDENTIFIED BY 'db6_1234';
#
# CREATE USER 'db7'@'localhost'  IDENTIFIED BY 'db7_1234';
# CREATE USER 'db7'@'%'  IDENTIFIED BY 'db7_1234';
#
# CREATE USER 'db8'@'localhost'  IDENTIFIED BY 'db8_1234';
# CREATE USER 'db8'@'%'  IDENTIFIED BY 'db8_1234';
#
#
# 分配权限
# grant all privileges on db1.* to db1@'%' identified by 'db1_1234';
# grant all privileges on db2.* to db2@'%' identified by 'db2_1234';
# grant all privileges on db3.* to db3@'%' identified by 'db3_1234';
# grant all privileges on db4.* to db4@'%' identified by 'db4_1234';
# grant all privileges on db5.* to db5@'%' identified by 'db5_1234';
# grant all privileges on db6.* to db6@'%' identified by 'db6_1234';
# grant all privileges on db7.* to db7@'%' identified by 'db7_1234';
# grant all privileges on db8.* to db8@'%' identified by 'db8_1234';
#
# 刷新权限
# flush privileges;