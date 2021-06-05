package beanapi.commands;

import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandArgument
{

	private static final Logger LOGGER = JDALogger.getLog(CommandArgument.class);
	private String Command;
	private String prefix;
	private String[] args;
	public String getCommandName()
	{
		return Command;
	}
	public String[] toStringArray()
	{
		return args;
	}


	public String toString(int startIndex)
	{
		String[] args = this.args;
		if(args == null)
			return null;
		StringBuilder sb = new StringBuilder();
		for(int i = startIndex; i < args.length; i++)
		{
			sb.append(args[i]).append(" ");
		}
		return sb.toString().trim();
	}

	protected CommandArgument(String argumentString, long guildID, String prefix)
	{
		Checks.notEmpty(argumentString, "arguments");
		Checks.isSnowflake(String.valueOf(guildID), "guildID");
		Checks.notEmpty(prefix, "prefix");
		this.prefix = prefix;
		String[] argumentArray = argumentString.split(" +");
		this.Command = argumentArray[0].substring(prefix.length());
		List<String> arguments = new ArrayList<String>(Arrays.asList(argumentArray).subList(1, argumentArray.length));
		args = new String[arguments.size()];
		arguments.toArray(args);
		
	}
}
