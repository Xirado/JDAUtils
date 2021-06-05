import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class EchoCommand extends Command
{
    public EchoCommand()
    {
        super("echo", "let the bot say something", "echo [Text]");
        setAliases("parrot", "speak", "say");
    }

    @Override
    public void executeCommand(@NotNull GuildMessageReceivedEvent event, @NotNull CommandArgument args)
    {
        String content = args.toString();
        event.getChannel().sendMessage(content).queue();
    }
}
