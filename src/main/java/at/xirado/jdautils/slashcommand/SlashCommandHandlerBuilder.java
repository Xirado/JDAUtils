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

    public SlashCommandHandlerBuilder()
    {

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
        return new SlashCommandHandler(addedSlashCommands, executorService);
    }
}
