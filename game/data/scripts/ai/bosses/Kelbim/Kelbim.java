/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.bosses.Kelbim;

import java.util.ArrayList;
import java.util.List;

import com.l2jmobius.Config;
import com.l2jmobius.commons.util.Rnd;
import com.l2jmobius.gameserver.data.xml.impl.SkillData;
import com.l2jmobius.gameserver.enums.Movie;
import com.l2jmobius.gameserver.instancemanager.GrandBossManager;
import com.l2jmobius.gameserver.instancemanager.MapRegionManager;
import com.l2jmobius.gameserver.instancemanager.ZoneManager;
import com.l2jmobius.gameserver.model.L2Party;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.TeleportWhereType;
import com.l2jmobius.gameserver.model.actor.L2Attackable;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.QuestTimer;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.model.zone.L2ZoneType;
import com.l2jmobius.gameserver.network.serverpackets.Earthquake;
import com.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmobius.gameserver.util.Broadcast;

import ai.AbstractNpcAI;

/**
 * Kelbim AI<br>
 * @author LasTravel<br>
 * @video https://www.youtube.com/watch?v=qVkk2BJoGoU
 */
public class Kelbim extends AbstractNpcAI
{
	// Status
	private static final int ALIVE = 0;
	private static final int WAITING = 1;
	private static final int FIGHTING = 2;
	private static final int DEAD = 3;
	// NPCs
	private static final int ENTER_DEVICE = 34052;
	private static final int TELEPORT_DEVICE = 34053;
	private static final int KELBIM_SHOUT = 19597;
	private static final int KELBIM = 26124;
	private static final int GUARDIAN_SINISTRA = 26126;
	private static final int GUARDIAN_DESTRA = 26127;
	private static final int[] KELBIM_GUARDIANS =
	{
		GUARDIAN_SINISTRA,
		GUARDIAN_DESTRA
	};
	private static final int KELBIM_GUARD = 26129;
	private static final int KELBIM_ALTAR = 26130;
	private static final int[] KELBIM_MINIONS =
	{
		GUARDIAN_SINISTRA,
		GUARDIAN_DESTRA,
		KELBIM_GUARD
	};
	private static final int[] ALL_MONSTERS =
	{
		KELBIM,
		KELBIM_MINIONS[0],
		KELBIM_MINIONS[1],
		KELBIM_MINIONS[2],
		KELBIM_ALTAR
	};
	// Doors
	private static final int DOOR1 = 18190002;
	private static final int DOOR2 = 18190004;
	// Skills
	private static final Skill METEOR_CRASH = SkillData.getInstance().getSkill(23692, 1);
	private static final Skill WATER_DROP = SkillData.getInstance().getSkill(23693, 1);
	private static final Skill TORNADO_SACKLE = SkillData.getInstance().getSkill(23694, 1);
	private static final Skill FLAME_THROWER = SkillData.getInstance().getSkill(23699, 1);
	private static final Skill[] AREA_SKILLS =
	{
		METEOR_CRASH,
		WATER_DROP,
		TORNADO_SACKLE,
		FLAME_THROWER
	};
	// Misc
	private static final L2ZoneType ZONE = ZoneManager.getInstance().getZoneById(60023);
	private static final Location KELBIM_LOCATION = new Location(-55386, 58939, -274);
	// Vars
	private static L2Npc _kelbimBoss;
	private static long _lastAction;
	private static int _bossStage;
	private static ArrayList<L2Npc> _minions = new ArrayList<>();
	
