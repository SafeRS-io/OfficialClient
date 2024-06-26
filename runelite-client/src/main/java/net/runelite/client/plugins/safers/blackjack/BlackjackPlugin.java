package net.runelite.client.plugins.safers.blackjack;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.WildcardMatcher;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;

@PluginDescriptor(
		name = "<html><font color=#3076DF>[SafeRS] </font>Blackjack</html>",
		description = "One Click blackjacking and automation tools",
		tags = {"blackjack", "thieve", "thieving"}
)
public class BlackjackPlugin extends Plugin {
	private static final String SUCCESS_BLACKJACK = "You smack the bandit over the head and render them unconscious.";

	@Inject
	private BlackjackConfig blackjackConfig;



	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ClientThread clientThread;

	@Inject
	private Client client;

	/**
	 * NPCs to highlight
	 */
	@Getter(AccessLevel.PACKAGE)
	private final Set<NPC> highlightedNpcs = new HashSet<>();

	/**
	 * Stores state of if NPC is knocked out or not.
	 */
	@Getter(AccessLevel.PACKAGE)
	private boolean knockedOut = false;

	private int pickpocketCount = 0;

	private NPC selectedNpc;
	public void setSelectedNpc(NPC npc) {
		this.selectedNpc = npc;
	}

	public NPC getSelectedNpc() {
		return selectedNpc;
	}

	private String highlight = "";
	private long nextKnockOutTick = 0;
	private boolean tagOptionPrepared = false;
	private final Map<NPC, Boolean> npcStates = new HashMap<>();
	// Example method to mark an NPC as knocked out
	private void markNpcKnockedOut(NPC npc) {
		npcStates.put(npc, true);
		// Schedule to mark awake later based on your game logic
	}

	// Example method to mark an NPC as awake
	private void markNpcAwake(NPC npc) {
		npcStates.put(npc, false);
	}
	private boolean isNpcAwake(NPC npc) {
		return npcStates.getOrDefault(npc, false) == false;
	}
	@Inject
	private ConfigManager configManager;






	@Provides
    BlackjackConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BlackjackConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{




		clientThread.invoke(() ->
		{
			rebuildAllNpcs();
		});
	}




