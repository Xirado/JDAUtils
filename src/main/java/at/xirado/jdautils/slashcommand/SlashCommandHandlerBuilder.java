package at.xirado.jdautils.slashcommand;

import net.dv8tion.jda.internal.utils.Checks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class SlashCommandHandlerBuilder
{
    private Set<SlashCommand> addedSlashCommands = new HashSet<>();
    private ExecutorService executorService;
    private Long debugGuildID;

    /**
     * Enables the debug mode with the specified guild id.
     *
     * This will turn all global slash commands into a guild command.
     *
     * Since global commands take up to an hour to update, and
     * guild commands are instant, this is useful if you're trying
     * to test some things without having to wait that long.
     *
     * <br>Note: Guild command updates are rate-limited to 200
     * command updates/guild/day. If you have lots
     * of commands, register only the ones you need right now!
     *
     * @param guildID the ID of the guild that acts as a "testing"-guild
     */
    public SlashCommandHandlerBuilder setDebugGuildID(long guildID)
    {
        this.debugGuildID = guildID;
        return this;
    }

    public SlashCommandHandlerBuilder setExecutorService(ExecutorService executorService)
    {
        this.executorService = executorService;
        return this;
    }

    public SlashCommandHandlerBuilder registerSlashCommands(SlashCommand... slashCommands)
    {
        Checks.notNull(slashCommands, "Slash commands");
        addedSlashCommands.addAll(Arrays.asList(slashCommands));
        return this;
    }

    public SlashCommandHandler build()
    {
        return new SlashCommandHandler(addedSlashCommands, executorService, debugGuildID);
    }
}
