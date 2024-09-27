
package com.piggyplugins.AoeWarn;

import com.google.inject.Provides;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.inject.Inject;

import com.piggyplugins.PiggyUtils.PiggyUtilsPlugin;
import com.piggyplugins.PiggyUtils.RLApi.GraphicIDExtended;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Projectile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;

@PluginDescriptor(
		name = "<html><font color=\"#FF9DF9\">[GS]</font> AoE Warnings</html>",
	enabledByDefault = false,
	description = "Muestra el destino final de los proyectiles de ataque AoE.",
	tags = {"ElGuason"}
)
public class AoeWarningPlugin extends Plugin
{
	@Getter(AccessLevel.PACKAGE)
	private final Set<CrystalBomb> bombs = new HashSet<>();

	@Getter(AccessLevel.PACKAGE)
	private final Set<ProjectileContainer> projectiles = new HashSet<>();
	private final ArrayList<Projectile> spawnedProjectiles = new ArrayList<>();


	@Inject
	public AoeWarningConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private AoeWarningOverlay coreOverlay;

	@Inject
	private BombOverlay bombOverlay;

	@Inject
	private Client client;

	@Getter(AccessLevel.PACKAGE)
	private List<WorldPoint> lightningTrail = new ArrayList<>();

	@Getter(AccessLevel.PACKAGE)
	private List<GameObject> acidTrail = new ArrayList<>();

	@Getter(AccessLevel.PACKAGE)
	private List<GameObject> crystalSpike = new ArrayList<>();

	@Getter(AccessLevel.PACKAGE)
	private List<GameObject> wintertodtSnowFall = new ArrayList<>();

	private static final int VERZIK_REGION = 12611;
	private static final int GROTESQUE_GUARDIANS_REGION = 6727;

