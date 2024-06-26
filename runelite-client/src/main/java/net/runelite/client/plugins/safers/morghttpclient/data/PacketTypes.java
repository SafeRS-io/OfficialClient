package net.runelite.client.plugins.safers.morghttpclient.data;

// collection of packet types so they are used consistently.
// use PacketTypes.packetname.name() for string value.
public enum PacketTypes
{
	unknown, // used for errors/unknown

	hitsplat,
	attacking,
	defending,
	inventory,
	death,
}
