package at.xirado.jdautils.slashcommand;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.Checks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class SlashCommand
{
    private final CommandData commandData;
    private final Set<Long> enabledGuilds = new HashSet<>();
    private boolean global = true;

    public SlashCommand(String name, String description)
    {
        commandData = new CommandData(name, description);
    }

    public void setGlobal(boolean global)
    {
        this.global = global;
    }

    public boolean canBeExecuted(long guildID)
    {
        if(isGlobal()) return true;
        return enabledGuilds.contains(guildID);
    }

    public void addEnabledGuilds(Long... guildIDs)
    {
        Checks.notNull(guildIDs, "Guild IDs");
        enabledGuilds.addAll(Arrays.asList(guildIDs));
    }

    public void addOption(OptionData... optionData)
    {
        commandData.addOptions(optionData);
    }

    public void addOption(OptionType type, String name, String description, boolean required)
    {
        commandData.addOption(type, name, description, required);
    }

    public void addOption(OptionType type, String name, String description)
    {
        commandData.addOption(type, name, description);
    }

    public boolean isGlobal()
    {
        return global;
    }

    public Set<Long> getEnabledGuilds()
    {
        return enabledGuilds;
    }

    public CommandData getCommandData()
    {
        return commandData;
    }

    public abstract void executeSlashCommand(SlashCommandEvent event);
}
