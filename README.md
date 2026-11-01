# OceanNeedle-Java 食用指南

---

## 鸣谢

---

本项目参考了 [@Jessamine](https://github.com/zengxinyueooo) 大佬的开源项目[🔗ThoughtCoding](https://github.com/zengxinyueooo/ThoughtCoding)，感谢大佬开源，开源万岁🤚😭🤚

## 简介

---

这是一个基于 Java 语言实现的图书馆管理系统CLI，目前有学生端和管理员双端，学生和管理员端都可以对书籍进行基本查询操作，而管理员可以完成对书籍的添加、状态更新、删除，除此之外，系统还有历史记录功能，学生和管理员都能查询

|               功能               |             实现             |
| :------------------------------: | :--------------------------: |
|     学生查询、借阅、归还书籍     |              ✅              |
| 管理员查询、更新、新增、删除书籍 |              ✅              |
|            开发者模式            |          ⚠️待实现          |
|           历史记录查询           |              ✅              |
|             错误处理             |              ✅              |
|             权限约束             |              ✅              |
|     持久化登录 (Remember Me)     |              ✅              |
|           登录失效检查           |              ✅              |
|      命令补全 命令历史记录      |              ✅              |
|             命令指南             |              ✅              |
|              跨平台              | ❌仅 macOS  其它平台未测试 |

## 依赖 / 技术栈

---

- `lombok` --> 简化函数构造，让实体类更好看
- `junit` --> 单元测试，测试功能实现是否达预期效果
- `mySQL` --> 数据库，操作数据的基础
- `myBatis` --> 简化数据库的连接、访问等实现代码，加快开发流程，提高项目可维护性
- `h2database` --> 一个由 Java 编写的开源关联式资料库引擎，用于数据库功能测试
- `jline` --> 一个跨平台的 Java 库，专门用来处理控制台与命令列介面的输入与输出
- `jakarta-validation` --> Java 数据校验
- `hibernate-validator` --> 提供声明式的数据校验机制
- `glassfish` --> 提供一个标准、稳定且全功能的运行环境
- `logback` --> 日志管理和储存
- `slf4j` --> 日志门面
- `fastjson2` --> json 文件读写

## 项目结构

---

```
.
├── app.zsh         # 🛞jar包启动脚本
├── 📁 logs         # 📝日志存放
├── pom.xml         # 📦maven包管理
├── README.md       # 📝项目简介
├── 📁 src          # 📄源码
│   ├── 📁 test         # 🔍测试集
│   └── 📁 main         # 🏠主程序源码
│       ├── 📁 resources        # 🧺配置、文档、资源文件存放处
│       │   ├── 📁 com/xietee/mapper      # 🗺️mybatis  数据映射实现层
│       │   │   ├── AdminMapper.xml       # 管理员    数据映射实现层
│       │   │   ├── BookMapper.xml        # 书籍     数据映射实现层
│       │   │   ├── RecordMapper.xml      # 历史记录  数据映射实现层
│       │   │   └── StudentMapper.xml     # 学生     数据映射实现层
│       │   ├── 📁 description         # 📄命令使用文档存放处
│       │   │   ├── all.txt            # 所有命令的简短介绍
│       │   │   ├── help.txt           # ...
│       │   │   └── (...).txt           # ...
│       │   ├── jdbc.properties             # ⚙️数据库连接配置文件( 此文件不会被 git 追踪 )
│       │   ├── jdbc.properties.example     # 📄git 提交模板
│       │   ├── logback.xml                 # ⚙️logback 日志输出格式配置文件
│       │   ├── mybatis-config.xml          # ⚙️mybatis 配置文件( 注册 mapper + 启动 mySQL 业务模式 )
│       │   └── user.json                   # 📋存放用户信息( 登录持久化的关键 )
│       └── 📁 java/com/xietee
│           ├── Main.java       # 🛞应用启动程序
│           ├── 📁 cli          # 💻命令行界面处理层( 前端 )
│           │   ├── App.java        # 🚪应用主循环入口
│           │   ├── 📁 cmd                  # 🚩命令逻辑实现
│           │   │   ├── add.java            # add          --新增操作
│           │   │   ├── borrow.java         # borrow       --书籍借阅操作
│           │   │   ├── clear.java          # clear        --清屏
│           │   │   ├── Cmd.java            # Cmd          --提供命令接口( 定义命令解析、执行、参数重置的接口 )
│           │   │   ├── del.java            # del          --删除操作
│           │   │   ├── help.java           # help         --打印命令使用文档
│           │   │   ├── history.java        # history      --打印借阅历史记录
│           │   │   ├── login.java          # login        --登录操作
│           │   │   ├── ls.java             # ls           --打印所有对象( 学生 书籍 ... )
│           │   │   ├── quit.java           # quit         --退出程序
│           │   │   ├── ret.java            # ret          --书籍归还操作
│           │   │   ├── search.java         # search       --模糊搜索
│           │   │   └── update.java         # update       --更新操作
│           │   ├── Command.java        # 🔍命令行拆分、解析
│           │   └── UserType.java       # 🙎‍♂️用户类型枚举类
│           ├── 📁 common           # 📋数据规范
│           │   └── Result.java     # 🍎数据结果规范
│           ├── 📁 dao                      # 🗺️数据访问层( 映射接口 )
│           │   ├── AdminMapper.java        # 管理员映射接口
│           │   ├── BookMapper.java         # 书籍映射接口
│           │   ├── RecordMapper.java       # 历史记录映射接口
│           │   └── StudentMapper.java      # 学生映射接口
│           ├── 📁 dto                          # 📋请求规范
│           │   ├── BookSaveDTO.java            # 书籍数据更新请求规范
│           │   ├── BookUpdateDTO.java          # 书籍数据更新请求规范
│           │   ├── RecordSaveDTO.java          # 历史记录数据请求规范
│           │   ├── StudentSaveDTO.java         # 学生数据新增请求规范
│           │   └── StudentUpdateDTO.java       # 学生数据更新请求规范
│           ├── 📁 entity               # 🏀实体类
│           │   ├── Admin.java          # 🤵管理员
│           │   ├── Book.java           # 📚书籍
│           │   ├── Developer.java      # 🧑‍💻开发者( 可能弃用 需重新设计 )
│           │   ├── Record.java         # 📋历史记录
│           │   └── Student.java        # 🧑‍🎓学生
│           ├── 📁 exception                        # ⚠️错误处理层
│           │   ├── BusinessException.java          # 业务异常
│           │   ├── BusinessExceptionEnum.java      # 错误/状态码
│           │   ├── LoginException.java             # 登录异常
│           │   └── StatusCode.java                 # 状态码接口
│           ├── 📁 service                      # 💼服务层( 业务处理层 )
│           │   ├── AdminService.java           # 管理员业务处理
│           │   ├── DeveloperService.java       # 开发者业务处理
│           │   ├── LoginService.java           # 登录业务处理
│           │   └── StudentService.java         # 学生业务处理
│           ├── 📁 ui                       # ✨用户界面实现
│           │   ├── AnsiColors.java         # 🌈ansi 编码颜色枚举类
│           │   ├── AppUI.java              # 📦IO封装
│           │   ├── ConsoleWriter.java      # 🖨️命令行输出
│           │   ├── InputReader.java        # 📝命令行输入
│           │   └── StatusBar.java          # 🔔状态栏输出
│           └── 📁 util                         # 🛠️工具类
│               ├── numberUtils.java            # 数字处理
│               ├── Pair.java                   # pair 类( 让方法能返回两个或多个值 )
│               ├── TimeUtils.java              # 时间处理
│               └── ValidationUtils.java        # 数据校验启动
│  
├── 📁 target
└── TESTING.md
```

## 如何运行

---

- 由于我的开发平台是 mac，其他平台（windows、 linux） 没有测试过
- 还没有写打包工具，但你可以尝试 clone 我的仓库，然后运行启动脚本

  ```zsh
  git clone https://github.com/xieteeQAQ/OceanNeedle-java.git
  zsh ./app.zsh
  ```
- 如果运行不了的话，只能自行打包了（不如问问神奇的 ai 大人！？）

## 已知问题

---

- 当用户自行中断程序时，抛出的异常未捕获
- 命令文档路径写死在代码中，当文件结构发生变动时，当前程序不会忽略异常而是一路上抛到`Main.java`
- （可能还有会发现，或者我忘了的问题......）

## 怎么做出这样的诗山的

---

如你所见，这个项目叫 OceanNeedle-java，我还有一个 C++ 版本的，你可以在我的项目里找到它 [🔗一个链接\_OceanNeedle](https://github.com/xieteeQAQ/OceanNeedle)，C++ 版本开发于 2026 年 8 月，这个版本并不是一个图书馆管理系统，它只是我一个猎奇想法的实现，我想把我大学认识的学生和老师全部塞进数据库里，掌控人际关系💦，现在这个项目应该不开发了，但本项目依然叫 OceanNeedle 是我的执念。开发 C++ 版本时，我基本全部靠 DeepSeek教我，因为那时我没写过数据库，所以我就学到了怎么操作数据库，怎么搭建框架，怎么规范代码结构，这是本项目结构比较规范的原因

现在开始设计程序架构。把所有东西都往 Main 里塞是毁灭性的做法，要让项目有维护性，必须把代码实现全部分类，做业务的放业务层，做美化的放 UI 层，做数据呈现的放界面处理层，做数据访问的就放映射层......之后看见文件夹的名字，java 类的名字，就能联想出这一部分是干什么的，就不用再一个文件里东翻西找的了。那么把每一层的文件夹建好后，该去设计 mysql 的表结构了，我需要设计一个学生的学号长度应该是多少，密码长度限制应为多少，唯一字段该定为哪个，书籍的 id 是否应该作为外键加入学生的表里？一个好的表结构能避免很多麻烦，比如要查找历史记录时，怎么让学生端只看到自己的记录？我的做法是将学生的学号作为外键加入历史记录的表中，在设计查询函数时我就可以直接传入一个存储学号的字符串，让 MyBatis 底层拿着这个学号去条件查询，即可达到预期效果，先这样通过外键将多表联系起来的做法本项目不止一处；然而在 MySQL 的表结构中我其实并没还有启用外键功能:

**_历史记录的表结构_**

```MySQL
CREATE TABLE `record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `studentID` char(12) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ISBN` char(13) COLLATE utf8mb4_unicode_ci NOT NULL,
  `borrowTime` datetime DEFAULT CURRENT_TIMESTAMP,
  `returnTime` datetime DEFAULT NULL,
  `bookID` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
```

并没有 foreign 字样，那我为啥不用呢，因为物理外键不好管理，当你某一天突发奇想需要修改表结构的时候，地狱时刻来了，你可能需要小心翼翼地解除外键关系，小心翼翼地修改，再小心翼翼地接回去，反正一个不小心就会报一堆错，而且，大厂也不喜欢用物理外键，他们的理由更多，本项目我的表只储存一个特殊索引，作为一个其他表的名片来使用，实际上每个表没有任何物理联系，只有名义上的联系。表结构设计好了不代表可以一劳永逸，我们很难预料到开发过程中会出现什么问题，需要我们修改表结构，但我们一开始就能做的，就是留足够多的后路

然后就开始正真设计应用层了。我们是服务于用户的，一个好看的 UI 是必不可少的，用户要的是体验，丝毫不在意你写代码用了什么范式( 除非...卧槽，用户彻底怒了😱 )，所以写几个 UI 类吧。一开始我想实现的是 TUI，可以用鼠标操作的命令终端，但我发现要实现这样的效果要学的东西不仅多，还比较复杂，我也没写过，直接这么做是不理智的，更何况核心代码是我自己来搓，不使用 AI。那为什么不用 AI 帮我搓？本项目中，AI 只用于帮我写配置文件和测试集，还有给我提意见，在 main/java 目录下的核心源码纯靠我手搓，说到底，这是一个学习项目，全给 AI 托管了，我学滚木吗，而且全给 AI 托管了，就变成 vibecoding 了，谁来维护？全给 AI 意淫了。因此我退而求其次做 CLI，一是我写过，二是好写( 好像是同一回事 )。 说回 UI，`AnsiColors.java`负责枚举 ansi 编码的颜色字符串，`ConsoleWriter.java`负责打印，`InputReader.java`负责输入，`StatusBar.java`负责打印状态栏，`AppUI.java`就负责将上面的功能封装起来，给`App.java`调用，其中`AppUI.java`还有获取终端对象，设置命令补全的功能。然后去实现 dao 层，我需要尽可能地提供基础的增删改查接口，不管以后会不会用到，然后是服务层，在这里就是调用增删改查的接口，还有异常抛出的处理，同时实现双端服务功能分离。同时在 dto 层实现请求规范，对象的什么属性是可以添加的，什么是不能更新的，全都规范起来，作为一种请求协议。再往上就是应用层了，`App.java`包含了程序的主循环，所有逻辑都在这里进行

**_`App.java`的部分代码_**

```java
Result<Cmd> parseResult = command.parseAndGetCommand(line);
                    if (parseResult.code() == 200) {
                        cmd = parseResult.data();
                        var executeResult = cmd.execute(this);
                        cmd.reset();
                        if (executeResult.code() == 200) {
                            ui.barPrintSuccess(executeResult.message());
                        } else {
                            ui.barPrintWarning(executeResult.message());
                        }
                    } else {
                        ui.barPrintWarning(parseResult.message());
                    }
```

然后是 CLI 最核心的命令，`Command.java`负责将用户输入的命令行拆分成一个链表，然后对参数进行第一步解析：链表第一个元素是命令的名字，剩下都是参数，将第一个元素与存放已有命令的哈希表匹配，如果存在就返回这个命令的对象，进行第二次参数解析，这一次解析交给命令对象本身，解析如果通过了，就返回命令对象和参数给`App.java`，`App.java`调用对象接口执行命令并处理执行结果。说到命令对象，就是`cmd`这个文件夹里存放的 java 类，所有命令对象都是接口`Cmd.java`的实现，它们不仅存放着命令的具体逻辑，还存放着参数对应的属性，这些属性在参数解析的过程中被赋值，命令解析完成后重置为默认值。这种将每个命令都做成一个类的设计我参考了 Linux，不过我其实不知道 Linux 具体是不是类似这样的实现，我学得不深，大概了解到 linux 的每个命令的 c文件 都存放在系统中，是可以被找到的

**_命令的第一次解析_**

```Java
public Result<Cmd> parseAndGetCommand(String line) {

        List<String> parsedLine;

        if (line.isEmpty()) {
            return Result.success(NONE);
        }

        parsedLine = Arrays.stream(line.split(" ")).toList();

        String commandName = "";
        List<String> args = new ArrayList<>();

        for (int i = 0; i < parsedLine.size(); i++) {
            if (i == 0) {
                commandName = parsedLine.get(i);
            } else {
                args.add(parsedLine.get(i));
            }
        }

        Optional<Cmd> cmdOptional = getCommand(commandName);
        if (cmdOptional.isEmpty()) {
            return Result.warn(COMMAND_ERROR);
        }

        var parseResult = cmdOptional.get().parse(args);
        if (parseResult.code() != 200) {
            return Result.warn(parseResult.code(), parseResult.message());
        }

        return Result.success(cmdOptional.get());
    }
```

除此之外，异常处理我除了自定义异常之外，还用了错误码/状态码，它们用枚举来实现`BusinessExceptionEnum.java`。还有其他的什么参数校验，时间处理，数字处理，数据可视化，持久化登录之类的我暂时不详细赘述了

说说心态吧，我是一个大一新生，专业是计算机科学与技术，每天都忙得莫名奇妙的，一门课结课了还要我来写篇论文，写一篇报告，还有很多莫名其妙的破事......如果累了，或者说觉得完成了一点小功能，我也会停下来刷刷视频，刷一刷崩铁的每日之类的，休息不是罪过，是好文明。我还是愿意用很多时间来开发这个项目，可能是我确实很喜欢学习计算机，喜欢创造一些东西吧，把学业，就业什么乱七八糟的抛到九霄之外后，我只是在干我喜欢，能让我感到快乐的事，即使编程一点也不轻松。人生不是一个目标，而是一种体验，我将这句我自己做的汤，送给每一位活在当下的人✨

---

_本项目仅供学习_
