package net.runelite.client.plugins.safers.morghttpclient;
import net.runelite.client.config.*;

@ConfigGroup("morghttpclient")
public interface HttpServerConfig extends Config
{
	@ConfigItem(keyName = "MaxObjectDistance", name = "Max Object Distance", description = "Max Distance of object to calculate within range")
	@Range(min = 0, max = 2400)
	default int reachedDistance()
	{
		return 1200;
	}

	@ConfigSection(
			name = "Server",
			description = "Config the server you wish to connect to.",
			position = 1
	)
	String title = "serverTitle";

	@ConfigItem(
			name = "Endpoint",
			description = "The endpoint of the server's API.",
			position = 2,
			keyName = "endpoint",
			section = "serverTitle"
	)
	default String endpoint()
	{
		return "http://localhost:5000/";
	}

	@ConfigItem(
			name = "EnableLogs",
			description = "Enable logging HTTP responses",
			position = 3,
			keyName = "enableLogs",
			section = "serverTitle"
	)
	default boolean enableLogs()
	{
		return false;
	}
}
