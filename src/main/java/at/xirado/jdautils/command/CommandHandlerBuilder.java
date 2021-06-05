package at.xirado.jdautils.command;

import net.dv8tion.jda.internal.utils.Checks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class CommandHandlerBuilder
{
    private final String prefix;
    private final Set<Command> addedCommands = new HashSet<>();
    private Long ownerID = null;
    private ExecutorService executorService = null;

    public CommandHandlerBuilder(String prefix)
    {
        Checks.notEmpty(prefix, "Prefix");
        this.prefix = prefix;
    }

    /**
     * Used to register commands
     * @param commands Command(s) to register
     * @return current object for chaining convenience
     */
    public CommandHandlerBuilder registerCommands(Command... commands)
    {
        Checks.notNull(commands, "Commands");
        addedCommands.addAll(Arrays.asList(commands));
        return this;
    }

    /**
     * Sets the Owner-ID.
     * Only this user can execute commands flagged with {@link CommandFlag#DEVELOPER_ONLY}.
     * @param ownerID
     * @return
     */
    public CommandHandlerBuilder setOwnerID(Long ownerID)
    {
        Checks.notNull(ownerID, "OwnerID");
        this.ownerID = ownerID;
        return this;
    }

    /**
     * Sets the ExecutorService where all commands run on
     * @param executorService ExecutorService
     * @return current object for chaining convenience
     */
    public CommandHandlerBuilder setExecutorService(ExecutorService executorService)
    {
        Checks.notNull(executorService, "Executor");
        this.executorService = executorService;
        return this;
    }

    /**
     * Builds the CommandHandler
     * @return the CommandHandler
     */
    public CommandHandler build()
    {
        return new CommandHandler(executorService, prefix, addedCommands, ownerID);
    }
}
