package at.xirado.jdautils.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public abstract class Command
{
    private final String name;
    private final String description;
    private final String usage;
    private final List<String> aliases;
    private final List<Long> allowedGuilds;
    private final EnumSet<CommandFlag> commandFlags;

    public Command(String name, String description, String usage)
    {
        Checks.notNull(name, "Name");
        Checks.notNull(description, "Description");
        Checks.notNull(usage, "Usage");
        this.name = name;
        this.description = description;
        this.usage = usage;
        aliases = new ArrayList<>();
        allowedGuilds = new ArrayList<>();
        commandFlags = EnumSet.noneOf(CommandFlag.class);
    }

    /**
     * Adds aliases for this command.
     * @param aliases Aliases
     */
    public void setAliases(String... aliases)
    {
        Checks.notNull(aliases, "Aliases");
        this.aliases.addAll(Arrays.asList(aliases));
    }

    /**
     * Adds guilds that are allowed to use this command
     * if {@link CommandFlag#PRIVATE_GUILD_COMMAND} is present.
     * @param guildIDs Guild IDs
     */
    public void addAllowedGuilds(Long... guildIDs)
    {
        Checks.notNull(guildIDs, "GuildIDs");
        this.allowedGuilds.addAll(Arrays.asList(guildIDs));
    }

    /**
     * Adds {@link CommandFlag CommandFlags} to this command.
     * @param commandFlags
     */
    public void setCommandFlags(CommandFlag... commandFlags)
    {
        Checks.notNull(commandFlags, "Commandflags");
        this.commandFlags.addAll(Arrays.asList(commandFlags));
    }

    /**
     * Returns the name of this command
     * @return the name of this command
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the description of this command
     * @return the description of this command
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Returns the usage of this command
     * @return the usage of this command
     */
    public String getUsage()
    {
        return usage;
    }

    /**
     * Returns a list containing all aliases of this command
     * @return a list containing all aliases of this command
     */
    public List<String> getAliases()
    {
        return aliases;
    }

    /**
     * Returns a list containing all whitelisted Guild IDs
     * @return a list containing all whitelisted Guild IDs
     */
    public List<Long> getAllowedGuilds()
    {
        return allowedGuilds;
    }

    /**
     * Returns an EnumSet containing all CommandFlags of this command
     * @return an EnumSet containing all CommandFlags of this command
     */
    public EnumSet<CommandFlag> getCommandFlags()
    {
        return commandFlags;
    }

    /**
     * Returns true, if the command has the passed CommandFlag
     * @param flag The CommandFlag to check for
     * @return true, if the command has the passed CommandFlag
     */
    public boolean hasCommandFlag(CommandFlag flag)
    {
        return commandFlags.contains(flag);
    }

    /**
     * Returns true, if the command can be accessed on this Guild
     * @param GuildID the guilds id
     * @return true, if the command can be accessed on this Guild
     */
    public boolean isAvailableIn(long GuildID)
    {
        if(!hasCommandFlag(CommandFlag.PRIVATE_GUILD_COMMAND)) return true;
        return getAllowedGuilds().contains(GuildID);
    }

    /**
     * This method will be executed on command invoke
     * @param event The GuildMessageReceivedEvent
     * @param commandArgument The CommandArgument
     */
    public abstract void executeCommand(@Nonnull GuildMessageReceivedEvent event, @Nonnull CommandArgument commandArgument);
}
