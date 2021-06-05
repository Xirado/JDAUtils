package at.xirado.jdautils.commands;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class CommandHandler implements EventListener
{


    private static final Logger LOGGER = JDALogger.getLog(CommandHandler.class);

    private final ConcurrentMap<String, Command> registeredCommands = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setNameFormat("Command Worker %d").build());
    private final String prefix;
    private Long ownerID;

    public CommandHandler(String prefix)
    {
        this.prefix = prefix;
    }

    public void setOwnerID(Long ownerID)
    {
        Checks.isSnowflake(String.valueOf(ownerID), "OwnerID");
        this.ownerID = ownerID;
    }

    public Long getOwnerID()
    {
        return ownerID;
    }

    public CommandHandler registerCommand(Command command)
    {
        String name = command.getName();
        if(registeredCommands.containsKey(name))
        {
            LOGGER.error("Command \""+name+"\" could not be registered because a command (or alias) with this name already exists!");
            return this;
        }
        registeredCommands.put(name, command);
        if(command.getAliases() != null && command.getAliases().size() >= 1)
        {
            for (String alias : command.getAliases())
            {
                if(registeredCommands.containsKey(alias))
                {
                    LOGGER.error("Alias \""+alias+"\" could not be registered because a command (or alias) with this name already exists!");
                    continue;
                }
                registeredCommands.put(alias, command);
            }
        }
        return this;
    }

    /**
     * Returns all registered commands
     * @return all registered commands
     */
    public List<Command> getRegisteredCommands()
    {
        return registeredCommands.values().stream().distinct().collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns all commands accessible in a guild
     * @param guildID the id of the guild
     * @return immutable list containing the commands
     */
    public List<Command> getRegisteredCommands(long guildID)
    {
        return registeredCommands
                .values()
                .stream().distinct()
                .filter(x -> x.isAvailableIn(guildID))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Gets all guild only commands for this guild.
     * This is similar to {@link CommandHandler#getRegisteredCommands(long)}, but it returns only guild commands.
     * @param guildID the guild id
     * @return a list of all guild only commands
     */
    public List<Command> getGuildCommands(long guildID)
    {
        return registeredCommands
                .values()
                .stream().distinct()
                .filter(x -> x.hasCommandFlag(CommandFlag.PRIVATE_GUILD_COMMAND) && x.getAllowedGuilds().contains(guildID))
                .collect(Collectors.toUnmodifiableList());
    }



    @Override
    public void onEvent(@NotNull GenericEvent event)
    {
        if(!(event instanceof GuildMessageReceivedEvent)) return;
        GuildMessageReceivedEvent guildEvent = (GuildMessageReceivedEvent) event;
        Runnable r = () ->
        {
            try
            {
                CommandArgument arguments = new CommandArgument(guildEvent.getMessage().getContentRaw(), guildEvent.getGuild().getIdLong(), prefix);
                String name = arguments.getCommandName();
                if(!registeredCommands.containsKey(name)) return;
                Command command = registeredCommands.get(name);
                if(command.hasCommandFlag(CommandFlag.PRIVATE_GUILD_COMMAND))
                {
                    if(!command.getAllowedGuilds().contains(guildEvent.getGuild().getIdLong())) return;
                }

                if(command.hasCommandFlag(CommandFlag.DEVELOPER_ONLY))
                {
                    if(guildEvent.getMember().getIdLong() != ownerID) return;
                }

                if(command.hasCommandFlag(CommandFlag.DISABLED))
                {
                    if(guildEvent.getMember().getIdLong() != ownerID) return;
                }

                command.executeCommand(guildEvent, arguments);
            }catch(Exception ex)
            {
                LOGGER.error("An error occured whilst executing command", ex);
            }
        };
        executorService.submit(r);
    }



}
