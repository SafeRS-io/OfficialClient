package net.runelite.client.plugins.safers.morghttpclient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Provides;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.safers.morghttpclient.models.AnimationData;
import net.runelite.client.plugins.safers.morghttpclient.pojos.BankItem;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import java.awt.*;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

@PluginDescriptor(
	name = "SafeRS API",
	description = "SafeRS API",
	tags = {"status", "stats"},
	enabledByDefault = true
)
@Slf4j
public class HttpServerPlugin extends Plugin
{
	// Constants
	private static final Duration WAIT = Duration.ofSeconds(5);
	public int MAX_DISTANCE = 1200;

	// Injected dependencies
	@Getter
	@Inject
	public Client client;
	@Inject
	private ItemManager itemManager;
	@Inject
	private OkHttpClient httpClient;
	@Inject
	private Gson gson;
	@Inject
	public HttpServerConfig config;
	@Inject
	public ClientThread clientThread;
	@Inject
	@Getter(AccessLevel.PUBLIC)
	private EventBus eventBus;

	// tracking
	public Skill[] skillList;
	public XpTracker xpTracker;
	public Bank bank;
	public NpcTracker npcTracker;
	public ObjectTracker objectTracker;
	public Skill mostRecentSkillGained;
	public StatusSocketClient slc;
	public HttpServer server;
	public TileTracker tileTracker;

	// Timing and counts
	public int tickCount = 0;
	public long startTime = 0;
	public long currentTime = 0;
	public int lastTickAttacked; // last tick the client player attacked

	// Miscellaneous
	public int[] xp_gained_skills;
	public String msg;
	@Provides
	private HttpServerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HttpServerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{

		slc = new StatusSocketClient(client, httpClient, gson, itemManager, config);

		//MAX_DISTANCE = config.reachedDistance();
		skillList = Skill.values();
		xpTracker = new XpTracker(this);
		tileTracker = new TileTracker(client);

		bank = new Bank(client);
		npcTracker = new NpcTracker(client);
		objectTracker = new ObjectTracker(client);
		server = HttpServer.create(new InetSocketAddress(8081), 0);
		server.createContext("/stats", this::handleStats);
		server.createContext("/inv", handlerForInv(InventoryID.INVENTORY));
		server.createContext("/equip", handlerForInv(InventoryID.EQUIPMENT));
		server.createContext("/events", this::handleEvents);
		server.createContext("/tiles", this::handleTiles);
		server.createContext("/bank", this::handleBank);
		server.createContext("/npcs", this::handleNpcs);
		server.createContext("/objects", this::handleObjects);
		server.createContext("/click", this::handleMouseClick);
		server.createContext("/rightclick", this::handlerightMouseClick);
		server.createContext("/move", this::handleMouseMove);
		server.createContext("/keypress", this::handleKeyPress); // Add new endpoint for keypress
		server.createContext("/keydown", this::handleKeyDown); // New endpoint for key down
		server.createContext("/keyup", this::handleKeyUp); // New endpoint for key up
		server.createContext("/stringInput", this::handleStringInput); // Add new context

		server.setExecutor(Executors.newSingleThreadExecutor());
		startTime = System.currentTimeMillis();
		xp_gained_skills = new int[Skill.values().length];
		int skill_count = 0;
		server.start();
		for (Skill skill : Skill.values())
		{
			if (skill == Skill.OVERALL)
			{
				continue;
			}
			xp_gained_skills[skill_count] = 0;
			skill_count++;
		}
	}

