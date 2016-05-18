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
CREATE TABLE bank(id BIGSERIAL PRIMARY KEY, name varchar(200));
CREATE TABLE bankinfo(id BIGSERIAL, owner varchar(200), bank_id BIGINT REFERENCES bank(id), branches BIGINT);
CREATE TABLE bankproduct(id BIGSERIAL PRIMARY KEY, name varchar(200), bank_id BIGINT references bank(id));
````

### For MySQL, run mysql, then:

```
create database bank_db;
use bank_db;
CREATE TABLE bank(id int PRIMARY KEY auto_increment, name varchar(200));
CREATE TABLE bankinfo(id int PRIMARY KEY auto_increment, owner varchar(200), bank_id int references bank(id), branches int );
CREATE TABLE bankproduct(id int PRIMARY KEY auto_increment, name varchar(200), bank_id int references bank(id));
```

### Run the app:

```
$ sbt run
info] Running com.knol.db.Demo
[INFO] - [2015-08-16 18:42:25,070] - [com.zaxxer.hikari.HikariDataSource]  HikariCP pool mysql is starting.
List((Bank(ICICI bank,Some(1)),Some(BankInfo(Goverment,1000,1,Some(1)))), (Bank(SBI Bank,Some(2)),None))
List((Bank(ICICI bank,Some(1)),Some(BankProduct(car loan,1,Some(1)))), (Bank(SBI Bank,Some(2)),None))
```
