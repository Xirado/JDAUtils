# JDAUtils

### Simple API that makes adding commands in JDA very easy

# How to use

### To create a command, create a new class extending `Command` (not the one from JDA)
```java
public class EchoCommand extends Command
{
    public EchoCommand()
    {
        super("echo", "let the bot say something", "echo [Text]");
        setAliases("parrot", "speak", "say");
    }

    @Override
    public void executeCommand(@NotNull GuildMessageReceivedEvent event, @NotNull CommandArgument args)
    {
        String content = args.toString();
        event.getChannel().sendMessage(content).queue();
    }
}
```
This example command called `echo` makes the bot say what you want.

* `args.toString()` returns everything that comes after the command. e.g. `!say Hello` will return `Hello`.
* If you want to get the content after a certain index, use `args.toString(int)`.
### Registering the command
Create a new `CommandHandler` object with your desired prefix using the `CommandHandlerBuilder`
```java
CommandHandler commandHandler = new CommandHandlerBuilder(";;")
    .setOwnerID(184654964122058752L)
    .registerCommands(new EchoCommand())
    .build();
```

Then, you can tell JDA to register the `CommandHandler` using `JDA.addEventListener()` or `JDABuilder.addEventListeners()`, passing the object.

* Note: Every command runs on an ExecutorService by default, you can choose your own by using `CommandHandlerBuilder.setExecutor()`. This means that every command runs asynchronously

# Download
[![GitHub release](https://img.shields.io/github/release/Xirado/JDAUtils.svg)](https://GitHub.com/Xirado/JDAUtils/releases/)

#### Note: Replace `%VERSION%` with your desired version (or the hash of the commit you want).

## Gradle
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```
```gradle
dependencies {
    implementation 'com.github.Xirado:JDAUtils:%VERSION%'
}
```

## Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.Xirado</groupId>
    <artifactId>JDAUtils</artifactId>
    <version>%VERSION%</version>
</dependency>
```
