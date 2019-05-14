# DataExport 介绍

## 简介

依据前台输入的SQL语句进行查询，同时将结果以Excel表格的方式下载到本地。

## 主要技术点

* SpringBoot2
* Mybatis
* Druid（如果SpringBoot为2.x需要将Druid升级为1.1.13）
* POI

## 注意事项

* 由于MyBatis完全以传入的SQL为准，因此保险起见，需要配置数据库只读用户，以防对数据库的误操作