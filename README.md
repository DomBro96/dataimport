# dataimport 数据快速导入


## 目录

- [1. 写在前面](#1)
- [2. 项目简介](#2)
- [3. 实现思路](#3)
 - [3.1 文件导入数据库](#3.1)
 - [3.2 数据库导出文件](#3.2)
 - [3.3 数据库品牌转换](#3.3)
 - [3.4 文件格式转换](#3.4)
- [4. 设计思想](#4)
 - [4.1 使用框架](#4.1)
 - [4.2 设计模式](4.2)
- [5. 问题解决](#5)
 - [5.1 调用命令行cmd](#5.1)
 - [5.2 MongoDB的使用](#5.2)
 - [5.3 对 XLSX2CSV 的修改](#5.3)
 - [5.4 MySql导入MongoDB导出csv的坑](#5.4)
 - [5.5 编码问题](#5.5)
- [6. 有待改善](#6)
- [7. 感谢的话](#7)
- [8. 注意](#8)


<h2 id="1">1. 写在前面</h2>


没想过会自己硬刚一个项目，想了半天也不知道 README 应该写些什么。从开始构思到敲代码，因为期间还要上课，拖了一个多月的时间，每天都盼着早点写完，奇怪的是就算写好了也没有啥成就感。不如就拿 README 写个总结吧。


<h2 id="2">2. 项目简介</h2>

拿到这个项目的时候只有三行项目需求。如下：

> 背景说明：目前系统中，不可避免会需要将Excel表数据手动导入到数据库中。
任务目标：开发一个独立的工具，通过界面或者配置文件的方式定义文件和数据库列的映射关系，支持多种数据源之间的互相导入，包括却不限于csv、xls、xlsx、txt（带制表符）、MySQL、MongoDB。


老实说，我们几个都被这个项目需求整懵了，数据源之间的互相导入是什么鬼？难道是要求上面说的几种文件格式和数据库之间互相导入？做了一周的需求分析，确立了四个功能


- 1.文件导入数据库

将 xlsx、xls、csv格式文件导入 MySql数据库表中 以及 MongoDB数据库集合中


- 2.数据库导出文件

将 MySql数据库表记录 和 MongoDB数据库集合记录 导出为 xlsx、xls、csv格式文件

- 3.数据库品牌转换

将 MySql数据库表记录 和 MongoDB数据库集合记录 互相导入

- 4.文件格式转换

将 xlsx、xls、csv 三种格式文件的互相转换




我还是不太放心，问了一下项目负责人有没有漏掉的功能，谁知他说你们只需要做正向导入就好了呀（也就是上面的第一条），不过要是可以做出来反向导出更好了。合着给自己多找了那么多活，但是自己装德逼，哭着也要装到最后。**就这样，dataimport 被定性为可以实现数据之间转换的工具**(所以叫数据快速导入有点委屈了)!





<h2 id="3">3. 实现思路</h2>


如何具体实现的可以看代码，这就仅仅说一下思路吧。


<h3 id="3.1">3.1 文件导入数据库</h3>

csv 是个好东西！csv 即逗号分隔符文件，每个数据单元之间用 “ , ” 分割。重要的是 MySql 和 MongoDB 都对导入 csv 文件十分支持，而且 MySql 导入 csv 文件的速度是按条插入速度的二十倍。

- 导入 MySql 数据库

将 xlsx、xls、csv 导入 MySql数据表，分三步走，首先将 xlsx 和 xls 文件导成 csv 文件，然后再将 csv 文件导入 MySql，最后导出该表的 sql 文件给用户。
xlsx 和 xls 文件导出 csv 文件 apche-POI 官方给了两个工具类。分别是    [XLSX2CSV](https://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/xssf/eventusermodel/XLSX2CSV.java)  和 [XLS2CSV](https://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/hssf/eventusermodel/examples/XLS2CSVmra.java)。 这两个类需要自己拷贝，由于需求原我在[项目源码](https://github.com/DomBro96/dataimport/blob/master/src/main/java/cn/dombro/dataimport/util/XLSX2CSV.java)中对 XLSX2CSV 做了一点点的改动，会在[5.3 对 XLSX2CSV 的修改](#5.3) 解释。然后就是 MySql 如何导入 csv 的问题了，你只需要写一个 sql 语句

```
load data local infile 'your_csv_file' into table tablename character set utf8 fields terminated by ',' optionally enclosed by '"' lines terminated by '\r\n'; 
local ：表示导入本地文件
fields terminated by ',' : 表示每个导入字段用 "," 分割 
optionally enclosed by '"' : 表示忽略 引号 " "
line terminated by '\r\n' : 表示导入记录之间有换行符   
```
导出 sql 文件 需要使用 MySql 自带的工具 mysqldump

```
mysqldump -u user -p password databaseName tableName > your_sql_file
```
一般不建议把密码写在语句中，所以还有一种写法使用 -S ，下面的 sockfile 是 你的sock文件

```
mysqldump -u root -S  sockfile  databaseName tableName > your_sock_file
```



- 导入 MongoDB 数据库

与 导入 导入 MySql 数据库数据库一样，文件导入 MongoDB 同样是三步走的套路，只是 mongoDB 导入 csv 需要借助MongoDB自带工具 mongoimport

```
mongoimport --db mytest -f id,number,name,gander,phone,address,email  --collection contact --type csv --file  your_scv_file
--db ： 导入的数据库
--collection : 导入的集合
-f : 每行“,” 分割的数据导入数据库时对应列名
--file : 导入文件名
```

上面 -f 选项后面的 列名 可以根据自己的列名去写，但是不能没有，如果你要导入MongoDB的是一个 csv文件 ，那么 -f 是一个必选选项。
MongoDB 导出的原文件为 .json 格式，需要使用 MongoDB 自带的 mongoexport 导出工具导出


```
mongoexport -d database -c collection -o json_path
-d 选项后接数据库参数
-c 选项后接集合参数
-o 后接导出 json 路径
```



<h3 id="3.2">3.2 数据库导出文件</h3>


数据库表（集合）导出csv、xls、xlsx格式文件。MySql 和 MongoDB 自然支持导出 csv 文件，所以重点是 csv 转换为 xls、xlsx ，我用到两个maven依赖 javacsv 和 jfreereport 在[pom.xml](https://github.com/DomBro96/dataimport/blob/master/pom.xml)可以查看一下。转换过程[戳这里](https://github.com/DomBro96/dataimport/blob/master/src/main/java/cn/dombro/dataimport/util/CsvUtil.java)。

- 从 MySql 中导出 csv 

首先需要将用户上传的 .sql 数据库表文件导入 MySql 原本想着使用 sql 语句实现

```
source sql_file_path 
```

但这条 sql 语句竟然不能被执行(反正我是不能执行)，只好利用使用 cmd 语句

```
mysql -u user -p password -D database < sql_file_path
```

然后就是导出 csv 文件了，同样是一个 sql 语句

```
SELECT * FROM contact into outfile 'csv_file_path' fields terminated by ',' optionally enclosed by '"' escaped by '"' lines terminated by '\r\n';
fields terminated : 每个字段以 xxx 字符进行分割
optionally enclosed by : 字段用 " 扩起
escaped by : 字段使用转移符 "
注意 ： outfile 导出文件文件位置 必须 为 my.ini(Linux 下位 my.cnf) 配置文件的 secure-file-priv 配置
```

- 从 MongoDB 导出 csv

首先将用户 json 文件导入 MongoDB，同样使用 mongoimport


``` 
mongoimport --db  database  --collection  collection --file json_file_path
```


然后使用 mongoexport 导出 csv 文件


```
mongoexport --type=csv -f fields  -d database -c collection --noHeaderLine  -o csv_file_path
-f 后接要导出的字段名
--noHeaderLine 意思是导出的 csv 文件不包含字段名
```


<h3 id="3.3">3.3 数据库品牌转换</h3>

这个功能是把 MySql 表中数据导入 MongoDB 集合中，MongoDB 集合数据导入 MySqL 表中。其实这样说很不严谨，因为 MongoDB 是 NoSql 数据库，所以将 MongDB集合数据 导入 MySql表 没什么太大意义。这里的实现过程在上面两个小节都谈到了。

```
第一步导入数据库源文件(MySql 为 .sql MongoDB 为 .json)；
第二步导出 csv ；
第三步将csv导入目标品牌数据库；
最后在导出目标品牌数据库可执行文件(MySql 为 .sql MongoDB 为 .json)。
```

在 MySql 导入 MongoDB 导出的 csv文件时有一个坑，会在[5.4 MySql导入MongoDB导出csv的坑](#5.4)提到解决方法。



<h3 id="3.4">3.4 文件格式转换</h3>

文件格式转换 需要用到 apche-POI 、javacsv 、jfreereport 在[pom.xml](https://github.com/DomBro96/dataimport/blob/master/pom.xml)可以查看一下。这里的实现思路很简单，把工具类中的方法进行一个简单的封装（封装到service层），在由上层调用就可以了。[csv 与 xls、xlsx 互相换](https://github.com/DomBro96/dataimport/blob/master/src/main/java/cn/dombro/dataimport/util/CsvUtil.java)上面已将提到了，[xls 与 xlsx的转换](https://github.com/DomBro96/dataimport/blob/master/src/main/java/cn/dombro/dataimport/util/ExcelUtil.java) 同样借助 csv 就可以了。




<h2 id="4">4. 设计思想</h2>

 

<h3 id="4.1">4.1 使用框架</h3>


经过上面的[实现思路](#3)，你会发现整个项目没有关于表记录的任何CRUD操作。所以选用了国内非常优秀的一款开源框架 [JFinal](http://www.jfinal.com/),目的是为了将重点放在业务逻辑的处理，所以并没有原则 ORM 框架进行开发。



<h3 id="4.2">4.2 设计模式</h3>

说到设计模式其实有点扯了，只不过用到了一点皮毛。

- 整体模式

当然是经典的分层 MVC 模式，可是这个项目很奇特，没有 model 前后端也是分离的，所以并不是传统的 MVC 。最有趣的是 DAO 的写法，一开始为如何写DAO感到难受，毕竟这涉及到两种数据库，于是分别为两种数据库操作设计DAO——MySqlDAO 、MongoDBDAO，[戳这里可查](https://github.com/DomBro96/dataimport/tree/master/src/main/java/cn/dombro/dataimport/dao)。

- 工厂模式


开始的时候很随意的让 Service 依赖 DAO ，Controller 依赖 Service ，上课期间接触了 GoF 的工厂模式，想来想去还是将代码重构了。Service 调用 DAO 时用到了抽象工厂模式（因为两种DAO并不能抽象成同一类产品，反而是一种产品族的关系），Controller 调用 Service 同样用到了抽象工厂模式。关于工厂模式，可以看一下我的这篇[Java工厂设计模式](https://github.com/DomBro96/MyNotes/blob/master/OOP/Java%E5%B7%A5%E5%8E%82%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F.md)。



<h2 id="5">5. 问题解决</h2>

写的时候遇到好多坑，在这一模块记录一下。

<h3 id="5.1">5.1 调用命令行cmd</h3>

在实现思路里面提到了很多外部工具，mysqldump、mongoimport、mongoexport 等，如何去调用系统命令是个问题。可以通过 Runtime.exec方法去实现在Java程序中开辟另一个系统进程去执行系统命令。当然 Linux 和 Windows 下是有命令的写法略微区别的。写了一个 CmdUtil工具类，封装了在 Windows 和 Linux 不同的cmd执行方法，[戳这里看源码](https://github.com/DomBro96/dataimport/blob/master/src/main/java/cn/dombro/dataimport/util/CmdUtil.java)。


<h3 id="5.2">5.2 MongoDB的使用</h3>

之前从来没有接触过 MongoDB ，到网上查一下发现MongoDB的使用比MySql简单太多了。
首先导入依赖，唯一的依赖

```
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver</artifactId>
    <version>3.5.0</version>
</dependency>
```

MongDB 的所有操作都是基于 MongoClient 对象，于是写了一个工具类 MongoDbUtil，[戳这里看源码](https://github.com/DomBro96/dataimport/blob/master/src/main/java/cn/dombro/dataimport/util/MongoDbUtil.java)，当然只是基本的操作，没有涉及到CRUD。


<h3 id="5.3">5.3 对 XLSX2CSV 的修改</h3>

在使用 apche-XLSX2CSV类 时，出现在转换 csv 成功后无法删除 xlsx 文件的情况，开始以为可能做测试类的地方对文件有占用，可是把测试类删掉后还是无法删除 xlsx 文件。猜测可能是在转换之后文件后，对 xlsx 的输入流没有关闭，于是添加 close方法 关闭资源。在 XLSX2CSV 对象每次使用 process 进行转换操作后在内部调用 close，xlsx 文件就可以删除了。 

```
//转换之后关闭资源
public void close() {
  try {
         this.xlsxPackage.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.output.close();
    }
```



<h3 id="5.4">5.4 MySql导入MongoDB导出csv的坑</h3>


实现思路中里面提到了 [MySql 导入 csv](#3.1) 是需要指定"记录之间的换行符"，为了实现跨平台，我指定的换行为Java提供的 `System.lineSeparator()` 即当前系统默认换行符(Linux为 '\n'，Windows为'\r\n') 。可是在**导入 MongoDB 导出的 csv 文件时，MySql 每次都只能导入一条记录**，我一瞧这肯定是换行惹的祸，因为 MongoDB 导出的 csv 文件是完整的。后来使用 Notepad++ 显示 MongDB导出 csv 全部字符，发现每行结尾 赫然一个 **LF** (也就是 "\n") 在 Windows 下面 换行是 '\r\n' 。解决方案是在 MySql 导入 MongoDB 导出的 csv 时使用 '\n' 导入每条记录，[戳这里看源码,MySqlDAOImp.csvImportByLf 方法](https://github.com/DomBro96/dataimport/blob/master/src/main/java/cn/dombro/dataimport/dao/MysqlDAOImp.java)。 



<h3 id="5.5">5.5 编码问题</h3>

编码问题是一个很容易忽略却最为致命的问题。四个功能模块中都会涉及到，这是一个很细节的问题。因为 Excel 默认编码为 GBK ，但数据库默认编码为 UTF-8（也可以自己设置），为了统一编码决定使用 UTF-8。那一些细节的处理就需要注意，比如 XLSX2CSV 和 XLS2CSV 中 将
`private final String OUTPUT_CHARSET = "GBK" `
修改为
`private final String OUTPUT_CHARSET = "utf-8";`

这样导入 数据库中的 csv 文件就为 UTF-8 编码了。


<h2 id="6">6. 有待改善</h2>

还有很多不满意的地方,有时间会优化以下代码。

- Cron4j 定时任务

为了删除用户下载后的文件，使用了 Cron4j 定时任务框架，但我认为将 Cron4j的操作放在 Service 层中很不规范，[Cron4j 中文文档](https://github.com/youyinnn/Cron4jTranslation)。


- CmdUtil 调用系统命令

在 Java 程序中调用本地程序会破坏平台的独立性规则，尤其是使用 Runtime.exec() 这种方式，有点不负责任。


- 代码耦合性

代码具有一定耦合性。

 
- 命名规范

项目命名有不规范的地方。


<h2 id="7">7. 感谢的话</h2>

首先感谢上帝，磨磨蹭蹭，写了一个月，需求分析，概要设计，详细设计，编码阶段...每个阶段都是锻炼自己和站在巨人肩膀上编程的过程。
特别感谢：

参与需求讨论、给予前端支持和意见指导的[youyinnn](https://github.com/youyinnn)。

参与需求讨论与功能设计[caoler](https://github.com/caoler)。

给予原型设计的[IntJames](https://github.com/IntJames)。



<h2 id="8">8. 注意</h2>

由于安全需要，git 版本屏蔽了 /dataimport/src/recourse/ 下的 config.properties 和 mongodb.properties 文件。想要 clone 的同学需要自己手动新建这两个文件。模板如下

- config.properties

```
user=your_mysql_user
password=your_mysql_password
driverClass=com.mysql.jdbc.Driver
jdbcUrl=jdbc:mysql://your_host:your_port/data_import?characterEncoding=utf8&useSSL=false
initialSize=10
maxActive=50
minIdle=5
maxWait=5000
mysqldatabase=data_import
sockfile=your_mysql_sockfile
```

- mongodb.properties


```
host=your_host
port=your_port
database=data_import
```
















