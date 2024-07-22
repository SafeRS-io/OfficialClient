package net.runelite.client.plugins.safers.zulrahoverlay;

import com.google.common.base.Preconditions;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.safers.zulrahoverlay.overlays.*;
import net.runelite.client.plugins.safers.zulrahoverlay.rotationutils.RotationType;
import net.runelite.client.plugins.safers.zulrahoverlay.rotationutils.ZulrahData;
import net.runelite.client.plugins.safers.zulrahoverlay.rotationutils.ZulrahPhase;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
		name = "<html><font color=#3076DF>[SafeRS]</font> Zulrah</html>",
		description = "All-in-One tool to help automatically do zulrah",
		tags = {"zulrah", "zul", "andra", "snakeling", "ported", "snake"},
		enabledByDefault = false
)
public class ZulrahPlugin extends Plugin implements KeyListener
{
	@Inject
	private Client client;
	@Inject
	private KeyManager keyManager;

	@Inject
	private InfoBoxManager infoBoxManager;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private housePoolOverlay housePoolOverlay;
	@Inject
	private ZulrahSmokeCloudsOverlay zulrahSmokeCloudsOverlay;
	@Inject
	private InstanceTimerOverlay instanceTimerOverlay;
	@Inject
	private PhaseOverlay phaseOverlay;
	@Inject
	private PrayerHelperOverlay prayerHelperOverlay;
	@Inject
	private PrayerMarkerOverlay prayerMarkerOverlay;






	@Inject
	private BoostTagger boostTagger;

	@Inject
	private DuelRingOverlay duelRingOverlay;

	private LootOverlay lootOverlay;
	@Inject
	private PrayerPotionOverlay prayerPotionOverlay;
	@Inject
	private TeleportScrollOverlay teleportScrollOverlay;


	@Inject
	private VenomTagger venomTagger;

	@Inject
	private ZulrahBankOverlay bankOverlay;

	@Inject
	private FairyRingObjectOverlay fairyRingObjectOverlay;
	@Inject
	private FairyRingObjectOverlay fairyRingObjectOverlay2;
	@Inject
	private ZulrahBoatOverlay zulrahBoatOverlay;

	@Inject
	private WorldPointMarkerOverlay worldPointMarkerOverlay;

	@Inject
	private ZulrahComboFoodOverlayLowHP zulrahComboFoodOverlayLowHP;

	@Inject
	private ZulrahFoodOverlayLowHP zulrahFoodOverlayLowHP;

	@Inject
	private WorldPointMinimapOverlay worldPointMinimapOverlay;

	@Inject
	private SceneOverlay sceneOverlay;

	@Inject
	private PoolOverlay poolOverlay;

	@Inject
	private fairyringoverlay fairyringoverlay;

	@Inject
	private recoiloverlay recoiloverlay;
	@Inject
	private TeleBoxOverlay teleBoxOverlay;
	@Inject
	private ZulrahPanelOverlay zulrahPanelOverlay;

	@Inject
	private ZulrahConfig config;

	@Inject
	ConfigManager configManager;
	private static final Set<Integer> ZULRAH_REGION_IDS = Set.of(9007,9008);

	private final Map<LocalPoint, Integer> itemLocations = new HashMap<>();

	private final List<Integer> targetItemIds = Arrays.asList(537,12934,12922,12932,12927,6571,13200, 13201,1392,1391,1149,6967,3204,560,563,562,3001,268,2999,270,5289,5288,5290,5316,5296,5300,5303,5304,5317,6290,392,5953,9193,1988,5975,1939,561,2363,12936,12938); // Your target item IDs

	private NPC zulrahNpc = null;
	private boolean inZulrahRegion;
	private int stage = 0;
	private int phaseTicks = -1;
	private int attackTicks = -1;
	private int totalTicks = 0;
	private RotationType currentRotation = null;
	private List<RotationType> potentialRotations = new ArrayList<RotationType>();
	private final Map<LocalPoint, Integer> projectilesMap = new HashMap<LocalPoint, Integer>();
	private final Map<GameObject, Integer> toxicCloudsMap = new HashMap<GameObject, Integer>();
	private static boolean flipStandLocation = false;
	private static boolean flipPhasePrayer = false;
	private static boolean zulrahReset = false;
	private final Collection<NPC> snakelings = new ArrayList<NPC>();
	private boolean holdingSnakelingHotkey = false;
	private Counter zulrahTotalTicksInfoBox;

