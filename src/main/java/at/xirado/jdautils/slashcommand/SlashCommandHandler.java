package at.xirado.jdautils.slashcommand;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SlashCommandHandler implements EventListener
{
    private static final Logger LOGGER = JDALogger.getLog(SlashCommandHandler.class);

    private final ExecutorService executorService;
    private final Set<SlashCommand> guildSlashCommands;
    private final Set<SlashCommand> globalSlashCommands;
    private boolean jdaReady = false;
    private final boolean debug;
    private long debugGuildID;
    private JDA jda;

    protected SlashCommandHandler(Set<SlashCommand> slashCommandSet, ExecutorService executorService, Long debugGuildID)
    {
        debug = debugGuildID != null;
        if(debug) this.debugGuildID = debugGuildID;
        this.executorService = executorService != null ? executorService : Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setNameFormat("SlashCommand Thread %d").build());
        globalSlashCommands = slashCommandSet.stream().filter(SlashCommand::isGlobal).collect(Collectors.toSet());
        guildSlashCommands = slashCommandSet.stream().filter(x -> !x.isGlobal() && x.getEnabledGuilds().size() >= 1).collect(Collectors.toSet());
    }

    @Override
    public void onEvent(@NotNull GenericEvent event)
    {
        if(!jdaReady && !debug && event instanceof ReadyEvent)
        {
            jdaReady = true;
            ReadyEvent readyEvent = (ReadyEvent) event;
            jda = readyEvent.getJDA();
            CommandListUpdateAction commandListUpdateAction = jda.updateCommands();
            for(SlashCommand slashCommand : globalSlashCommands)
            {
                commandListUpdateAction.addCommands(slashCommand.getCommandData());
            }
            commandListUpdateAction.queue();
            return;
        }

        if(event instanceof GuildReadyEvent)
        {
            GuildReadyEvent guildReadyEvent = (GuildReadyEvent) event;
            Guild guild = guildReadyEvent.getGuild();
            Set<SlashCommand> slashCommands = guildSlashCommands.stream()
                    .filter(x -> x.getEnabledGuilds().contains(guild.getIdLong()))
                    .collect(Collectors.toSet());
            if(debug && debugGuildID == guild.getIdLong())
            {
                slashCommands.addAll(globalSlashCommands);
            }
            if(slashCommands.size() >= 1)
            {
                CommandListUpdateAction commandListUpdateAction = guild.updateCommands();
                for(SlashCommand slashCommand : slashCommands)
                {
                    LOGGER.info("Registered command "+slashCommand.getCommandData().getName()+"! (To guild "+guild.getName()+")");
                    commandListUpdateAction.addCommands(slashCommand.getCommandData());
                }
                commandListUpdateAction.queue();
            }
            return;
        }

        if(event instanceof SlashCommandEvent)
        {
            SlashCommandEvent slashCommandEvent = (SlashCommandEvent) event;
            String name = slashCommandEvent.getName();
            SlashCommand slashCommand = globalSlashCommands.stream()
                    .filter(x -> x.getCommandData().getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);
            if(slashCommand != null)
            {
                executorService.submit(() -> slashCommand.executeSlashCommand(slashCommandEvent));
                return;
            }
            if(slashCommandEvent.isFromGuild())
            {
                guildSlashCommands.stream()
                        .filter(x -> x.getCommandData().getName().equalsIgnoreCase(name) && x.canBeExecuted(slashCommandEvent.getGuild().getIdLong()))
                        .findFirst().ifPresent(guildslashCommand -> executorService.submit(() -> guildslashCommand.executeSlashCommand(slashCommandEvent)));
            }
        }
    }
}
