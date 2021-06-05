package at.xirado.jdautils.commands;

public enum CommandFlag
{

    DEVELOPER_ONLY(), // Only the developer can execute the command. (Set with CommandHandlerBuilder.setOwnerID())
    DISABLED(), // Command is disabled and cannot be executed
    PRIVATE_GUILD_COMMAND() // Command is disabled by default and needs to be whitelisted using Command.addAllowedGuilds()

}
