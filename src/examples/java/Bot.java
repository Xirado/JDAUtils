import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import javax.security.auth.login.LoginException;

public class ExampleBot
{
    public static void main(String[] args) throws LoginException
    {
        CommandHandler commandHandler = new CommandHandlerBuilder(";;") // Prefix is ;;
                .setOwnerID(184654964122058752L) // This user can execute commands with the DEVELOPER_ONLY CommandFlag
                .registerCommand(new EchoCommand())
                .build();
        JDA jda = JDABuilder.createDefault("token")
                .addEventListeners(commandHandler)
                .build();
    }
}
