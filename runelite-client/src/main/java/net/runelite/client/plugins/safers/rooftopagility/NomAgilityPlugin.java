/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.safers.rooftopagility;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.runelite.api.Skill.AGILITY;

@PluginDescriptor(
		name = "<html><font color=#3076DF>[SafeRS] </font>Agility</html>",
	description = "Highlight what to click",
	tags = {"tom","grace", "marks", "overlay", "shortcuts", "skilling", "traps", "sepulchre", "nom"},
	enabledByDefault = false
)
@Slf4j
public class NomAgilityPlugin extends Plugin
{
	@Getter
	private final Map<TileObject, Tile> obstacles = new HashMap<>();

	@Getter
	private final List<Tile> marksOfGrace = new ArrayList<>();

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NomAgilityOverlay nomAgilityOverlay;

	@Inject
	private NomAgilityMinimapOverlay nomAgilityMinimapOverlay;


	@Inject
	private NomAgilityPanelOverlay nomAgilityPanelOverlay;

	@Inject
	private WorldPointMinimapOverlay worldPointMinimapOverlay;

	@Inject
	private Notifier notifier;

	@Inject
	private Client client;

	@Inject
	private NomAgilityConfig config;

	private int lastAgilityXp;
	@Inject
	private ConfigManager configManager;

	@Getter
	private int agilityLevel;

	@Provides
	NomAgilityConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NomAgilityConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{

		overlayManager.add(nomAgilityOverlay);
		overlayManager.add(nomAgilityMinimapOverlay);
		overlayManager.add(nomAgilityPanelOverlay);
		overlayManager.add(worldPointMinimapOverlay);
		agilityLevel = client.getBoostedSkillLevel(AGILITY);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(nomAgilityOverlay);
		overlayManager.remove(nomAgilityMinimapOverlay);
		overlayManager.remove(nomAgilityPanelOverlay);
		overlayManager.remove(worldPointMinimapOverlay);
		marksOfGrace.clear();
		obstacles.clear();
		agilityLevel = 0;
	}


	@Getter
	private NomCourses course = null;

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		if (statChanged.getSkill() != AGILITY)
		{
			return;
		}

