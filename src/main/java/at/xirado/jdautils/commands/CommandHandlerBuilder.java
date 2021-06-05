package at.xirado.jdautils.commands;

import net.dv8tion.jda.internal.utils.Checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

public class CommandHandlerBuilder
{
    private final String prefix;
    private final ArrayList<Command> registeredCommands = new ArrayList<>();
    private Long ownerID = null;
    private ExecutorService executorService = null;

    public CommandHandlerBuilder(String prefix)
    {
        Checks.notEmpty(prefix, "Prefix");
        this.prefix = prefix;
    }

    public CommandHandlerBuilder registerCommands(Command... commands)
    {
        Checks.notNull(commands, "Commands");
        registeredCommands.addAll(Arrays.asList(commands));
        return this;
    }

    public CommandHandlerBuilder setOwnerID(Long ownerID)
    {
        Checks.notNull(ownerID, "OwnerID");
        this.ownerID = ownerID;
        return this;
    }

    public CommandHandlerBuilder setExecutorService(ExecutorService executorService)
    {
        Checks.notNull(executorService, "Executor");
        this.executorService = executorService;
        return this;
    }

    public CommandHandler build()
    {
        return new CommandHandler(executorService, prefix, registeredCommands, ownerID);
    }
}