	@Provides
	AoeWarningConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AoeWarningConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(coreOverlay);
		overlayManager.add(bombOverlay);
		reset();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(coreOverlay);
		overlayManager.remove(bombOverlay);
		reset();
	}

	@Subscribe
	private void onProjectileMoved(ProjectileMoved event)
	{
		//Original ProjectileSpawned Code
		final Projectile projectile = event.getProjectile();
		final int id = projectile.getId();

		if (AoeProjectileInfo.getById(id) == null)
		{
			return;
		}

		final int lifetime = config.delay() + (projectile.getRemainingCycles() * 20);
		int ticksRemaining = projectile.getRemainingCycles() / 30;
		if (!isTickTimersEnabledForProjectileID(id))
		{
			ticksRemaining = 0;
		}
		final int tickCycle = client.getTickCount() + ticksRemaining;
		if (isConfigEnabledForProjectileId(id, false))
		{
			if (!spawnedProjectiles.contains(projectile)) //Store the projectile and check if it's the not same projectile (ProjectileSpawned)
			{
				spawnedProjectiles.add(projectile);
				projectiles.add(new ProjectileContainer(projectile, Instant.now(), lifetime, tickCycle));

				if (config.aoeNotifyAll() || isConfigEnabledForProjectileId(id, true))
				{
					notifier.notify("¡Ataque AoE detectado!");
				}
			}
		}

		//Original ProjectileMoved Code
		if (projectiles.isEmpty())
		{
			return;
		}

		//	final Projectile projectile = event.getProjectile();

		projectiles.forEach(proj ->
		{
			if (proj.getProjectile() == projectile)
			{
				proj.setTargetPoint(event.getPosition());
			}
		});
	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event)
	{
		final GameObject gameObject = event.getGameObject();

		switch (gameObject.getId())
		{
			case ObjectID.CRYSTAL_BOMB:
				bombs.add(new CrystalBomb(gameObject, client.getTickCount()));

				if (config.aoeNotifyAll() || config.bombDisplayNotifyEnabled())
				{
					notifier.notify("Bomb!");
				}
				break;
			case ObjectID.ACID_POOL:
				acidTrail.add(gameObject);
				break;
			case ObjectID.SMALL_CRYSTALS:
				crystalSpike.add(gameObject);
				break;
			case NullObjectID.NULL_26690:
				if (config.isWintertodtEnabled())
				{
					wintertodtSnowFall.add(gameObject);

					if (config.aoeNotifyAll() || config.isWintertodtNotifyEnabled())
					{
						notifier.notify("¡Caída de nieve!");
					}
				}
				break;
		}
	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned event)
	{
		final GameObject gameObject = event.getGameObject();

		switch (gameObject.getId())
		{
			case ObjectID.CRYSTAL_BOMB:
				bombs.removeIf(o -> o.getGameObject() == gameObject);
				break;
			case ObjectID.ACID_POOL:
				acidTrail.remove(gameObject);
				break;
			case ObjectID.SMALL_CRYSTALS:
				crystalSpike.remove(gameObject);
				break;
			case NullObjectID.NULL_26690:
				wintertodtSnowFall.remove(gameObject);
				break;
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			return;
		}
		reset();
	}

	@Subscribe
	private void onGameTick(GameTick event)
	{
		lightningTrail.clear();

		if (config.LightningTrail())
		{
			client.getGraphicsObjects().forEach(o ->
			{
				if (o.getId() == GraphicIDExtended.OLM_LIGHTNING)
				{
					lightningTrail.add(WorldPoint.fromLocal(client, o.getLocation()));

					if (config.aoeNotifyAll() || config.LightningTrailNotifyEnabled())
					{
						notifier.notify("Lightning!");
					}
				}
			});
		}

		bombs.forEach(CrystalBomb::bombClockUpdate);
	}

	private boolean isTickTimersEnabledForProjectileID(int projectileId)
	{
		AoeProjectileInfo projectileInfo = AoeProjectileInfo.getById(projectileId);

		if (projectileInfo == null)
		{
			return false;
		}

		switch (projectileInfo)
		{
			case VASA_RANGED_AOE:
			case VORKATH_POISON_POOL:
			case VORKATH_SPAWN:
			case VORKATH_TICK_FIRE:
			case OLM_BURNING:
			case OLM_FALLING_CRYSTAL_TRAIL:
			case OLM_ACID_TRAIL:
			case OLM_FIRE_LINE:
				return false;
		}

		return true;
	}

	private boolean isConfigEnabledForProjectileId(int projectileId, boolean notify)
	{
		AoeProjectileInfo projectileInfo = AoeProjectileInfo.getById(projectileId);
		if (projectileInfo == null)
		{
			return false;
		}

		if (notify && config.aoeNotifyAll())
		{
			return true;
		}

		switch (projectileInfo)
		{
			case LIZARDMAN_SHAMAN_AOE:
				return notify ? config.isShamansNotifyEnabled() : config.isShamansEnabled();
			case CRAZY_ARCHAEOLOGIST_AOE:
				return notify ? config.isArchaeologistNotifyEnabled() : config.isArchaeologistEnabled();
			case ICE_DEMON_RANGED_AOE:
			case ICE_DEMON_ICE_BARRAGE_AOE:
				return notify ? config.isIceDemonNotifyEnabled() : config.isIceDemonEnabled();
			case VASA_AWAKEN_AOE:
			case VASA_RANGED_AOE:
				return notify ? config.isVasaNotifyEnabled() : config.isVasaEnabled();
			case TEKTON_METEOR_AOE:
				return notify ? config.isTektonNotifyEnabled() : config.isTektonEnabled();
			case VORKATH_BOMB:
			case VORKATH_POISON_POOL:
			case VORKATH_SPAWN:
			case VORKATH_TICK_FIRE:
				return notify ? config.isVorkathNotifyEnabled() : config.vorkathModes().contains(AoeWarningConfig.VorkathMode.of(projectileInfo));
			case VETION_LIGHTNING:
				return notify ? config.isVetionNotifyEnabled() : config.isVetionEnabled();
			case CHAOS_FANATIC:
				return notify ? config.isChaosFanaticNotifyEnabled() : config.isChaosFanaticEnabled();
			case GALVEK_BOMB:
			case GALVEK_MINE:
				return notify ? config.isGalvekNotifyEnabled() : config.isGalvekEnabled();
			case DAWN_FREEZE:
			case DUSK_CEILING:
				if (regionCheck(GROTESQUE_GUARDIANS_REGION))
				{
					return notify ? config.isGargBossNotifyEnabled() : config.isGargBossEnabled();
				}
			case VERZIK_P1_ROCKS:
				if (regionCheck(VERZIK_REGION))
				{
					return notify ? config.isVerzikNotifyEnabled() : config.isVerzikEnabled();
				}
			case OLM_FALLING_CRYSTAL:
			case OLM_BURNING:
			case OLM_FALLING_CRYSTAL_TRAIL:
			case OLM_ACID_TRAIL:
			case OLM_FIRE_LINE:
				return notify ? config.isOlmNotifyEnabled() : config.isOlmEnabled();
			case CORPOREAL_BEAST:
			case CORPOREAL_BEAST_DARK_CORE:
				return notify ? config.isCorpNotifyEnabled() : config.isCorpEnabled();
			case XARPUS_POISON_AOE:
				return notify ? config.isXarpusNotifyEnabled() : config.isXarpusEnabled();
			case ADDY_DRAG_POISON:
				return notify ? config.addyDragsNotifyEnabled() : config.addyDrags();
			case DRAKE_BREATH:
				return notify ? config.isDrakeNotifyEnabled() : config.isDrakeEnabled();
			case CERB_FIRE:
				return notify ? config.isCerbFireNotifyEnabled() : config.isCerbFireEnabled();
			case DEMONIC_GORILLA_BOULDER:
				return notify ? config.isDemonicGorillaNotifyEnabled() : config.isDemonicGorillaEnabled();
			case VERZIK_PURPLE_SPAWN:
				return notify ? config.isVerzikNotifyEnabled() : config.isVerzikEnabled();
		}

		return false;
	}

	private void reset()
	{
		lightningTrail.clear();
		acidTrail.clear();
		crystalSpike.clear();
		wintertodtSnowFall.clear();
		bombs.clear();
		projectiles.clear();
	}

	private boolean regionCheck(int region)
	{
		return ArrayUtils.contains(client.getMapRegions(), region);
	}
}