		status = "Exp drop";
		statTimer.resetTimer();
	}


	private boolean seersTeleport = false;
	private boolean showingMarkOfGrace = false;
	private int lolcount = 0;

	private final InstantTimer actionTimer=new InstantTimer();
	private final InstantTimer statTimer=new InstantTimer();
	private WorldPoint lastPoint = null;
	private static int ACTION_DELAY = 1800;
	private static int FORCE_SHOW_AFTER_EXP = 3800;
	private boolean disableOverlays = false;
	private String status = "";
	@Getter
	private int minimapDistance = 12;
	@Subscribe
	public void onGameTick(GameTick event) throws IOException {


		minimapDistance = 6 + lolcount % 8;
		lolcount++;
		if (course == null || lolcount % 10 == 0) setCourse();
		if (course == null) {
			disableOverlays = true;
			return;
		}


		WorldPoint currentPoint = client.getLocalPlayer().getWorldLocation();
		LocalPoint destination = client.getLocalDestinationLocation();


		// If not force showing, consider other overlay disables
		if (statTimer.runningMoreThan(FORCE_SHOW_AFTER_EXP)) {
			if (actionTimer.runningMoreThan(ACTION_DELAY)) {
				disableOverlays = false;
				status = "";
			}

			// If animating/moving since ACTION_DELAY, disable overlay
			// If on ground floor, don't disable since we're just running back to start
			if (currentPoint.getPlane() != 0 &&
				client.getLocalPlayer().getAnimation() != -1) {
				log.info("Animation disable");
				status = "Animating";
				actionTimer.resetTimer();
				disableOverlays = true;
			} else if (!currentPoint.equals(lastPoint) ||
				client.getLocalDestinationLocation() != null) {
				actionTimer.resetTimer();
				lastPoint = currentPoint;
				log.info("Recent movement disable");
				status = "Moving";
				disableOverlays = false;
			} else {
				disableOverlays = false;
				status = "";
			}
		} else {
			disableOverlays = false;
			status = "Exp drop";
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case HOPPING:
			case LOGIN_SCREEN:
				course = null;
				break;
			case LOADING:
				marksOfGrace.clear();
				obstacles.clear();
				break;
		}
	}

	private void setCourse() {
		double distance = Double.MAX_VALUE;
		for (NomCourses value : NomCourses.values()) {
			double courseDistance = value.getCourseData().getLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation());
			if (courseDistance < distance) {
				distance = courseDistance;
				course = value;
			}
		}
		if (distance > 150) {
			log.info("No course");
			course = null;
		} else {
			log.info("Setting course " + course);
		}
	}


	@Subscribe
	public void onItemSpawned(ItemSpawned itemSpawned)
	{
		if (obstacles.isEmpty())
		{
			return;
		}

		final TileItem item = itemSpawned.getItem();
		final Tile tile = itemSpawned.getTile();

		if (item.getId() == ItemID.MARK_OF_GRACE)
		{
			marksOfGrace.add(tile);
		}
	}

	@Subscribe
	public void onItemDespawned(ItemDespawned itemDespawned)
	{
		final TileItem item = itemDespawned.getItem();
		final Tile tile = itemDespawned.getTile();

		marksOfGrace.remove(tile);
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		onTileObject(event.getTile(), null, event.getGameObject());
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		onTileObject(event.getTile(), event.getGameObject(), null);
	}

	@Subscribe
	public void onGroundObjectSpawned(GroundObjectSpawned event)
	{
		onTileObject(event.getTile(), null, event.getGroundObject());
	}

	@Subscribe
	public void onGroundObjectDespawned(GroundObjectDespawned event)
	{
		onTileObject(event.getTile(), event.getGroundObject(), null);
	}

	@Subscribe
	public void onWallObjectSpawned(WallObjectSpawned event)
	{
		onTileObject(event.getTile(), null, event.getWallObject());
	}

	@Subscribe
	public void onWallObjectDespawned(WallObjectDespawned event)
	{
		onTileObject(event.getTile(), event.getWallObject(), null);
	}

	@Subscribe
	public void onDecorativeObjectSpawned(DecorativeObjectSpawned event)
	{
		onTileObject(event.getTile(), null, event.getDecorativeObject());
	}

	@Subscribe
	public void onDecorativeObjectDespawned(DecorativeObjectDespawned event)
	{
		onTileObject(event.getTile(), event.getDecorativeObject(), null);
	}

	private void onTileObject(Tile tile, TileObject oldObject, TileObject newObject)
	{
		obstacles.remove(oldObject);

		if (newObject == null)
		{
			return;
		}

		if (NomObstacles.COURSE_OBSTACLE_IDS.contains(newObject.getId()) ||
			NomObstacles.PORTAL_OBSTACLE_IDS.contains(newObject.getId()) ||
			(NomObstacles.TRAP_OBSTACLE_IDS.contains(newObject.getId())
				&& NomObstacles.TRAP_OBSTACLE_REGIONS.contains(newObject.getWorldLocation().getRegionID())) ||
			NomObstacles.SEPULCHRE_OBSTACLE_IDS.contains(newObject.getId()) ||
			NomObstacles.SEPULCHRE_SKILL_OBSTACLE_IDS.contains(newObject.getId()))
		{
			obstacles.put(newObject, tile);
			System.out.println("Add "+newObject.getId());
		}
	}

	public int square() {
		return Math.min(5,Math.max(1,config.solidSquare()));
	}

	public boolean isDisableOverlays()
	{
		return disableOverlays;
	}

	public boolean isShowingMarkOfGrace()
	{
		return showingMarkOfGrace;
	}

	public void setShowingMarkOfGrace(boolean showingMarkOfGrace)
	{
		this.showingMarkOfGrace = showingMarkOfGrace;
	}

	public String getStatus()
	{
		return status;
	}


	public void setSeersTeleport(boolean seersTeleport)
	{
		this.seersTeleport = seersTeleport;
	}
}