	private final BiConsumer<RotationType, RotationType> phaseTicksHandler = (current, potential) ->
	{
		if (zulrahReset) 
		{
			phaseTicks = 38;
		}
		else
		{
			ZulrahPhase p = current != null ? getCurrentPhase((RotationType)((Object)current)) : getCurrentPhase((RotationType)((Object)potential));
			Preconditions.checkNotNull(p, "Attempted to set phase ticks but current Zulrah phase was somehow null. Stage: " + stage);
			phaseTicks = p.getAttributes().getPhaseTicks();
		}
	};

	@Provides
    ZulrahConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ZulrahConfig.class);
	}

	protected void startUp() throws Exception {
		if (client.getGameState() != GameState.LOGGED_IN || !inZulrahRegion())
		{
			overlayManager.add(zulrahPanelOverlay);
			lootOverlay = new LootOverlay(client, itemLocations, config);
			overlayManager.add(fairyRingObjectOverlay);
			overlayManager.add(fairyRingObjectOverlay2);
			overlayManager.add(housePoolOverlay);
			overlayManager.add(lootOverlay);
			overlayManager.add(teleBoxOverlay);
			overlayManager.add(bankOverlay);
			overlayManager.add(zulrahBoatOverlay);
			overlayManager.add(poolOverlay);
			overlayManager.add(zulrahSmokeCloudsOverlay);
			overlayManager.add(worldPointMinimapOverlay);
			overlayManager.add(fairyringoverlay);
			overlayManager.add(worldPointMarkerOverlay);
			overlayManager.add(recoiloverlay);
			return;
		}
		init();
	}
	
	private void init()
	{
		inZulrahRegion = true;
		overlayManager.add(zulrahPanelOverlay);
		overlayManager.add(housePoolOverlay);
		overlayManager.add(teleBoxOverlay);
		lootOverlay = new LootOverlay(client, itemLocations, config);

		overlayManager.add(lootOverlay);

		overlayManager.add(bankOverlay);
		overlayManager.add(zulrahBoatOverlay);
		overlayManager.add(poolOverlay);
		overlayManager.add(instanceTimerOverlay);
		overlayManager.add(phaseOverlay);
		overlayManager.add(prayerHelperOverlay);
		overlayManager.add(prayerMarkerOverlay);
		overlayManager.add(sceneOverlay);
		overlayManager.add(zulrahSmokeCloudsOverlay);
		overlayManager.add(worldPointMinimapOverlay);
		overlayManager.add(fairyringoverlay);
		overlayManager.add(fairyRingObjectOverlay);
		overlayManager.add(fairyRingObjectOverlay2);
		overlayManager.add(worldPointMarkerOverlay);

		keyManager.registerKeyListener(this);
	}

	protected void shutDown() 
	{
		inZulrahRegion = false;
		reset();
		overlayManager.remove(instanceTimerOverlay);
		overlayManager.remove(phaseOverlay);
		overlayManager.remove(teleBoxOverlay);
		overlayManager.remove(prayerHelperOverlay);
		overlayManager.remove(prayerMarkerOverlay);
		overlayManager.remove(sceneOverlay);
		overlayManager.remove(zulrahPanelOverlay);
		overlayManager.remove(housePoolOverlay);
		lootOverlay = new LootOverlay(client, itemLocations, config);
		overlayManager.remove(zulrahSmokeCloudsOverlay);
		overlayManager.remove(lootOverlay);
		overlayManager.remove(bankOverlay);
		overlayManager.remove(zulrahBoatOverlay);
		overlayManager.remove(poolOverlay);
		overlayManager.remove(worldPointMinimapOverlay);
		overlayManager.remove(fairyringoverlay);
		overlayManager.remove(fairyRingObjectOverlay);
		overlayManager.add(fairyRingObjectOverlay2);
		overlayManager.remove(worldPointMarkerOverlay);
		overlayManager.remove(recoiloverlay);
		keyManager.unregisterKeyListener(this);
	}

	private void reset() 
	{
		zulrahPanelOverlay.setBossSpawned(false);
		zulrahNpc = null;
		stage = 0;
		phaseTicks = -1;
		attackTicks = -1;
		totalTicks = 0;
		currentRotation = null;
		potentialRotations.clear();
		projectilesMap.clear();
		toxicCloudsMap.clear();
		flipStandLocation = false;
		flipPhasePrayer = false;
		instanceTimerOverlay.resetTimer();
		zulrahReset = false;
		clearSnakelingCollection();
		holdingSnakelingHotkey = false;
		handleTotalTicksInfoBox(true);
		log.debug("Zulrah Reset!");
		overlayManager.add(zulrahPanelOverlay);
		lootOverlay = new LootOverlay(client, itemLocations, config);
		overlayManager.add(zulrahSmokeCloudsOverlay);
		overlayManager.add(lootOverlay);
		overlayManager.add(bankOverlay);
		overlayManager.add(zulrahBoatOverlay);
		overlayManager.add(poolOverlay);
		overlayManager.add(worldPointMinimapOverlay);
		overlayManager.add(fairyringoverlay);
		overlayManager.add(fairyRingObjectOverlay);
		overlayManager.add(fairyRingObjectOverlay2);
		overlayManager.add(worldPointMarkerOverlay);
		overlayManager.add(recoiloverlay);

	}

	public void keyTyped(KeyEvent e) 
	{
	
	}

	public void keyPressed(KeyEvent e) 
	{
		if (config.snakelingSetting() == ZulrahConfig.SnakelingSettings.MES && config.snakelingMesHotkey().matches(e))
		{
			holdingSnakelingHotkey = true;
		}
	}

	public void keyReleased(KeyEvent e) 
	{
		if (config.snakelingSetting() == ZulrahConfig.SnakelingSettings.MES && config.snakelingMesHotkey().matches(e))
		{
			holdingSnakelingHotkey = false;
		}
	}
	
	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		GameState gameState = event.getGameState();
		
		switch (gameState)
		{
			case LOGGED_IN:
				if (inZulrahRegion())
				{
					if (!inZulrahRegion)
					{
						init();
					}
				}
				else
				{
					if (inZulrahRegion)
					{
						reset();
					}
				}
				break;
			case HOPPING:
			case LOGIN_SCREEN:
				if (inZulrahRegion)
				{
					shutDown();
				}
				break;
			default:
				break;
		}
	}


	@Subscribe
	public void onItemSpawned(ItemSpawned event) {
		if (targetItemIds.contains(event.getItem().getId())) {
			LocalPoint location = event.getTile().getLocalLocation();
			if (!itemLocations.containsKey(location)) {
				itemLocations.put(location, 1);
			} else {
				int currentCount = itemLocations.get(location);
				itemLocations.put(location, currentCount + 1);
			}
		}
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned event) {
		if (targetItemIds.contains(event.getItem().getId())) {
			LocalPoint location = event.getTile().getLocalLocation();
			if (itemLocations.containsKey(location)) {
				int currentCount = itemLocations.get(location);
				if (currentCount > 1) {
					itemLocations.put(location, currentCount - 1);
				} else {
					itemLocations.remove(location);
				}
			}
		}
	}

	@Provides
	List<TileObject> provideTileObjectList(Client client) {
		// Replace this with your logic to obtain the list of TileObjects you want to highlight
		List<TileObject> tileObjects = new ArrayList<>();
		// Add TileObjects to the list
		return tileObjects;
	}




	@Subscribe
	private void onConfigChanged(ConfigChanged event) throws IOException {


		if (event.getGroup().equalsIgnoreCase("znzulrah")) 
		{
			switch (event.getKey()) 
			{
				case "snakelingSetting":
					if (config.snakelingSetting() != ZulrahConfig.SnakelingSettings.ENTITY)
					{
						clearSnakelingCollection();
					}
					if (config.snakelingSetting() == ZulrahConfig.SnakelingSettings.MES)
					{
						break;
					}
					holdingSnakelingHotkey = false;
					break;
				case "totalTickCounter":
					if (config.totalTickCounter())
					{
						break;
					}
					handleTotalTicksInfoBox(true);
				default:
					break;
			}
		}
	}

	private void clearSnakelingCollection() 
	{
		snakelings.forEach(npc -> ZulrahPlugin.setHidden(npc, false));
		snakelings.clear();
	}

	@Subscribe
	private void onClientTick(ClientTick event) 
	{
		if (!inZulrahRegion || client.getGameState() != GameState.LOGGED_IN || zulrahNpc == null)
		{
			return;
		}
		if (config.snakelingSetting() == ZulrahConfig.SnakelingSettings.ENTITY)
		{
			snakelings.addAll(client.getNpcs().stream().filter(npc -> npc != null && npc.getName() != null && npc.getName().equalsIgnoreCase("snakeling") && npc.getCombatLevel() == 90).collect(Collectors.toList()));
			snakelings.forEach(npc -> ZulrahPlugin.setHidden(npc, true));
		}
	}

	@Subscribe
	private void onGameTick(GameTick event) throws IOException {
		if (!inZulrahRegion || client.getGameState() != GameState.LOGGED_IN || zulrahNpc == null)
		{
			return;
		}
		++totalTicks;
		if (attackTicks >= 0) 
		{
			--attackTicks;
		}
		if (phaseTicks >= 0) 
		{
			--phaseTicks;
		}
		if (projectilesMap.size() > 0) 
		{
			projectilesMap.values().removeIf(v -> v <= 0);
			projectilesMap.replaceAll((k, v) -> v - 1);
		}
		if (toxicCloudsMap.size() > 0) 
		{
			toxicCloudsMap.values().removeIf(v -> v <= 0);
			toxicCloudsMap.replaceAll((k, v) -> v - 1);
		}
		handleTotalTicksInfoBox(false);
	}

	@Subscribe
	private void onAnimationChanged(AnimationChanged event) 
	{
		if (!inZulrahRegion || !(event.getActor() instanceof NPC))
		{
			return;
		}
		NPC npc = (NPC)((Object)event.getActor());
		if (npc.getName() != null && !npc.getName().equalsIgnoreCase("zulrah")) 
		{
			return;
		}
		switch (npc.getAnimation()) 
		{
			case 5071:
				zulrahNpc = npc;
				instanceTimerOverlay.setTimer();
				potentialRotations = RotationType.findPotentialRotations(npc, stage);
				phaseTicksHandler.accept(currentRotation, potentialRotations.get(0));
				log.debug("New Zulrah Encounter Started");
				zulrahPanelOverlay.setBossSpawned(true);
				break;
			case 5073:
				++stage;
				if (currentRotation == null) 
				{
					potentialRotations = RotationType.findPotentialRotations(npc, stage);
					currentRotation = potentialRotations.size() == 1 ? potentialRotations.get(0) : null;
				}
				phaseTicksHandler.accept(currentRotation, potentialRotations.get(0));
				break;
			case 5072:
				if (zulrahReset) 
				{
					zulrahReset = false;
				}
				if (currentRotation == null || !isLastPhase(currentRotation))
				{
					break;
				}
				stage = -1;
				currentRotation = null;
				potentialRotations.clear();
				snakelings.clear();
				flipStandLocation = false;
				flipPhasePrayer = false;
				zulrahReset = true;
				log.debug("Resetting Zulrah");
				break;
			case 5069:
				attackTicks = 4;
				if (currentRotation != null) {
					ZulrahPhase currentPhase = getCurrentPhase(currentRotation);
					if (currentPhase != null && currentPhase.getZulrahNpc().isJad()) {
						flipPhasePrayer = !flipPhasePrayer;
						// Call the update method on the EDT
					}
				}
				break;
			case 5806:
			case 5807:
				attackTicks = 8;
				flipStandLocation = !flipStandLocation;
				break;
			case 5804:
				reset();
				break;
			default:
				break;
		}
	}

	@Subscribe
	private void onFocusChanged(FocusChanged event) 
	{
		if (!inZulrahRegion)
		{
			return;
		}
		if (!event.isFocused()) 
		{
			holdingSnakelingHotkey = false;
		}
	}

	@Subscribe
	private void onMenuEntryAdded(MenuEntryAdded event) 
	{
		if (!inZulrahRegion || config.snakelingSetting() != ZulrahConfig.SnakelingSettings.MES || zulrahNpc == null || zulrahNpc.isDead())
		{
			return;
		}
		if (!holdingSnakelingHotkey && event.getTarget().contains("Snakeling") && event.getOption().equalsIgnoreCase("attack")) 
		{
			NPC npc = client.getCachedNPCs()[event.getIdentifier()];
			if (npc == null) 
			{
				return;
			}
			client.setMenuEntries(Arrays.copyOf(client.getMenuEntries(), client.getMenuEntries().length - 1));
		}
	}

	@Subscribe
	private void onProjectileMoved(ProjectileMoved event) 
	{
		if (!inZulrahRegion || zulrahNpc == null)
		{
			return;
		}
		Projectile p = event.getProjectile();
		switch (p.getId()) 
		{
			case 1045:
			case 1047:
				projectilesMap.put(event.getPosition(), p.getRemainingCycles() / 30);
				break;
			default:
				break;
		}
	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event)
	{
		if (!inZulrahRegion || zulrahNpc == null)
		{
			return;
		}
		GameObject obj = event.getGameObject();
		if (obj.getId() == 11700)
		{
			toxicCloudsMap.put(obj, 30);
		}
	}

	@Nullable
	private ZulrahPhase getCurrentPhase(RotationType type) 
	{
		return stage >= type.getZulrahPhases().size() ? null : type.getZulrahPhases().get(stage);
	}

	@Nullable
	private ZulrahPhase getNextPhase(RotationType type) 
	{
		return isLastPhase(type) ? null : type.getZulrahPhases().get(stage + 1);
	}

	private boolean isLastPhase(RotationType type) 
	{
		return stage == type.getZulrahPhases().size() - 1;
	}

	public Set<ZulrahData> getZulrahData()
	{
		LinkedHashSet<ZulrahData> zulrahDataSet = new LinkedHashSet<ZulrahData>();
		if (currentRotation == null) 
		{
			potentialRotations.forEach(type -> zulrahDataSet.add(new ZulrahData(getCurrentPhase((RotationType)((Object)type)), getNextPhase((RotationType)((Object)type)))));
		}
		else
		{
			zulrahDataSet.add(new ZulrahData(getCurrentPhase(currentRotation), getNextPhase(currentRotation)));
		}
		return zulrahDataSet.size() > 0 ? zulrahDataSet : Collections.emptySet();
	}

	private void handleTotalTicksInfoBox(boolean remove) 
	{
		if (remove) 
		{
			infoBoxManager.removeInfoBox(zulrahTotalTicksInfoBox);
			zulrahTotalTicksInfoBox = null;
		}
		else if (config.totalTickCounter())
		{
			if (zulrahTotalTicksInfoBox == null) 
			{
				zulrahTotalTicksInfoBox.setTooltip("Total Ticks Alive");
				infoBoxManager.addInfoBox(zulrahTotalTicksInfoBox);
			}
			else
			{
				zulrahTotalTicksInfoBox.setCount(totalTicks);
			}
		}
	}

	private static void setHidden(Renderable renderable, boolean hidden) 
	{
		Method setHidden = null;
		try
		{
			setHidden = renderable.getClass().getMethod("setHidden", Boolean.TYPE);
		}
		catch (NoSuchMethodException e) 
		{
			log.debug("Couldn't find method setHidden for class {}", renderable.getClass());
			return;
		}
		try
		{
			setHidden.invoke(renderable, hidden);
		}
		catch (IllegalAccessException | InvocationTargetException e) 
		{
			log.debug("Couldn't call method setHidden for class {}", renderable.getClass());
		}
	}

	public NPC getZulrahNpc() 
	{
		return zulrahNpc;
	}

	public int getPhaseTicks() 
	{
		return phaseTicks;
	}

	public int getAttackTicks() 
	{
		return attackTicks;
	}

	public RotationType getCurrentRotation() 
	{
		return currentRotation;
	}


	public static boolean isFlipStandLocation() 
	{
		return flipStandLocation;
	}

	public static boolean isFlipPhasePrayer() 
	{
		return flipPhasePrayer;
	}

	public static boolean isZulrahReset() 
	{
		return zulrahReset;
	}



	private boolean inZulrahRegion()
	{
		for (final int regionId : client.getMapRegions())
		{
			if (ZULRAH_REGION_IDS.contains(regionId))
			{
				return true;
			}
		}
		return false;
	}
}