	public void handleKeyPress(HttpExchange exchange) throws IOException {
		if ("POST".equals(exchange.getRequestMethod())) {
			InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			String requestBody = sb.toString();
			JsonObject json = gson.fromJson(requestBody, JsonObject.class);
			int keyCode = json.get("keyCode").getAsInt();

			invokeAndWait(() -> {
				EventQueue.invokeLater(() -> {
					final Canvas target = client.getCanvas();

						long time = System.currentTimeMillis();
						KeyEvent press = new KeyEvent(target, KeyEvent.KEY_PRESSED, time, 0, keyCode, KeyEvent.CHAR_UNDEFINED);
						target.dispatchEvent(press);
						KeyEvent release = new KeyEvent(target, KeyEvent.KEY_RELEASED, time, 0, keyCode, KeyEvent.CHAR_UNDEFINED);
						target.dispatchEvent(release);


				});
				return null;
			});

			exchange.sendResponseHeaders(200, 0);
		} else {
			exchange.sendResponseHeaders(405, 0); // Method Not Allowed
		}
		exchange.close();
	}


	public void handleMouseMove(HttpExchange exchange) throws IOException {
		if ("POST".equals(exchange.getRequestMethod())) {
			InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			String requestBody = sb.toString();
			JsonObject json = gson.fromJson(requestBody, JsonObject.class);
			JsonArray coordinates = json.getAsJsonArray("coordinates");
			int x = coordinates.get(0).getAsInt();
			int y = coordinates.get(1).getAsInt();

			invokeAndWait(() -> {
				EventQueue.invokeLater(() -> {
					final Canvas target = client.getCanvas();
					Point canvasLocation = target.getLocationOnScreen();
					int adjustedX = x - canvasLocation.x;
					int adjustedY = y - canvasLocation.y;

					long time = System.currentTimeMillis();
					MouseEvent move = new MouseEvent(target, MouseEvent.MOUSE_MOVED, time, 0, adjustedX, adjustedY, 0, false, MouseEvent.NOBUTTON);
					target.dispatchEvent(move);

					try {
						Thread.sleep(40); // Add a delay of 50 milliseconds after moving the mouse
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
				return null;
			});

			exchange.sendResponseHeaders(200, 0);
		} else {
			exchange.sendResponseHeaders(405, 0); // Method Not Allowed
		}
		exchange.close();
	}


	public void handleKeyDown(HttpExchange exchange) throws IOException {
		if ("POST".equals(exchange.getRequestMethod())) {
			InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			String requestBody = sb.toString();
			JsonObject json = gson.fromJson(requestBody, JsonObject.class);
			int keyCode = json.get("keyCode").getAsInt();
			char keyChar = (char) json.get("keyChar").getAsInt();

			log.info("Received keydown event with keyCode: " + keyCode);

			invokeAndWait(() -> {
				EventQueue.invokeLater(() -> {
					final Canvas target = client.getCanvas();
					long time = System.currentTimeMillis();

					KeyEvent press = new KeyEvent(target, KeyEvent.KEY_PRESSED, time, 0, keyCode, keyChar);
					target.dispatchEvent(press);
					log.info("Key Pressed: " + press.toString());
				});
				return null;
			});

			exchange.sendResponseHeaders(200, 0);
		} else {
			exchange.sendResponseHeaders(405, 0); // Method Not Allowed
		}
		exchange.close();
	}

	public void handleKeyUp(HttpExchange exchange) throws IOException {
		if ("POST".equals(exchange.getRequestMethod())) {
			InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			String requestBody = sb.toString();
			JsonObject json = gson.fromJson(requestBody, JsonObject.class);
			int keyCode = json.get("keyCode").getAsInt();
			char keyChar = (char) json.get("keyChar").getAsInt();

			log.info("Received keyup event with keyCode: " + keyCode);

			invokeAndWait(() -> {
				EventQueue.invokeLater(() -> {
					final Canvas target = client.getCanvas();
					long time = System.currentTimeMillis();

					KeyEvent release = new KeyEvent(target, KeyEvent.KEY_RELEASED, time, 0, keyCode, keyChar);
					target.dispatchEvent(release);
					log.info("Key Released: " + release.toString());
				});
				return null;
			});

			exchange.sendResponseHeaders(200, 0);
		} else {
			exchange.sendResponseHeaders(405, 0); // Method Not Allowed
		}
		exchange.close();
	}


	public void handleStringInput(HttpExchange exchange) throws IOException {
		if ("POST".equals(exchange.getRequestMethod())) {
			InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			String requestBody = sb.toString();
			JsonObject json = gson.fromJson(requestBody, JsonObject.class);
			String inputString = json.get("inputString").getAsString();

			invokeAndWait(() -> {
				EventQueue.invokeLater(() -> {
					final Canvas target = client.getCanvas();
					long time = System.currentTimeMillis();

					for (char c : inputString.toCharArray()) {
						int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
						KeyEvent press = new KeyEvent(target, KeyEvent.KEY_PRESSED, time, 0, keyCode, c);
						target.dispatchEvent(press);

						KeyEvent typed = new KeyEvent(target, KeyEvent.KEY_TYPED, time, 0, KeyEvent.VK_UNDEFINED, c);
						target.dispatchEvent(typed);

						KeyEvent release = new KeyEvent(target, KeyEvent.KEY_RELEASED, time, 0, keyCode, c);
						target.dispatchEvent(release);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}

						// Log the events
						log.info("Key Pressed: " + press.toString());
						log.info("Key Typed: " + typed.toString());
						log.info("Key Released: " + release.toString());
					}
				});
				return null;
			});

			exchange.sendResponseHeaders(200, 0);
		} else {
			exchange.sendResponseHeaders(405, 0); // Method Not Allowed
		}
		exchange.close();
	}
	public void handleMouseClick(HttpExchange exchange) throws IOException {
		if ("POST".equals(exchange.getRequestMethod())) {
			InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			String requestBody = sb.toString();
			JsonObject json = gson.fromJson(requestBody, JsonObject.class);
			JsonArray coordinates = json.getAsJsonArray("coordinates");
			int x = coordinates.get(0).getAsInt();
			int y = coordinates.get(1).getAsInt();

			invokeAndWait(() -> {
				EventQueue.invokeLater(() -> {
					final Canvas target = client.getCanvas();
					Point canvasLocation = target.getLocationOnScreen();
					int adjustedX = x - canvasLocation.x;
					int adjustedY = y - canvasLocation.y;

					try {
						// Simulate mouse press
						long time = System.currentTimeMillis();
						MouseEvent press = new MouseEvent(target, MouseEvent.MOUSE_PRESSED, time, InputEvent.BUTTON1_DOWN_MASK, adjustedX, adjustedY, 1, false, MouseEvent.BUTTON1);
						target.dispatchEvent(press);
						Thread.sleep(5); // Add a delay of 20 milliseconds

						// Simulate mouse release
						time = System.currentTimeMillis();
						MouseEvent release = new MouseEvent(target, MouseEvent.MOUSE_RELEASED, time, 0, adjustedX, adjustedY, 1, false, MouseEvent.BUTTON1);
						target.dispatchEvent(release);
						Thread.sleep(5); // Add a delay of 20 milliseconds

						// Simulate mouse click
						time = System.currentTimeMillis();
						MouseEvent click = new MouseEvent(target, MouseEvent.MOUSE_CLICKED, time, 0, adjustedX, adjustedY, 1, false, MouseEvent.BUTTON1);
						target.dispatchEvent(click);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
				return null;
			});

			exchange.sendResponseHeaders(200, 0);
		} else {
			exchange.sendResponseHeaders(405, 0); // Method Not Allowed
		}
		exchange.close();
	}


	public void handlerightMouseClick(HttpExchange exchange) throws IOException {
		if ("POST".equals(exchange.getRequestMethod())) {
			InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			String requestBody = sb.toString();
			JsonObject json = gson.fromJson(requestBody, JsonObject.class);
			JsonArray coordinates = json.getAsJsonArray("coordinates");
			int x = coordinates.get(0).getAsInt();
			int y = coordinates.get(1).getAsInt();

			invokeAndWait(() -> {
				EventQueue.invokeLater(() -> {
					final Canvas target = client.getCanvas();
					Point canvasLocation = target.getLocationOnScreen();
					int adjustedX = x - canvasLocation.x;
					int adjustedY = y - canvasLocation.y;

					try {
						// Simulate mouse press
						long time = System.currentTimeMillis();

						MouseEvent press = new MouseEvent(target, MouseEvent.MOUSE_PRESSED, time, InputEvent.META_DOWN_MASK + InputEvent.BUTTON3_DOWN_MASK, adjustedX, adjustedY, 1, false, MouseEvent.BUTTON3);
						target.dispatchEvent(press);
						Thread.sleep(5); // Add a delay of 5 milliseconds

						// Simulate mouse release
						time = System.currentTimeMillis();
						MouseEvent release = new MouseEvent(target, MouseEvent.MOUSE_RELEASED, time, InputEvent.META_DOWN_MASK + InputEvent.BUTTON3_DOWN_MASK, adjustedX, adjustedY, 1, false, MouseEvent.BUTTON3);
						target.dispatchEvent(release);
						Thread.sleep(5); // Add a delay of 5 milliseconds

						// Simulate mouse click
						time = System.currentTimeMillis();
						MouseEvent click = new MouseEvent(target, MouseEvent.MOUSE_CLICKED, time, InputEvent.META_DOWN_MASK + InputEvent.BUTTON3_DOWN_MASK, adjustedX, adjustedY, 1, false, MouseEvent.BUTTON3);
						target.dispatchEvent(click);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
				return null;
			});

			exchange.sendResponseHeaders(200, 0);
		} else {
			exchange.sendResponseHeaders(405, 0); // Method Not Allowed
		}
		exchange.close();
	}






	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		msg = event.getMessage();
		//System.out.println("onChatmsg:" + msg);
	}
	
	@Override
	protected void shutDown() throws Exception
	{
		server.stop(1);
	}

    @Subscribe
	public void onGameTick(GameTick tick)
	{
		currentTime = System.currentTimeMillis();
		xpTracker.update();
		bank.handleBankWindow();

		int skill_count = 0;
		for (Skill skill : Skill.values())
		{
			if (skill == Skill.OVERALL)
			{
				continue;
			}
			int xp_gained = handleTracker(skill);
			xp_gained_skills[skill_count] = xp_gained;
			skill_count ++;
		}
		tickCount++;

		//StatusSocket
		Player player = client.getLocalPlayer();

		if (player == null)
		{
			slc.sendInventoryChangeLog();
			return;
		}

		Actor target = player.getInteracting();

		if (!(target instanceof Player))
		{
			slc.sendInventoryChangeLog();
			return;
		}

		int animationId = player.getAnimation();
		if (animationId == -1)
		{
			slc.sendInventoryChangeLog();
			return;
		}

		AnimationData animationData = AnimationData.fromId(animationId);
		if (animationData == null) // disregard non-combat or unknown animations
		{
			slc.sendInventoryChangeLog();
			return;
		}

		// if we are somehow sending more than 1 attack per tick, it has to be invalid so ignore it.
		if (lastTickAttacked == client.getTickCount())
		{
			slc.sendInventoryChangeLog();
			return;
		}

		// send combat log which will include attack/animation data
		slc.sendCombatLog(target.getName(), true);
		lastTickAttacked = client.getTickCount();
	}


	public void handleTiles(HttpExchange exchange) throws IOException
	{
		JsonArray visibleTiles = invokeAndWait(() -> {
			return tileTracker.getVisibleTiles();
		});

		exchange.sendResponseHeaders(200, 0);
		try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody()))
		{
			RuneLiteAPI.GSON.toJson(visibleTiles, out);
		}
	}


	public int handleTracker(Skill skill){
		int startingSkillXp = xpTracker.getXpData(skill, 0);
		int endingSkillXp = xpTracker.getXpData(skill, tickCount);
		int xpGained = endingSkillXp - startingSkillXp;
		return xpGained;
	}

	public void handleStats(HttpExchange exchange) throws IOException
	{
		Player player = client.getLocalPlayer();
		JsonArray skills = new JsonArray();
		JsonObject headers = new JsonObject();
		headers.addProperty("username", client.getUsername());
		headers.addProperty("player name", player.getName());
		int skill_count = 0;
		skills.add(headers);
		for (Skill skill : Skill.values())
		{
			if (skill == Skill.OVERALL)
			{
				continue;
			}
			JsonObject object = new JsonObject();
			object.addProperty("stat", skill.getName());
			object.addProperty("level", client.getRealSkillLevel(skill));
			object.addProperty("boostedLevel", client.getBoostedSkillLevel(skill));
			object.addProperty("xp", client.getSkillExperience(skill));
			object.addProperty("xp gained", String.valueOf(xp_gained_skills[skill_count]));
			skills.add(object);
			skill_count++;
		}

		exchange.sendResponseHeaders(200, 0);
		try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody()))
		{
			RuneLiteAPI.GSON.toJson(skills, out);
		}
	}

	public void handleEvents(HttpExchange exchange) throws IOException
	{
		MAX_DISTANCE = config.reachedDistance();
		Player player = client.getLocalPlayer();
		Actor npc = player.getInteracting();
		String npcName;
		int npcHealth;
		int npcHealth2;
		int health;
		int minHealth = 0;
		int maxHealth = 0;
		if (npc != null)
		{
			npcName = npc.getName();
			npcHealth = npc.getHealthScale();
			npcHealth2 = npc.getHealthRatio();
			health = 0;
			if (npcHealth2 > 0)
			{
				minHealth = 1;
				if (npcHealth > 1)
				{
					if (npcHealth2 > 1)
					{
						// This doesn't apply if healthRatio = 1, because of the special case in the server calculation that
						// health = 0 forces healthRatio = 0 instead of the expected healthRatio = 1
						minHealth = (npcHealth * (npcHealth2 - 1) + npcHealth - 2) / (npcHealth- 1);
					}
					maxHealth = (npcHealth * npcHealth2 - 1) / (npcHealth- 1);
					if (maxHealth > npcHealth)
					{
						maxHealth = npcHealth;
					}
				}
				else
				{
					// If healthScale is 1, healthRatio will always be 1 unless health = 0
					// so we know nothing about the upper limit except that it can't be higher than maxHealth
					maxHealth = npcHealth;
				}
				// Take the average of min and max possible healths
				health = (minHealth + maxHealth + 1) / 2;
			}
		}
		else
		{
			npcName = "null";
			npcHealth = 0;
			npcHealth2 = 0;
			health = 0;
		}

		WorldPoint worldLocation = client.isInInstancedRegion()?
				WorldPoint.fromLocalInstance(client, player.getLocalLocation()):
				WorldPoint.fromLocal(client, player.getLocalLocation());

		JsonObject object = new JsonObject();
		JsonObject camera = new JsonObject();
		JsonObject worldPoint = new JsonObject();
		JsonObject mouse = new JsonObject();
		JsonObject minimap = new JsonObject();
		object.addProperty("animation", player.getAnimation());
		object.addProperty("animation pose", player.getPoseAnimation());
		object.addProperty("latest msg", msg);
		object.addProperty("run energy", client.getEnergy());
		object.addProperty("game tick", client.getGameCycle());
		object.addProperty("health", client.getBoostedSkillLevel(Skill.HITPOINTS) + "/" + client.getRealSkillLevel(Skill.HITPOINTS));
		object.addProperty("interacting code", String.valueOf(player.getInteracting()));
		object.addProperty("npc name", npcName);
		object.addProperty("npc health ", minHealth);
		object.addProperty("MAX_DISTANCE", MAX_DISTANCE);
		mouse.addProperty("x", client.getMouseCanvasPosition().getX());
		mouse.addProperty("y", client.getMouseCanvasPosition().getY());
		worldPoint.addProperty("x", worldLocation.getX());
		worldPoint.addProperty("y", worldLocation.getY());
		worldPoint.addProperty("plane", player.getWorldLocation().getPlane());
		worldPoint.addProperty("regionID", getRegionIDs());
		worldPoint.addProperty("regionX", worldLocation.getRegionX());
		worldPoint.addProperty("regionY", worldLocation.getRegionY());
		camera.addProperty("yaw", client.getCameraYawTarget() & 2047);
		camera.addProperty("pitch", client.getCameraPitch());
		camera.addProperty("x", client.getCameraX());
		camera.addProperty("y", client.getCameraY());
		camera.addProperty("z", client.getCameraZ());
		camera.addProperty("x2", client.getCameraFocalPointX());
		camera.addProperty("y2", client.getCameraFocalPointY());
		camera.addProperty("z2", client.getCameraFocalPointZ());
		object.add("worldPoint", worldPoint);
		object.add("camera", camera);
		object.add("mouse", mouse);

		Widget minimap_draw_area_tl = client.isResized()?
				client.getWidget(10551326): // https://i.imgur.com/TU1DPfG.png
				client.getWidget(WidgetInfo.FIXED_VIEWPORT_MINIMAP_DRAW_AREA);

		int minimapCenterX = 0;
		int minimapCenterY = 0;

        if (minimap_draw_area_tl != null) {
            int minimapWidth = minimap_draw_area_tl.getWidth();
			int minimapHeight = minimap_draw_area_tl.getHeight();
			minimapCenterX = minimap_draw_area_tl.getCanvasLocation().getX() + minimapWidth / 2;
			minimapCenterY = minimap_draw_area_tl.getCanvasLocation().getY() + minimapHeight / 2;
        }

		minimap.addProperty("minimap_zoom", client.getMinimapZoom());
		minimap.addProperty("center_x", minimapCenterX);
		minimap.addProperty("center_y", minimapCenterY);
		object.add("minimap", minimap);

		exchange.sendResponseHeaders(200, 0);
		try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody()))
		{
			RuneLiteAPI.GSON.toJson(object, out);
		}
	}
	public int getRegionIDs(){
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null) {
			// Handle the case where there is no player information available
			return 0;
		}


		WorldPoint wp = localPlayer.getWorldLocation();
		int tileX = wp.getX();
		int tileY = wp.getY();
		int z = client.getPlane();

// Check if the player is in an instanced area and adjust coordinates
		if (client.isInInstancedRegion()) {
			int[][][] instanceTemplateChunks = client.getInstanceTemplateChunks();
			LocalPoint localPoint = localPlayer.getLocalLocation();
			int chunkData = instanceTemplateChunks[z][localPoint.getSceneX() / 8][localPoint.getSceneY() / 8];

			tileX = (chunkData >> 14 & 0x3FF) * 8 + (tileX % 8);
			tileY = (chunkData >> 3 & 0x7FF) * 8 + (tileY % 8);
		}

		int regionX = tileX / 64;
		int regionY = tileY / 64;
		int regionID = (regionX << 8) | regionY;
		return regionID;
	}
	public void handleBank(HttpExchange exchange) throws IOException
	{
		JsonArray items = new JsonArray();

		List<BankItem> bankItems = bank.getItems();
		for(BankItem bankItem : bankItems)
		{
			JsonObject object = new JsonObject();
			object.addProperty("id", bankItem.getId());
			object.addProperty("quantity", bankItem.getQuantity());
			items.add(object);
		}

		exchange.sendResponseHeaders(200, 0);
		try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody()))
		{
			RuneLiteAPI.GSON.toJson(items, out);
		}
	}

	public void handleNpcs(HttpExchange exchange) throws IOException
	{
		JsonArray visibleNpcs = invokeAndWait(() -> {
			return npcTracker.getVisibleNpcs();
		});

		exchange.sendResponseHeaders(200, 0);
		try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody()))
		{
			RuneLiteAPI.GSON.toJson(visibleNpcs, out);
		}
	}

	public void handleObjects(HttpExchange exchange) throws IOException
	{
		JsonObject visibleObjects = invokeAndWait(() -> {
			return objectTracker.getVisibleObjects();
		});

		exchange.sendResponseHeaders(200, 0);
		try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody()))
		{
			RuneLiteAPI.GSON.toJson(visibleObjects, out);
		}
	}

	private HttpHandler handlerForInv(InventoryID inventoryID)
	{
		return exchange -> {
			Item[] items = invokeAndWait(() -> {
				ItemContainer itemContainer = client.getItemContainer(inventoryID);
				if (itemContainer != null)
				{
					return itemContainer.getItems();
				}
				return null;
			});

			if (items == null)
			{
				exchange.sendResponseHeaders(204, 0);
				return;
			}

			exchange.sendResponseHeaders(200, 0);
			try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody()))
			{
				RuneLiteAPI.GSON.toJson(items, out);
			}
		};
	}
	private <T> T invokeAndWait(Callable<T> r)
	{
		try
		{
			AtomicReference<T> ref = new AtomicReference<>();
			Semaphore semaphore = new Semaphore(0);
			clientThread.invokeLater(() -> {
				try
				{

					ref.set(r.call());
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
				finally
				{
					semaphore.release();
				}
			});
			semaphore.acquire();
			return ref.get();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	// StatusSocket
	// send hitsplat packet when main Player does damage to another Player
	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		Player player = client.getLocalPlayer();
		Actor actor = event.getActor();
		Hitsplat hitsplat = event.getHitsplat();

		if (player == null || actor == null || hitsplat == null || !hitsplat.isMine() || Objects.equals(actor.getName(), player.getName()))
		{
			return;
		}

		if (actor instanceof Player)
		{
			Player target = (Player) actor;
			String targetName = target.getName();

			clientThread.invokeLater(() ->
                    slc.sendHitsplat(hitsplat.getAmount(), targetName));
		}
	}

	// the onAnimationChanged event is used to:
	// - detect when the client player is being attacked by another player
	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		// delay animation processing, since we will also want to use equipment data for deserved
		// damage, and equipment updates are loaded shortly after the animation updates.
		// without the invokeLater, equipped gear would be 1 tick behind the animation.
		clientThread.invokeLater(() ->
		{
			// check if client player gets targeted by a player.
			Player player = client.getLocalPlayer();
			Actor actor = event.getActor();

			if (player == null || !(actor instanceof Player))
			{
				return;
			}

			// if the event actor is the player, then we're attacking.
			// otherwise, the player is being attacked. so the target attacker is the event actor
			boolean isAttacking = Objects.equals(actor.getName(), player.getName());

			// attacking is now dealt with in the onGameTick event, this is only for defending
			if (isAttacking)
			{
				return;
			}

			Actor target = actor.getInteracting();
			// make sure that the player is one of the people involved in the interaction
			// (being attacked)
			// I forget why exactly use names, but it behaves more consistently than comparing the whole player object.
			if (!(target instanceof Player) ||
					(!Objects.equals(target.getName(), player.getName())))
			{
				return;
			}

			int animationId = actor.getAnimation();
			if (animationId == -1)
			{
				return;
			}

			AnimationData animationData = AnimationData.fromId(animationId);
			if (animationData == null) // disregard non-combat or unknown animations
			{
				return;
			}

			slc.sendCombatLog(actor.getName(), false);
		});
	}

	// detect when any Player dies
	@Subscribe
	public void onActorDeath(ActorDeath event)
	{
		// don't really need player here, but if it's null then something wrong
		Player player = client.getLocalPlayer();
		Actor actor = event.getActor();

		// only check Player deaths
		if (player == null || !(actor instanceof Player))
		{
			return;
		}

		slc.sendDeath(actor.getName());
	}
}
