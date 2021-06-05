package at.xirado.jdautils.command;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CommandHandler implements EventListener
{
    private static final Logger LOGGER = JDALogger.getLog(CommandHandler.class);

    private final ConcurrentMap<String, Command> registeredCommands = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);
    private final ExecutorService executorService;
    private final String prefix;
    private final Long ownerID;

    protected CommandHandler(ExecutorService executorService, String prefix, Collection<Command> commands, Long ownerID)
    {
        this.executorService = executorService != null ? executorService : Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setNameFormat("Command Thread %d").build());
        this.prefix = prefix;
        this.ownerID = ownerID;
        for(Command command : commands)
        {
            registerCommand(command);
        }
    }

    private void registerCommand(Command command)
    {
        String name = command.getName();
        if(registeredCommands.containsKey(name))
        {
            LOGGER.error("Command \""+name+"\" could not be registered because a command (or alias) with this name already exists!");
            return;
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
    }

    /**
     * Returns the User-ID of the owner
     * @return Possibly null-valued User-ID of the owner
     */
    public Long getOwnerID()
    {
        return ownerID;
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
        if(guildEvent.isWebhookMessage() || guildEvent.getAuthor().isBot()) return;
        if(!guildEvent.getMessage().getContentRaw().startsWith(prefix)) return;
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

                if(command.hasCommandFlag(CommandFlag.DISABLED)) return;

                command.executeCommand(guildEvent, arguments);
            }catch(Exception ex)
            {
                LOGGER.error("An error occured whilst executing command", ex);
            }
        };
        executorService.submit(r);
    }
}
