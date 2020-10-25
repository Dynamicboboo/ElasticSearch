## 1、ELK的安装的准备

### 1.1 ELK的下载地址

ElasticSearch: https://mirrors.huaweicloud.com/elasticsearch/?C=N&O=D

logstash: https://mirrors.huaweicloud.com/logstash/?C=N&O=D

可视化界面elasticsearch-head.https://github.com/mobz/elasticsearch-head

kibana: https://mirrors.huaweicloud.com/kibana/?C=N&O=D

ik分词器 https://github.com/medcl/elasticsearch-analysis-ik

**jdk必须是1.8及以上的版本**

### 1.2 elk安装 参考

https://blog.csdn.net/mgdj25/article/details/105740191

#### **ElasticSearch**

```tex
安装：
     1 解压，
     2 bin
     3 启动
目录：
     bin 启动文件
     config 配置文件
         log4j2 日志配置文件
         jvm.options java 虚拟机相关的配置
         elasticsearch.yml elasticsearch 的配置文件！ 默认 9200 端口！ 跨域！
         lib 相关jar包
         logs 日志！
         modules 功能模块
         plugins 插件！

```

****

访问http://localhost:9200/

![image-20201025204552756](upload/image-20201025204552756.png)

#### **kibana**

1. kibana的国际化设置 yml中设置成zh-CN

2. elasticsearch\config\elasticsearch文件添加

   ```shell
   http.cors.enabled: true
   http.cors.allow-origin: "*" 
   ```