	@Override
	protected void shutDown() throws Exception
	{



		highlightedNpcs.clear();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN ||
				event.getGameState() == GameState.HOPPING)
		{
			highlightedNpcs.clear();
		}
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) throws IOException {


		rebuildAllNpcs();

	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		final NPC npc = npcSpawned.getNpc();
		final String npcName = npc.getName();

		if (npcName == null)
		{
			return;
		}

		if (WildcardMatcher.matches(highlight, npcName))
		{
			highlightedNpcs.add(npc);
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		final NPC npc = npcDespawned.getNpc();

		highlightedNpcs.remove(npc);
		if (npcDespawned.getNpc().equals(getSelectedNpc())) {
			setSelectedNpc(null);
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) throws IOException {

		Player localPlayer = client.getLocalPlayer();
		List<NPC> npcs = client.getNpcs();

		for (NPC npc : npcs) {
			if (npc.getInteracting() != null && npc.getInteracting().equals(localPlayer)) {
				// This NPC is targeting the player. Likely, it is attacking or about to attack.
				System.out.println("NPC " + npc.getName() + " is attacking me.");
			}
		}

		if (client.getTickCount() >= nextKnockOutTick)
		{
			knockedOut = false;
		}

	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		if (event.getType() == ChatMessageType.SPAM || event.getType() == ChatMessageType.GAMEMESSAGE) {
			String msg = event.getMessage();
			if (msg.contains("You pick the Menaphite's pocket") || msg.contains("You pick the Bandit's pocket")) {
				pickpocketCount++;
			} else if (msg.equals(SUCCESS_BLACKJACK)) {
				knockedOut = true;
				pickpocketCount = 0; // Reset the counter on a successful knockout
				nextKnockOutTick = client.getTickCount() + 4; // Adjust if necessary
			}
		}
	}

	public Color getHighlightColor() {
		return knockedOut ? blackjackConfig.knockedOutStateColor() : blackjackConfig.awakeStateColor();
	}

	public Color getNpcHighlightColor(NPC npc) {
		boolean isKnockedOut = npcStates.getOrDefault(npc, false);
		return isKnockedOut ? blackjackConfig.knockedOutStateColor() : blackjackConfig.awakeStateColor();
	}



	private void rebuildAllNpcs()
	{
		highlightedNpcs.clear();

		if (client.getGameState() != GameState.LOGGED_IN &&
				client.getGameState() != GameState.LOADING)
		{
			// NPCs are still in the client after logging out,
			// but we don't want to highlight those.
			return;
		}

		outer:
		for (NPC npc : client.getNpcs())
		{
			final String npcName = npc.getName();

			if (npcName == null)
			{
				continue;
			}

			if (WildcardMatcher.matches(highlight, npcName))
			{
				highlightedNpcs.add(npc);
				continue outer;
			}
		}
	}

	// Method to process menu entries
	private void processMenuEntries(MenuEntry[] entries, Client client) {
		for (int idx = entries.length - 1; idx >= 0; --idx) {
			// ... existing code ...
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		// Check if the event is for an NPC interaction
		if (!isNpcInteraction(event.getType())) {
			return;
		}
		NPC npc = client.getCachedNPCs()[event.getIdentifier()];
		Player localPlayer = client.getLocalPlayer();
		// Get the NPC from the event

		// Validate the NPC
		if (npc == null || npc.getName() == null || localPlayer == null) {
			return;
		}
		boolean isNpcAttackingPlayer = npc.getInteracting() != null && npc.getInteracting().equals(localPlayer);

		// Check the NPC's animation ID; if it's not 838, deprioritize certain options
		if (npc.getAnimation() != 838 && !isNpcAttackingPlayer) {
			final String option = event.getOption().toLowerCase();
			if (activateKnockout(option)) {
				PrioritizeKnockout(event, npc);
			}
		}
		final String option = event.getOption().toLowerCase();
		if (activatePickpocket(option) && pickpocketCount < 3 && !isNpcAttackingPlayer) {
			PrioritizePickpocket(event, npc);
		}
		if (activatePickpocket(option) && pickpocketCount >= 2 && !isNpcAttackingPlayer) {
			PrioritizeKnockout(event, npc);
		}
		if (activatePickpocket(option) && isNpcAttackingPlayer) {
			PrioritizePickpocket(event, npc);
		}
		if (activatePickpocket(option) && npc.getAnimation() == 395 && isNpcAttackingPlayer) {
			PrioritizePickpocket(event, npc);
		}
		}




	private boolean menuContainsTagOption(MenuEntry[] entries) {
		for (MenuEntry entry : entries) {
			if ("Tag".equals(entry.getOption())) {
				return true; // Found the "Tag" option, no need to add it again.
			}
		}
		return false; // "Tag" option not found.
	}

	@Subscribe
	public void onMenuOpened(MenuOpened event) {
		if (client.getGameState() != GameState.LOGGED_IN) {
			return;
		}


		final Point mousePosition = client.getMouseCanvasPosition();

		// Add a custom menu entry to all highlighted NPCs.
		for (NPC npc : getHighlightedNpcs()) {
			if (npc.getCanvasTilePoly() != null && npc.getCanvasTilePoly().contains(mousePosition.getX(), mousePosition.getY())) {
				client.createMenuEntry(-1)
						.setOption("Tag")
						.setTarget(npc.getName())
						.setType(MenuAction.RUNELITE)
						.setIdentifier(npc.getIndex())
						.setParam0(0)
						.setParam1(0)
						.onClick(consumer -> {
							// Logic to handle NPC tagging goes here
							setSelectedNpc(npc);
						});
			}
		}
	}

	private boolean isNpcInteraction(int menuActionId) {
		// Checks if the menu action is one of the NPC interaction types
		return menuActionId == MenuAction.NPC_FIRST_OPTION.getId() ||
				menuActionId == MenuAction.NPC_SECOND_OPTION.getId() ||
				menuActionId == MenuAction.NPC_THIRD_OPTION.getId() ||
				menuActionId == MenuAction.NPC_FOURTH_OPTION.getId() ||
				menuActionId == MenuAction.NPC_FIFTH_OPTION.getId();
	}

	private boolean activateKnockout(String option) {
		// Determines if the given option should be deprioritized based on your criteria
		String lowerOption = option.toLowerCase();
		return "talk-to".equals(lowerOption) || "pickpocket".equals(lowerOption) || "lure".equals(lowerOption);
	}

	private boolean activatePickpocket(String option) {
		// Determines if the given option should be deprioritized based on your criteria
		String lowerOption = option.toLowerCase();
		return "talk-to".equals(lowerOption) || "lure".equals(lowerOption);
	}

	private void PrioritizeKnockout(MenuEntryAdded event, NPC targetNpc) {
		// Iterate through menu entries and deprioritize specified options for the target NPC
		MenuEntry[] menuEntries = client.getMenuEntries();
		for (MenuEntry entry : menuEntries) {
			if (entry.getIdentifier() == targetNpc.getIndex() && activateKnockout(entry.getOption())) {
				entry.setDeprioritized(true);
			}
		}

		// Update the menu entries to apply the changes
		client.setMenuEntries(menuEntries);
	}

	private void PrioritizePickpocket(MenuEntryAdded event, NPC targetNpc) {
		// Iterate through menu entries and deprioritize specified options for the target NPC
		MenuEntry[] menuEntries = client.getMenuEntries();
		for (MenuEntry entry : menuEntries) {
			if (entry.getIdentifier() == targetNpc.getIndex() && activatePickpocket(entry.getOption())) {
				entry.setDeprioritized(true);
			}
		}

		// Update the menu entries to apply the changes
		client.setMenuEntries(menuEntries);
	}



}
