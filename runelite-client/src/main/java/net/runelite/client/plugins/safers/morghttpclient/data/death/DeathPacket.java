package net.runelite.client.plugins.safers.morghttpclient.data.death;


import net.runelite.client.plugins.safers.morghttpclient.data.PacketTypes;

public class DeathPacket
{
	public String packetType = PacketTypes.death.name();

	public String playerName; // Name of the client player.
	public String targetName; // Name of the player who died.
	public int tick;

}
