# JDAUtils

### Simple API that makes adding commands and slash-commands in JDA very easy

# How to use
## Default commands
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
## Slash commands
### Slash commands are the modern way of creating commands on Discord.
### To create a slash command, create a new Class that extends `SlashCommand`.
```java
import at.xirado.jdautils.slashcommand.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class EchoCommand extends SlashCommand
{
    public EchoCommand()
    {
        super("echo", "let the bot say something");
        addOption(OptionType.STRING, "text", "the text", true);
    }

    @Override
    public void executeSlashCommand(SlashCommandEvent event)
    {
        event.reply(event.getOption("text").getAsString()).queue();
    }
}
```
### This example slash-command makes the bot say what you want.
* To understand the behaviour of slash-commands, please read the [Wiki](https://github.com/DV8FromTheWorld/JDA/wiki/Interactions) of JDA.
### Registering the slash-command
Create a new `SlashCommandHandler` object and instantiate your command like in the example below.
```java
SlashCommandHandler slashCommandHandler = new SlashCommandHandlerBuilder()
        .registerSlashCommands(new EchoCommand())
        .build();
```
Then, you can tell JDA to register the `SlashCommandHandler` using `JDA.addEventListener()` or `JDABuilder.addEventListeners()`, passing the object.
### Note: Global commands take up to an hour to update. For testing, you can make a slash-command a guild-command using `setGlobal(false)`, then you can whitelist your guild with `addAllowedGuilds()`. Since guild-commands update instantly, you don't have to wait up to an hour. Keep in mind that Discord only allows 200 Guild command updates per day per guild.
* Using the method `setDebugGuildID(long guildID)` on the `SlashCommandHandlerBuilder` object, you can automatically make every global command a guild command. With this, you don't have to do the step above.
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
