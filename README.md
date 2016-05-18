# slick-starting-on-the-right-foot

This is an sample project for showcasing best practices and providing a seed for starting with Slick 3.1.1.
Adapted from the [Knoldus project](https://github.com/knoldus/slick-starting-on-the-right-foot).

## Clone Project:

```
$ git clone git@github.com:mslinn/slick-starting-on-the-right-foot.git

$ cd slick-starting-on-the-right-foot

$ sbt clean compile
```

Unit tests have use the H2 database. If you want run the demo app you need to create a database in Postgres or MySQL.

### For Postgres, run psql, then:

````
CREATE DATABASE bank_db;
\c
use bank_db;
````
See postgresSchema.sql for the DDL to create the tables.

### For MySQL, run mysql, then:

```
create database bank_db;
use bank_db;
```

See mysqlSchema.sql for the DDL to create the tables. This file is also used for unit tests.

### Run the app:

```
$ sbt run
info] Running com.knol.db.Demo
[INFO] - [2015-08-16 18:42:25,070] - [com.zaxxer.hikari.HikariDataSource]  HikariCP pool mysql is starting.
List((Bank(ICICI bank,Some(1)),Some(BankInfo(Government,1000,1,Some(1)))), (Bank(SBI Bank,Some(2)),None))
List((Bank(ICICI bank,Some(1)),Some(BankProduct(Car loan,1,Some(1)))), (Bank(SBI Bank,Some(2)),None))
```
