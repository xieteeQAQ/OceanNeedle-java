# OceanNeedle-Java 食用指南

---

## 鸣谢

---

本项目参考了 [@Jessamine](https://github.com/zengxinyueooo) 大佬的开源项目[🔗ThoughtCoding](https://github.com/zengxinyueooo/ThoughtCoding)，感谢大佬开源，开源万岁🤚😭🤚

## 标签

java, jline, MySQL, MyBatis, CLI, 命令行, 入门, 图书馆管理系统, 广东海洋大学

---

## 简介

---

这是一个基于 Java 语言实现的图书馆管理系统CLI，目前有学生端和管理员双端，学生和管理员端都可以对书籍进行基本查询操作，而管理员可以完成对书籍的添加、状态更新、删除，除此之外，系统还有历史记录功能，学生和管理员都能查询

|               功能               |                实现                |
| :------------------------------: | :--------------------------------: |
|     学生查询、借阅、归还书籍     |                 ✅                 |
| 管理员查询、更新、新增、删除书籍 |                 ✅                 |
|            DEBUG模式            |            ✅(功能较少)            |
|           历史记录查询           |                 ✅                 |
|             错误处理             |                 ✅                 |
|             权限约束             |                 ✅                 |
|     持久化登录 (Remember Me)     |                 ✅                 |
|           登录失效检查           |                 ✅                 |
|      命令补全 命令历史记录      |                 ✅                 |
|             命令指南             |                 ✅                 |
|              跨平台              | ⚠️可跨 windows mac<br />但不稳定 |

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
├── app.zsh         # 🛞jar包启动脚本( macOS / Linux )
├── build.bat       # 📦Windows 11 打包脚本( 需要 JDK 21+ 与 Maven )
├── run.bat         # 🛞Windows 启动脚本( dist 目录或项目根目录都能用 )
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
│           │   ├── Record.java         # 📋历史记录
│           │   └── Student.java        # 🧑‍🎓学生
│           ├── 📁 exception                        # ⚠️错误处理层
│           │   ├── BusinessException.java          # 业务异常
│           │   ├── BusinessExceptionEnum.java      # 错误/状态码
│           │   ├── LoginException.java             # 登录异常
│           │   └── StatusCode.java                 # 状态码接口
│           ├── 📁 service                      # 💼服务层( 业务处理层 )
│           │   ├── AdminService.java           # 管理员业务处理
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

- 由于我的开发平台是 mac，其他平台可能会有问题
- 准备：无论你是哪个平台，都需要下载 java JDK 21+ 才能跑程序，还有 MySQL，不然核心命令（ls search del update ...）是跑不了的，会直接提示"程序异常"， 如果还需要自行打包，则需要 maven，如果都没有下载......这里准备了链接，不知道具体安装步骤的请看网上的教程

  - java JDK 下载: https://www.oracle.com/cn/java/technologies/downloads/
  - MySQL 下载: https://www.mysql.com/cn/products/community/
  - maven 下载: https://learn.microsoft.com/zh-cn/java/openjdk/download
- 然后确保 MySQL 服务启动，在终端执行这条命令

  ```bash
  mysql -u root -p --default-character-set=utf8mb4 < init.sql
  # 然后输入你的用户密码
  ```
- 找到 src/main/resources/jdbc.properties.example 这个配置文件，按提示填好你的数据库信息，然后**删掉.example后缀**

  ```text
  jdbc.driver=com.mysql.cj.jdbc.Driver
  jdbc.url=jdbc:mysql://localhost:3306/<数据库名称>?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
  jdbc.username=<数据库用户名>
  jdbc.password=<数据库密码>
  ```
- **macOS / Linux**：clone 仓库后运行启动脚本 (Linux 还未测试)

  ```zsh
  git clone https://github.com/xieteeQAQ/OceanNeedle-java.git
  cd ./OceanNeedle-java
  zsh ./app.zsh
  ```
- **Windows 11**：

  _方法一_ 直接运行已经打包好的 jar包

  ```bat
  java -jar target/OceanNeedle-java-1.0-SNAPSHOT.jar
  # 如果不行就尝试方法二
  ```

  _方法二_ 通过脚本打包

  需要 JDK 21+ 和 Maven，双击 `build.bat` 一键打包

  > `pom.xml` 里给 `maven-compiler-plugin` 显式配置了 `annotationProcessorPaths`（注册 Lombok）。
  > 因为 **JDK 23 起 javac 不再自动扫描 classpath 上的注解处理器**，不配置的话在 JDK 25/27 上
  > 会编译失败，报一大片「找不到符号」—— `@Data` / `@Getter` 生成的 getter、setter 全部消失。
  >

  ```bat
  build.bat                 # 编译 + 打包 + 组装运行目录 + 生成 zip
  build.bat --skip-tests    # 跳过测试，打包更快
  run.bat                   # 启动（dist 目录或项目根目录都能用）
  ```

  打包产物：

  | 路径                                         | 说明                                           |
  | -------------------------------------------- | ---------------------------------------------- |
  | `target\OceanNeedle-java-1.0-SNAPSHOT.jar` | 可执行 jar，`Class-Path` 指向 `lib/`       |
  | `target\lib\`                              | 运行期依赖，共 15 个 jar                       |
  | `dist\OceanNeedle-win64\`                  | 可直接运行的目录（jar + lib + run.bat + 文档） |
  | `dist\OceanNeedle-win64.zip`               | 分发包                                         |


  > `build.bat` 会检查 JDK 版本、Maven、`jdbc.properties` 是否存在；
  > 缺少 `jdbc.properties` 时会自动从 `jdbc.properties.example` 生成一份，然后停下来等你填密码。
  >

## 已知问题

---

- 命令文档路径写死在代码中，当文件结构发生变动时，当前程序不会忽略异常而是一路上抛到`Main.java`
- `help` 的命令文档和 `user.json` 都是用相对路径 `src/main/resources/...` 读取的，所以**不能在图方便的地方直接 `java -jar`**：`build.bat` 会把 `description/` 和 `user.json` 按同样的目录结构复制进 `dist`，再由 `run.bat` 先切换到该目录启动
- `jdbc.properties` 会被一起打进 jar，里面的明文数据库密码也随之进入 jar / dist，不要把产物发给别人
- `build.bat` / `run.bat` 里的提示只有英文：`.bat` 文件里写中文会让 cmd.exe 按代码页 + 字节偏移解析错位，出现 `'xx' is not recognized as an internal or external command`，所以宁可全用 ASCII
- （可能还有没发现，或者我忘了的问题......）

## 怎么做出这样的诗山的

---

如你所见，这个项目叫 OceanNeedle-java，我还有一个 C++ 版本的，你可以在我的项目里找到它 [🔗一个链接\_OceanNeedle](https://github.com/xieteeQAQ/OceanNeedle)，C++ 版本开发于 2026 年 8 月，这个版本并不是一个图书馆管理系统，它只是我一个猎奇想法的实现，我想把我大学认识的学生和老师全部塞进数据库里，掌控人际关系💦，现在这个项目应该不开发了，但本项目依然叫 OceanNeedle 是我的执念。开发 C++ 版本时，我基本全部靠 蓝色大肥鱼 教我，因为那时我没写过数据库，所以我就学到了怎么操作数据库，怎么搭建框架，怎么规范代码结构，这是本项目结构比较规范的原因

### 架构

现在开始设计程序架构。把所有东西都往 Main 里塞是毁灭性的做法，要让项目有维护性，必须把代码实现全部分类，做业务的放业务层，做美化的放 UI 层，做数据呈现的放界面处理层，做数据访问的就放映射层......之后看见文件夹的名字，java 类的名字，就能联想出这一部分是干什么的，就不用再一个文件里东翻西找的了。

### 建表

那么把每一层的文件夹建好后，该去设计 mysql 的表结构了，我需要设计一个学生的学号长度应该是多少，密码长度限制应为多少，唯一字段该定为哪个，书籍的 id 是否应该作为外键加入学生的表里？一个好的表结构能避免很多麻烦，比如要查找历史记录时，怎么让学生端只看到自己的记录？我的做法是将学生的学号作为外键加入历史记录的表中，在设计查询函数时我就可以直接传入一个存储学号的字符串，让 MyBatis 底层拿着这个学号去条件查询，即可达到预期效果，先这样通过外键将多表联系起来的做法本项目不止一处；然而在 MySQL 的表结构中我其实并没还有启用外键功能:

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

### UI

然后就开始正真设计应用程序了。我们是服务于用户的，一个好看的 UI 是必不可少的，用户要的是体验，丝毫不在意你写代码用了什么范式( 除非...卧槽，用户彻底怒了😱 )，所以写几个 UI 类吧。一开始我想实现的是 TUI，可以用鼠标操作的命令终端，但我发现要实现这样的效果要学的东西不仅多，还比较复杂，我也没写过，直接这么做是不理智的，更何况核心代码是我自己来搓，不使用 AI。

为什么不直接用 AI 帮我搓？本项目中，AI 主要用于帮我写配置文件和测试集，还有给我提意见，在 main/java 目录下的核心源码纯靠我手搓，说到底，这是一个学习项目，全给 AI 托管了，我学了滚木，而且全给 AI 托管了，就变成 vibecoding 了，没人有能力来维护，全给 AI 意淫了。因此我退而求其次做 CLI，一是我写过，二是好写( 好像是同一回事 )。 说回 UI，`AnsiColors.java`负责枚举 ansi 编码的颜色字符串，`ConsoleWriter.java`负责打印，`InputReader.java`负责输入，`StatusBar.java`负责打印状态栏，`AppUI.java`就负责将上面的功能封装起来，给`App.java`调用，其中`AppUI.java`还有获取终端对象，设置命令补全的功能。

`AppUI.java`**_的部分代码_**

```Java
private static final String welcomeTitle = "图书馆管理系统 (author: xieteeQAQ)";

private static final List<String> welcomeOptions = new ArrayList<>(List.of(
        "列出所有        --ls",
        "查询           --search",
        "更新           --update",
        "新增           --add",
        "删除           --del",
        "命令指南        --help"));

// 这个函数打印的就是程序中的 OceanNeedle 横幅
public void showBanner() {
    terminal.writer().println("\n".repeat(2) + AnsiColors.CYAN + BANNER + AnsiColors.RESET);
}

public void showWelcome() {
    consoleWriteTitle(welcomeTitle);
    consoleWriteList(welcomeOptions);
}
```

### 数据访问 & 主循环

然后去实现 dao 层，我需要尽可能地提供基础的增删改查接口，不管以后会不会用到，然后是服务层，在这里就是调用增删改查的接口，还有异常抛出的处理，同时实现双端服务功能分离。同时在 dto 层实现请求规范，对象的什么属性是可以添加的，什么是不能更新的，全都规范起来，作为一种请求协议。再往上就是应用层了，`App.java`包含了程序的主循环，所有逻辑都在这里进行

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

### 命令

然后是 CLI 最核心的命令，`Command.java`负责将用户输入的命令行拆分成一个链表，然后对参数进行第一步解析：链表第一个元素是命令的名字，剩下都是参数，将第一个元素与存放已有命令的哈希表匹配，如果存在就返回这个命令的对象，进行第二次参数解析，这一次解析交给命令对象本身，解析如果通过了，就返回命令对象和参数给`App.java`，`App.java`调用对象接口执行命令并处理执行结果。说到命令对象，就是`cmd`这个文件夹里存放的 java 类，所有命令对象都是接口`Cmd.java`的实现，它们不仅存放着命令的具体逻辑，还存放着参数对应的属性，这些属性在参数解析的过程中被赋值，命令解析完成后重置为默认值。这种将每个命令都做成一个类的设计我参考了 Linux，不过我其实不知道 Linux 具体是不是类似这样的实现，我学得不深，大概了解到 linux 的每个命令的 c 文件 都存放在系统中，是可以被找到的

**_命令接口_**

```Java
public interface Cmd {

    Result<?> execute(App app); // 执行命令的接口

    Result<Void> parse(List<String> args); // 参数解析接口 （这里是命令的第二次解析）

    default void reset() {} // 属性重置接口
}
```

**_命令对象的创建和分配_**

```java
public class Command {

    private static final Map<String, Cmd> GeneralCommandsMap =
            new HashMap<>();
    private static final Map<String, Cmd> StudentCommandsMap =
            new HashMap<>();
    private static final Map<String, Cmd> AdminCommandsMap =
            new HashMap<>();

    public Command() {

        // 通用命令
        GeneralCommandsMap.put("help", new help());
        GeneralCommandsMap.put("quit", new quit());
        GeneralCommandsMap.put("clear", new clear());
        GeneralCommandsMap.put("login", new login());
        GeneralCommandsMap.put("ls", new ls());
        GeneralCommandsMap.put("search", new search());
        GeneralCommandsMap.put("history", new history());

		// 学生专属命令
        StudentCommandsMap.put("borrow", new borrow());
        StudentCommandsMap.put("ret", new ret());

        // 管理员专属命令
        AdminCommandsMap.put("update", new update());
        AdminCommandsMap.put("add", new add());
        AdminCommandsMap.put("del", new del());
    }

    ...
```

**_命令的第一次解析_**

```Java
public Result<Cmd> parseAndGetCommand(String line) {

    List<String> parsedLine;

    if (line.isEmpty()) {
        return Result.success(NONE);
    }

    parsedLine = Arrays.stream(line.split(" ")).toList(); // 以空格分离命令行

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

### 异常

异常处理我除了自定义异常之外，还用了错误码/状态码，它们用枚举来实现`BusinessExceptionEnum.java`

```Java
// 具体业务错误码枚举实现
public enum BusinessExceptionEnum implements StatusCode {

    // 公共状态码
    NONE(200, ""),
    SUCCESS(200, "操作成功"),
    DATA_UNCHANGED(200, "没有数据更新"),
    SYSTEM_ERROR(500, "系统异常"),
    PARAM_ERROR(400, "参数校验失败"),

    // 对象模块错误码
    NO_DATA(2000, "还没有数据"),
    STUDENT_NOT_FOUND(2001, "未查找到学生"),
    STUDENT_ID_EXISTED(2002, "所填学号已存在"),
    STUDENT_CONTRACT_NUMBER_EXISTED(2003, "所填电话号码已存在"),
    BOOK_NOT_FOUND(2004, "未查找到书籍"),
    AVAILABLE_BOOK_NOT_FOUND(2005, "未查找到可借书籍"),
    ALREADY_BORROWED_BOOK(2006, "不可重复借阅同一本书籍"),
    BOOK_UNRETURNED(2007, "该书籍还未归还"),
    UNEXISTED_RECORD(2008, "该记录不存在"),

  	...
```

### 登录持久化

关于登录持久化，我直接用 JSON 序列化来解决，因为这个方法简单粗暴，这个项目比较简单，不用搞得很复杂，每次登录成功时，把唯一字段放进`user.json`，这样启动程序时就不用每次都要登录了，运行程序时，每次解析命令之前都会检查一遍`user.json`，如果文件被修改就会强制重新登录。再声明一下，这个方法简单粗暴，实际业务中不提倡这种方法

```JSON
{
	"name":"作者是弱智",
	"id":"000000000000",
	"userType":"STUDENT"
}
```

### DEBUG模式

DEBUG模式可以在不用切换账号的前提下，开放或关闭权限，例如当前账号是学生账号，输入"debug"，即可获得全部权限，把自己账号删掉都行，所以也是有风险的，在实际业务中，DEBUG 最好藏起来。DEBUG模式开启成功的标志是命令行提示词从"Command"变成"Command!"。DEBUG模式开启状态下，再次输入"debug"即可关闭。只有提示词为"Command > "时才有开启DEBUG模式的功能。

### 困难与应对

开发过程中，我遇到很多我自己解决不了的东西，首先没有 springboot 的无脑自动配置，像 logback mybaits 这样的依赖是需要自己配置的，手写配置很麻烦，参数格式都是不熟悉的，依赖一多起来，管理也会变得很麻烦，我使用这些工具是为了简化开发，不是增添麻烦，所以我要么多多借用浏览器搜索，看看别人的文档，复杂的配置任务，我则交给 AI。

### 心态

说说心态吧，我是一个大一新生，专业是计算机科学与技术，每天都忙得莫名奇妙的，一门课结课了还要我来写篇论文，写一篇报告，还有很多莫名其妙的破事......如果累了，或者说觉得完成了一点小功能，我也会停下来刷刷视频，刷一刷崩铁的每日之类的，休息不是罪过，是好文明。坐在电脑前，把学业，就业什么乱七八糟的抛到九霄之外后，我只是在干我喜欢，能让我感到快乐的事，即使编程一点也不轻松。人生是一种体验，我将这句我自己做的汤，送给每一位活在当下的人✨

---

_本项目仅供学习_