3. 分词器下载后，修改里面的pom文件修改对应es的版本，放到es的plugins的ik目录下（创建一个），放进去，进到ik目录下

   ![img](http://markdown.xiaonainiu.top/img/_16704.png)

   

4. 这边特别提醒一下分词的粒度设置，比如一个分词只是将一个词的单体拆分，“李逵哈哈”，可能只是将李逵两个都分开了，并没有分成“李逵”这个词，那就要自己去写字典了

   在ik分词下的config目录下新增一个 “xxx.dic”

   把自己的dic 添加到配置中 IKAnalyzer.cfg.xml中

   增加一个(里面很多用法 都有注释)

   ```xml
   <!-- 用户可以在这扩展自己的用户字典-->  
   <entry key="ext_dict">xxx.dic</entry>
   ```



#### **elasticsearch-head**

1.下载，要和elastic版本对应

2.解压

3.启动 npm run start

4.访问http://localhost:9100

![image-20201025204511273](upload/image-20201025204511273.png)

## 2、ES核心概念

```
集群，节点，索引，类型，文档，分片，映射是什么？
```

> elasticsearch是面向文档，关系型数据库和elasticsearch客观的对比！一切都是json

| Relational DB      | Elasticsearch   |
| ------------------ | --------------- |
| 数据库（database） | 索引（indices） |
| 表（tables）       | types           |
| 行（rows）         | documents       |
| 字段（columns）    | fields          |

物理设计：

elasticsearch在后台把每个索引划分成多个分片。每个分片可以在集群中的不同服务器间迁移

逻辑设计：

一个索引类型中，抱哈an多个文档，当我们索引一篇文档时，可以通过这样的一个顺序找到它：索引-》类型-》文档id，通过这个组合我们就能索引到某个具体的文档。注意：`ID不必是整数，实际上它是一个字符串。`

### 文档

> 文档

就是我们的一条条的记录

之前说elasticsearch是面向文档的,那么就意味着索弓和搜索数据的最小单位是文档, elasticsearch中,文档有几个重要属性:

- 自我包含, - -篇文档同时包含字段和对应的值,也就是同时包含key:value !
- 可以是层次型的，-一个文档中包含自文档,复杂的逻辑实体就是这么来的! {就是一 个json对象! fastjson进行自动转换!}
- 灵活的结构,文档不依赖预先定义的模式,我们知道关系型数据库中,要提前定义字段才能使用,在elasticsearch中,对于字段是非常灵活的,有时候,我们可以忽略该字段,或者动态的添加一个新的字段。

尽管我们可以随意的新增或者忽略某个字段,但是,每个字段的类型非常重要,比如一一个年龄字段类型,可以是字符串也可以是整形。因为elasticsearch会保存字段和类型之间的映射及其他的设置。这种映射具体到每个映射的每种类型,这也是为什么在elasticsearch中,类型有时候也称为映射类型。

### 类型

> 类型

类型是文档的逻辑容器,就像关系型数据库一样,表格是行的容器。类型中对于字段的定 义称为映射,比如name映射为字符串类型。我们说文档是无模式的 ,它们不需要拥有映射中所定义的所有字段,比如新增一个字段,那么elasticsearch是怎么做的呢?elasticsearch会自动的将新字段加入映射,但是这个字段的不确定它是什么类型, elasticsearch就开始猜,如果这个值是18 ,那么elasticsearch会认为它是整形。但是elasticsearch也可能猜不对 ，所以最安全的方式就是提前定义好所需要的映射,这点跟关系型数据库殊途同归了,先定义好字段,然后再使用,别整什么幺蛾子。

### 索引

> 索引

就是数据库!

索引是映射类型的容器, elasticsearch中的索引是一个非常大的文档集合。索|存储了映射类型的字段和其他设置。然后它们被存储到了各个分片上了。我们来研究下分片是如何工作的。

**物理设计:节点和分片如何工作**

一个集群至少有一 个节点,而一个节点就是一-个elasricsearch进程 ,节点可以有多个索引默认的,如果你创建索引,那么索引将会有个5个分片( primary shard ,又称主分片)构成的,每一个主分片会有-一个副本( replica shard ,又称复制分片）

![在这里插入图片描述](http://markdown.xiaonainiu.top/img/20200828224136138.png)

上图是一个有3个节点的集群,可以看到主分片和对应的复制分片都不会在同-个节点内,这样有利于某个节点挂掉了,数据也不至于丢失。实际上, 一个分片是- -个Lucene索引, -一个包含倒排索引的文件目录,倒排索引的结构使得elasticsearch在不扫描全部文档的情况下,就能告诉你哪些文档包含特定的关键字。不过,等等,倒排索引是什么鬼?

### 倒排索引

> 倒排索引

elasticsearch使用的是一种称为倒排索引 |的结构,采用Lucene倒排索作为底层。这种结构适用于快速的全文搜索，一个索引由文
档中所有不重复的列表构成,对于每一个词,都有一个包含它的文档列表。 例如,现在有两个文档，每个文档包含如下内容:

```shell
Study every day， good good up to forever  # 文 档1包含的内容
To forever, study every day，good good up  # 文档2包含的内容
12
```

为为创建倒排索引,我们首先要将每个文档拆分成独立的词(或称为词条或者tokens) ,然后创建一一个包含所有不重 复的词条的排序列表,然后列出每个词条出现在哪个文档:

| term    | doc_1 | doc_2 |
| ------- | ----- | ----- |
| Study   | √     | x     |
| To      | x     | x     |
| every   | √     | √     |
| forever | √     | √     |
| day     | √     | √     |
| study   | x     | √     |
| good    | √     | √     |
| every   | √     | √     |
| to      | √     | x     |
| up      | √     | √     |

现在，我们试图搜索 to forever，只需要查看包含每个词条的文档

| term    | doc_1 | doc_2 |
| ------- | ----- | ----- |
| to      | √     | x     |
| forever | √     | √     |
| total   | 2     | 1     |

两个文档都匹配,但是第一个文档比第二个匹配程度更高。如果没有别的条件,现在,这两个包含关键字的文档都将返回。
再来看一个示例,比如我们通过博客标签来搜索博客文章。那么倒排索引列表就是这样的一个结构:

| 博客文章(原始数据) | 博客文章(原始数据) | 索引列表(倒排索引) | 索引列表(倒排索引) |
| ------------------ | ------------------ | ------------------ | ------------------ |
| 博客文章ID         | 标签               | 标签               | 博客文章ID         |
| 1                  | python             | python             | 1，2，3            |
| 2                  | python             | linux              | 3，4               |
| 3                  | linux，python      |                    |                    |
| 4                  | linux              |                    |                    |

如果要搜索含有python标签的文章,那相对于查找所有原始数据而言，查找倒排索引后的数据将会快的多。只需要查看标签这一栏,然后获取相关的文章ID即可。完全过滤掉无关的所有数据,提高效率!

elasticsearch的索引和Lucene的索引对比

在elasticsearch中，索引(库)这个词被频繁使用,这就是术语的使用。在elasticsearch中 ,索引被分为多个分片,每份分片是-个Lucene的索引。**所以一个elasticsearch索引是由多 个Lucene索引组成的**。别问为什么,谁让elasticsearch使用Lucene作为底层呢!如无特指，说起索引都是指elasticsearch的索引。

接下来的一切操作都在kibana中Dev Tools下的Console里完成。基础操作!

### ik分词器

> 什么是IK分词器 ?

分词:即把一-段中文或者别的划分成一个个的关键字,我们在搜索时候会把自己的信息进行分词,会把数据库中或者索引库中的数据进行分词,然后进行一个匹配操作,默认的中文分词是将每个字看成一个词,比如“我爱狂神”会被分为"我",“爱”,“狂”,“神” ,这显然是不符合要求的,所以我们需要安装中文分词器ik来解决这个问题。

如果要使用中文,建议使用ik分词器!

IK提供了两个分词算法: **ik_ smart和ik_ max_ word** ,其中ik_ smart为最少切分, ik_ max_ _word为最细粒度划分!一会我们测试!

什么是IK分词器：

- 把一句话分词
- 如果使用中文：推荐IK分词器
- 两个分词算法：ik_smart（最少切分），ik_max_word（最细粒度划分）

**【ik_smart】测试：**

```shell
GET _analyze
{
  "analyzer": "ik_smart",
  "text": "我是工程师"
}


//输出结果
{
  "tokens" : [
    {
      "token" : "我",
      "start_offset" : 0,
      "end_offset" : 1,
      "type" : "CN_CHAR",
      "position" : 0
    },
    {
      "token" : "是",
      "start_offset" : 1,
      "end_offset" : 2,
      "type" : "CN_CHAR",
      "position" : 1
    },
    {
      "token" : "工程师",
      "start_offset" : 2,
      "end_offset" : 5,
      "type" : "CN_WORD",
      "position" : 2
    }
  ]
}

```

**【ik_max_word】测试：**

```shell
GET _analyze
{
  "analyzer": "ik_max_word",
  "text": "我是工程师"
}
//输出
{
  "tokens" : [
    {
      "token" : "我",
      "start_offset" : 0,
      "end_offset" : 1,
      "type" : "CN_CHAR",
      "position" : 0
    },
    {
      "token" : "是",
      "start_offset" : 1,
      "end_offset" : 2,
      "type" : "CN_CHAR",
      "position" : 1
    },
    {
      "token" : "工程师",
      "start_offset" : 2,
      "end_offset" : 5,
      "type" : "CN_WORD",
      "position" : 2
    },
    {
      "token" : "工程",
      "start_offset" : 2,
      "end_offset" : 4,
      "type" : "CN_WORD",
      "position" : 3
    },
    {
      "token" : "师",
      "start_offset" : 4,
      "end_offset" : 5,
      "type" : "CN_CHAR",
      "position" : 4
    }
  ]
}
```

## 3、命令模式的使用

### 3.1 Rest风格说明

一种软件架构风格，而不是标准。更易于实现缓存等机制

| method | url地址                                         | 描述                   |
| ------ | ----------------------------------------------- | ---------------------- |
| PUT    | localhost:9200/索引名称/类型名称/文档id         | 创建文档(指定文档id)   |
| POST   | localhost:9200/索引名称/类型名称                | 创建文档（随机文档id） |
| POST   | localhost:9200/索引名称/类型名称/文档id/_update | 修改文档               |
| DELETE | localhost:9200/索引名称/类型名称/文档id         | 删除文档               |
| GET    | localhost:9200/索引名称/类型名称/文档id         | 通过文档id查询文档     |
| POST   | localhost:9200/索引名称/类型名称/_search        | 查询所有的数据基础测试 |

#### 创建一个索引

PUT /索引名/类型名(高版本都不写了，都是_doc)/文档id

![image-20201025205908340](upload/image-20201025205908340.png)

此时查看elasticsearch里面是否插入成功

![image-20201025210012833](upload/image-20201025210012833.png)

那么name这个字段用不用指定类型呢

![image-20201025210337349](upload/image-20201025210337349.png)

指定字段的类型properties 就比如sql创表

获得这个规则！可以通过GET请求获得具体的信息

![image-20201025210550366](upload/image-20201025210550366.png)

如果自己不设置文档字段类型，那么es会自动给默认类型

![image-20201025210710981](upload/image-20201025210710981.png)

#### 修改索引

1.修改我们可以还是用原来的PUT的命令，根据id来修改

![image-20201025210925408](upload/image-20201025210925408.png)

但是如果没有填写的字段 会重置为空了 ，相当于java接口传对象修改，如果只是传id的某些字段，那其他没传的值都为空了。

2.还有一种update方法 这种不设置某些值 数据不会丢失

```shell
POST /test3/_doc/1/_update
{
  "doc":{
    "name":"212121"
  }
}

//下面两种都是会将不修改的值清空的

POST /test3/_doc/1
{
    "name":"212121"
}

POST /test3/_doc/1
{
  "doc":{
    "name":"212121"
  }
}
```

#### 删除索引

关于删除索引或者文档的操作

![image-20201025211152885](upload/image-20201025211152885.png)

通过DELETE命令实现删除，根据你的请求来判断是删除索引还是删除文档记录

使用RESTFUL的风格是我们ES推荐大家使用的！

#### 查询

最简单的搜索是GET

搜索功能search

![image-20201025211544070](upload/image-20201025211544070.png)




