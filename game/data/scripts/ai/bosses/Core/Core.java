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
package ai.bosses.Core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.instancemanager.GlobalVariablesManager;
import com.l2jmobius.gameserver.instancemanager.GrandBossManager;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Attackable;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.PlaySound;

import ai.AbstractNpcAI;

/**
 * Core AI.
 * @author DrLecter, Emperorc
 */
public final class Core extends AbstractNpcAI
{
	private static final int CORE = 29006;
	private static final int DEATH_KNIGHT = 29007;
	private static final int DOOM_WRAITH = 29008;
	// private static final int DICOR = 29009;
	// private static final int VALIDUS = 29010;
	private static final int SUSCEPTOR = 29011;
	// private static final int PERUM = 29012;
	// private static final int PREMO = 29013;
	
	// Core Status Tracking :
	private static final byte ALIVE = 0; // Core is spawned.
	private static final byte DEAD = 1; // Core has been killed.
	
	private static boolean _firstAttacked;
	
	private final List<L2Attackable> _minions = new CopyOnWriteArrayList<>();
	
	private Core()
	{
		registerMobs(CORE, DEATH_KNIGHT, DOOM_WRAITH, SUSCEPTOR);
		
		_firstAttacked = false;
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(CORE);
		final int status = GrandBossManager.getInstance().getBossStatus(CORE);
		if (status == DEAD)
		{
			// load the unlock date and time for Core from DB
			final long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
			// if Core is locked until a certain time, mark it so and start the unlock timer
			// the unlock time has not yet expired.
			if (temp > 0)
			{
				startQuestTimer("core_unlock", temp, null, null);
			}
			else
			{
				// the time has already expired while the server was offline. Immediately spawn Core.
				final L2GrandBossInstance core = (L2GrandBossInstance) addSpawn(CORE, 17726, 108915, -6480, 0, false, 0);
				GrandBossManager.getInstance().setBossStatus(CORE, ALIVE);
				spawnBoss(core);
			}
		}
		else
		{
			final boolean test = GlobalVariablesManager.getInstance().getBoolean("Core_Attacked", false);
			if (test)
			{
				_firstAttacked = true;
			}
			final int loc_x = info.getInt("loc_x");
			final int loc_y = info.getInt("loc_y");
			final int loc_z = info.getInt("loc_z");
			final int heading = info.getInt("heading");
			final double hp = info.getDouble("currentHP");
			final double mp = info.getDouble("currentMP");
			final L2GrandBossInstance core = (L2GrandBossInstance) addSpawn(CORE, loc_x, loc_y, loc_z, heading, false, 0);
			core.setCurrentHpMp(hp, mp);
			spawnBoss(core);
		}
	}
	
	@Override
	public void onSave()
	{
		GlobalVariablesManager.getInstance().set("Core_Attacked", _firstAttacked);
	}
	
	public void spawnBoss(L2GrandBossInstance npc)
	{
		GrandBossManager.getInstance().addBoss(npc);
		npc.broadcastPacket(new PlaySound(1, "BS01_A", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
		// Spawn minions
		L2Attackable mob;
		for (int i = 0; i < 5; i++)
		{
			final int x = 16800 + (i * 360);
			mob = (L2Attackable) addSpawn(DEATH_KNIGHT, x, 110000, npc.getZ(), 280 + getRandom(40), false, 0);
			mob.setIsRaidMinion(true);
			_minions.add(mob);
			mob = (L2Attackable) addSpawn(DEATH_KNIGHT, x, 109000, npc.getZ(), 280 + getRandom(40), false, 0);
			mob.setIsRaidMinion(true);
			_minions.add(mob);
			final int x2 = 16800 + (i * 600);
			mob = (L2Attackable) addSpawn(DOOM_WRAITH, x2, 109300, npc.getZ(), 280 + getRandom(40), false, 0);
			mob.setIsRaidMinion(true);
			_minions.add(mob);
		}
		for (int i = 0; i < 4; i++)
		{
			final int x = 16800 + (i * 450);
			mob = (L2Attackable) addSpawn(SUSCEPTOR, x, 110300, npc.getZ(), 280 + getRandom(40), false, 0);
			mob.setIsRaidMinion(true);
			_minions.add(mob);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("core_unlock"))
		{
			final L2GrandBossInstance core = (L2GrandBossInstance) addSpawn(CORE, 17726, 108915, -6480, 0, false, 0);
			GrandBossManager.getInstance().setBossStatus(CORE, ALIVE);
			spawnBoss(core);
		}
		else if (event.equalsIgnoreCase("spawn_minion"))
		{
			final L2Attackable mob = (L2Attackable) addSpawn(npc.getId(), npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0);
			mob.setIsRaidMinion(true);
			_minions.add(mob);
		}
		else if (event.equalsIgnoreCase("despawn_minions"))
		{
			_minions.forEach(L2Attackable::decayMe);
			_minions.clear();
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (npc.getId() == CORE)
		{
			if (_firstAttacked)
			{
				if (getRandom(100) == 0)
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.REMOVING_INTRUDERS);
				}
			}
			else
			{
				_firstAttacked = true;
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.A_NON_PERMITTED_TARGET_HAS_BEEN_DISCOVERED);
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.INTRUDER_REMOVAL_SYSTEM_INITIATED);
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final int npcId = npc.getId();
		if (npcId == CORE)
		{
			npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.A_FATAL_ERROR_HAS_OCCURRED);
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.SYSTEM_IS_BEING_SHUT_DOWN);
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.EMPTY);
			_firstAttacked = false;
			GrandBossManager.getInstance().setBossStatus(CORE, DEAD);
			// Calculate Min and Max respawn times randomly.
			long respawnTime = (Config.CORE_SPAWN_INTERVAL + getRandom(-Config.CORE_SPAWN_RANDOM, Config.CORE_SPAWN_RANDOM)) * 3600000;
			respawnTime *= 3600000;
			
			startQuestTimer("core_unlock", respawnTime, null, null);
			// also save the respawn time so that the info is maintained past reboots
			final StatsSet info = GrandBossManager.getInstance().getStatsSet(CORE);
			info.set("respawn_time", (System.currentTimeMillis() + respawnTime));
			GrandBossManager.getInstance().setStatsSet(CORE, info);
			startQuestTimer("despawn_minions", 20000, null, null);
			cancelQuestTimers("spawn_minion");
		}
		else if ((GrandBossManager.getInstance().getBossStatus(CORE) == ALIVE) && (_minions != null) && _minions.contains(npc))
		{
			_minions.remove(npc);
			startQuestTimer("spawn_minion", 60000, npc, null);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc.getId() == CORE)
		{
			npc.setIsImmobilized(true);
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new Core();
	}
}