	public Kelbim()
	{
		addTalkId(ENTER_DEVICE, TELEPORT_DEVICE);
		addStartNpc(ENTER_DEVICE, TELEPORT_DEVICE);
		addFirstTalkId(ENTER_DEVICE, TELEPORT_DEVICE);
		addAttackId(ALL_MONSTERS);
		addKillId(KELBIM);
		
		// Unlock
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(KELBIM);
		final int status = GrandBossManager.getInstance().getBossStatus(KELBIM);
		if (status == DEAD)
		{
			final long time = info.getLong("respawn_time") - System.currentTimeMillis();
			if (time > 0)
			{
				startQuestTimer("unlock_kelbim", time, null, null);
			}
			else
			{
				openDoor(DOOR1, 0);
				openDoor(DOOR2, 0);
				GrandBossManager.getInstance().setBossStatus(KELBIM, ALIVE);
			}
		}
		else if (status != ALIVE)
		{
			openDoor(DOOR1, 0);
			openDoor(DOOR2, 0);
			GrandBossManager.getInstance().setBossStatus(KELBIM, ALIVE);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "unlock_kelbim":
			{
				GrandBossManager.getInstance().setBossStatus(KELBIM, ALIVE);
				Broadcast.toAllOnlinePlayers(new Earthquake(-55754, 59903, -269, 20, 10));
				openDoor(DOOR1, 0);
				openDoor(DOOR2, 0);
				break;
			}
			case "check_activity_task":
			{
				if ((_lastAction + 900000) < System.currentTimeMillis())
				{
					GrandBossManager.getInstance().setBossStatus(KELBIM, ALIVE);
					for (L2Character charInside : ZONE.getCharactersInside())
					{
						if (charInside != null)
						{
							if (charInside.isNpc())
							{
								charInside.deleteMe();
							}
							else if (charInside.isPlayer())
							{
								charInside.teleToLocation(MapRegionManager.getInstance().getTeleToLocation(charInside, TeleportWhereType.TOWN));
							}
						}
					}
					startQuestTimer("end_kelbim", 2000, null, null);
				}
				else
				{
					startQuestTimer("check_activity_task", 60000, null, null);
				}
				break;
			}
			case "stage_1_start":
			{
				_bossStage = 1;
				GrandBossManager.getInstance().setBossStatus(KELBIM, FIGHTING);
				playMovie(ZONE.getPlayersInside(), Movie.SC_KELBIM_OPENING);
				startQuestTimer("stage_1_kelbim_spawn", 25000, null, null);
				break;
			}
			case "stage_1_kelbim_spawn":
			{
				_kelbimBoss = addSpawn(KELBIM, -56340, 60801, -269, 54262, false, 0);
				_lastAction = System.currentTimeMillis();
				startQuestTimer("check_activity_task", 60000, null, null);
				startQuestTimer("stage_all_random_area_attack", Rnd.get(2, 3) * 60000, null, null);
				break;
			}
			case "stage_all_spawn_minions":
			{
				for (int i = 0; i < Rnd.get((_bossStage * 5) / 2, _bossStage * 5); i++)
				{
					L2Npc minion = addSpawn(KELBIM_GUARD, _kelbimBoss.getX(), _kelbimBoss.getY(), _kelbimBoss.getZ(), 0, true, 0, true, 0);
					minion.setRunning();
					((L2Attackable) minion).setIsRaidMinion(true);
					_minions.add(minion);
				}
				for (int i = 0; i < Rnd.get((_bossStage * 2) / 2, _bossStage * 2); i++)
				{
					L2Npc minion = addSpawn(KELBIM_GUARDIANS[Rnd.get(KELBIM_GUARDIANS.length)], _kelbimBoss.getX(), _kelbimBoss.getY(), _kelbimBoss.getZ(), 0, true, 0, true, 0);
					minion.setRunning();
					((L2Attackable) minion).setIsRaidMinion(true);
					_minions.add(minion);
				}
				break;
			}
			case "stage_all_random_area_attack":
			{
				if ((_bossStage > 0) && (_bossStage < 7))
				{
					if (_kelbimBoss.isInCombat())
					{
						Skill randomAttackSkill = AREA_SKILLS[Rnd.get(AREA_SKILLS.length)];
						ArrayList<L2Npc> _skillNpcs = new ArrayList<>();
						for (L2PcInstance pl : ZONE.getPlayersInside())
						{
							if (pl == null)
							{
								continue;
							}
							if (Rnd.get(100) > 40)
							{
								L2Npc skillMob = addSpawn(KELBIM_SHOUT, pl.getX(), pl.getY(), pl.getZ() + 10, 0, true, 60000, false, 0);
								_skillNpcs.add(skillMob);
								_minions.add(skillMob);
							}
						}
						for (L2Npc skillNpc : _skillNpcs)
						{
							if (skillNpc == null)
							{
								continue;
							}
							skillNpc.doCast(randomAttackSkill);
						}
					}
					startQuestTimer("stage_all_random_area_attack", Rnd.get(1, 2) * 60000, null, null);
				}
				break;
			}
			case "cancel_timers":
			{
				QuestTimer activityTimer = getQuestTimer("check_activity_task", null, null);
				if (activityTimer != null)
				{
					activityTimer.cancel();
				}
				break;
			}
			case "end_kelbim":
			{
				_bossStage = 0;
				ZONE.oustAllPlayers();
				if (_kelbimBoss != null)
				{
					_kelbimBoss.deleteMe();
				}
				if (!_minions.isEmpty())
				{
					for (L2Npc minion : _minions)
					{
						if (minion == null)
						{
							continue;
						}
						minion.deleteMe();
					}
				}
				_minions.clear();
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		switch (npc.getId())
		{
			case TELEPORT_DEVICE:
			{
				player.teleToLocation(-55730, 55643, -1954);
				return null;
			}
			case ENTER_DEVICE:
			{
				return "34052.html";
			}
		}
		return super.onFirstTalk(npc, player);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		if (npc.getId() == ENTER_DEVICE)
		{
			int status = GrandBossManager.getInstance().getBossStatus(KELBIM);
			if (status > ALIVE)
			{
				return "34052-1.html";
			}
			
			if (!player.isInParty())
			{
				final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
				packet.setHtml(getHtm(player, "34052-2.html"));
				packet.replace("%min%", Integer.toString(Config.KELBIM_MIN_PLAYERS));
				player.sendPacket(packet);
				return null;
			}
			
			final L2Party party = player.getParty();
			final boolean isInCC = party.isInCommandChannel();
			final List<L2PcInstance> members = (isInCC) ? party.getCommandChannel().getMembers() : party.getMembers();
			final boolean isPartyLeader = (isInCC) ? party.getCommandChannel().isLeader(player) : party.isLeader(player);
			if (!isPartyLeader)
			{
				return "34052-3.html";
			}
			else if ((members.size() < Config.KELBIM_MIN_PLAYERS) || (members.size() > Config.KELBIM_MAX_PLAYERS))
			{
				final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
				packet.setHtml(getHtm(player, "34052-2.html"));
				packet.replace("%min%", Integer.toString(Config.KELBIM_MIN_PLAYERS));
				player.sendPacket(packet);
			}
			else
			{
				for (L2PcInstance member : members)
				{
					if (member.isInsideRadius3D(npc, 1000))
					{
						member.teleToLocation(KELBIM_LOCATION, true);
					}
				}
			}
			
			if (status == ALIVE)
			{
				GrandBossManager.getInstance().setBossStatus(KELBIM, WAITING);
				startQuestTimer("stage_1_start", Config.KELBIM_WAIT_TIME * 60 * 1000, null, null);
			}
		}
		return super.onTalk(npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.getId() == KELBIM)
		{
			_lastAction = System.currentTimeMillis();
			
			switch (_bossStage)
			{
				case 1:
				{
					if (npc.getCurrentHp() < (npc.getMaxHp() * 0.80))
					{
						_bossStage = 2;
						notifyEvent("stage_all_spawn_minions", null, null);
					}
					break;
				}
				case 2:
				{
					if (npc.getCurrentHp() < (npc.getMaxHp() * 0.60))
					{
						_bossStage = 3;
						notifyEvent("stage_all_spawn_minions", null, null);
					}
					break;
				}
				case 3:
				{
					if (npc.getCurrentHp() < (npc.getMaxHp() * 0.40))
					{
						_bossStage = 4;
						notifyEvent("stage_all_spawn_minions", null, null);
					}
					break;
				}
				case 4:
				{
					if (npc.getCurrentHp() < (npc.getMaxHp() * 0.20))
					{
						_bossStage = 5;
						notifyEvent("stage_all_spawn_minions", null, null);
					}
					break;
				}
				case 5:
				{
					if (npc.getCurrentHp() < (npc.getMaxHp() * 0.05))
					{
						_bossStage = 6;
						notifyEvent("stage_all_spawn_minions", null, null);
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		_bossStage = 7;
		
		addSpawn(TELEPORT_DEVICE, -54331, 58331, -264, 16292, false, 1800000);
		
		notifyEvent("cancel_timers", null, null);
		
		closeDoor(DOOR1, 0);
		closeDoor(DOOR2, 0);
		
		GrandBossManager.getInstance().setBossStatus(KELBIM, DEAD);
		final long respawnTime = (Config.KELBIM_SPAWN_INTERVAL + getRandom(-Config.KELBIM_SPAWN_RANDOM, Config.KELBIM_SPAWN_RANDOM)) * 3600000;
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(KELBIM);
		info.set("respawn_time", System.currentTimeMillis() + respawnTime);
		GrandBossManager.getInstance().setStatsSet(KELBIM, info);
		
		startQuestTimer("unlock_kelbim", respawnTime, null, null);
		startQuestTimer("end_kelbim", 1800000, null, null);
		
		return super.onKill(npc, killer, isPet);
	}
	
	public static void main(String[] args)
	{
		new Kelbim();
	}
}
