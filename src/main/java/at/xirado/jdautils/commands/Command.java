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

    public void setAliases(String... aliases)
    {
        Checks.notNull(aliases, "Aliases");
        this.aliases.addAll(Arrays.asList(aliases));
    }

    public void addAllowedGuilds(Long... guildIDs)
    {
        Checks.notNull(guildIDs, "GuildIDs");
        this.allowedGuilds.addAll(Arrays.asList(guildIDs));
    }

    public void setCommandFlags(CommandFlag... commandFlags)
    {
        Checks.notNull(commandFlags, "Commandflags");
        this.commandFlags.addAll(Arrays.asList(commandFlags));
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getUsage()
    {
        return usage;
    }

    public List<String> getAliases()
    {
        return aliases;
    }

    public List<Long> getAllowedGuilds()
    {
        return allowedGuilds;
    }

    public EnumSet<CommandFlag> getCommandFlags()
    {
        return commandFlags;
    }

    public boolean hasCommandFlag(CommandFlag flag)
    {
        return commandFlags.contains(flag);
    }

    public boolean isAvailableIn(long GuildID)
    {
        if(!hasCommandFlag(CommandFlag.PRIVATE_GUILD_COMMAND)) return true;
        return getAllowedGuilds().contains(GuildID);
    }

    public abstract void executeCommand(@Nonnull GuildMessageReceivedEvent event, @Nonnull CommandArgument commandArgument);
}