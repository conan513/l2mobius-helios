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
package ai.areas.GainakUnderground;

import com.l2jmobius.Config;
import com.l2jmobius.commons.concurrent.ThreadPool;
import com.l2jmobius.gameserver.instancemanager.ZoneManager;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.ListenerRegisterType;
import com.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import com.l2jmobius.gameserver.model.events.annotations.RegisterType;
import com.l2jmobius.gameserver.model.events.impl.character.OnCreatureDeath;
import com.l2jmobius.gameserver.model.zone.L2ZoneType;
import com.l2jmobius.gameserver.model.zone.type.L2PeaceZone;
import com.l2jmobius.gameserver.model.zone.type.L2SiegeZone;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmobius.gameserver.network.serverpackets.OnEventTrigger;
import com.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import com.l2jmobius.gameserver.network.serverpackets.UserInfo;
import com.l2jmobius.gameserver.util.Broadcast;

import ai.AbstractNpcAI;

/**
 * @author LasTravel, Gigi
 * @URL http://l2wiki.com/Gainak
 */
public final class GainakSiege extends AbstractNpcAI
{
	private static final int SIEGE_EFFECT = 20140700;
	private static final int SIEGE_DURATION = 30;
	private static final L2SiegeZone GAINAK_SIEGE_ZONE = ZoneManager.getInstance().getZoneById(60019, L2SiegeZone.class);
	private static final L2PeaceZone GAINAK_TOWN_ZONE = ZoneManager.getInstance().getZoneById(60020, L2PeaceZone.class);
	protected static final int[] ASSASSIN_IDS =
	{
		19471,
		19472,
		19473
	};
	private static final Location[] ASSASSIN_SPAWNS =
	{
		new Location(17085, -115385, -249, 41366),
		new Location(15452, -114531, -243, 5464),
		new Location(15862, -113121, -250, 53269)
	};
	private boolean _isInSiege = false;
	
	public GainakSiege()
	{
		addEnterZoneId(GAINAK_SIEGE_ZONE.getId(), GAINAK_TOWN_ZONE.getId());
		addKillId(ASSASSIN_IDS);
		addSpawnId(ASSASSIN_IDS);
		startQuestTimer("GAINAK_WAR", getTimeBetweenSieges() * 60000, null, null);
	}
	
	private final int getTimeBetweenSieges()
	{
		return getRandom(120, 180); // 2 to 3 hours.
	}
	
	@Override
	public final String onEnterZone(L2Character character, L2ZoneType zone)
	{
		if (_isInSiege && character.isPlayer())
		{
			character.broadcastPacket(new OnEventTrigger(SIEGE_EFFECT, true));
		}
		return super.onEnterZone(character, zone);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("GAINAK_WAR"))
		{
			if (_isInSiege)
			{
				_isInSiege = false;
				GAINAK_TOWN_ZONE.setEnabled(true); // enable before broadcast
				GAINAK_TOWN_ZONE.broadcastPacket(new OnEventTrigger(SIEGE_EFFECT, false));
				GAINAK_TOWN_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.GAINAK_IN_PEACE, ExShowScreenMessage.TOP_CENTER, 5000, true));
				GAINAK_SIEGE_ZONE.setIsActive(false);
				GAINAK_SIEGE_ZONE.updateZoneStatusForCharactersInside();
				startQuestTimer("GAINAK_WAR", getTimeBetweenSieges() * 60000, null, null);
				if (Config.ANNOUNCE_GAINAK_SIEGE)
				{
					SystemMessage s = SystemMessage.getSystemMessage(SystemMessageId.PROGRESS_EVENT_STAGE_S1);
					s.addString("Gainak is now in peace.");
					Broadcast.toAllOnlinePlayers(s);
				}
			}
			else
			{
				for (Location loc : ASSASSIN_SPAWNS)
				{
					addSpawn(ASSASSIN_IDS[getRandom(ASSASSIN_IDS.length)], loc, true, 1800000);
				}
				_isInSiege = true;
				GAINAK_TOWN_ZONE.broadcastPacket(new OnEventTrigger(SIEGE_EFFECT, true));
				GAINAK_TOWN_ZONE.broadcastPacket(new ExShowScreenMessage(NpcStringId.GAINAK_IN_WAR, ExShowScreenMessage.TOP_CENTER, 5000, true));
				GAINAK_TOWN_ZONE.setEnabled(false); // disable after broadcast
				GAINAK_SIEGE_ZONE.setIsActive(true);
				GAINAK_SIEGE_ZONE.updateZoneStatusForCharactersInside();
				startQuestTimer("GAINAK_WAR", SIEGE_DURATION * 60000, null, null);
				if (Config.ANNOUNCE_GAINAK_SIEGE)
				{
					SystemMessage s = SystemMessage.getSystemMessage(SystemMessageId.PROGRESS_EVENT_STAGE_S1);
					s.addString("Gainak is now under siege.");
					Broadcast.toAllOnlinePlayers(s);
				}
				ZoneManager.getInstance().getZoneById(GAINAK_TOWN_ZONE.getId(), L2PeaceZone.class).setEnabled(false);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final L2SiegeZone zone = ZoneManager.getInstance().getZone(npc, L2SiegeZone.class);
		if ((zone != null) && (zone.getId() == 60019) && zone.isActive())
		{
			ThreadPool.schedule(new RespawnNewAssassin(npc.getLocation()), 60000);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private class RespawnNewAssassin implements Runnable
	{
		private final Location _loc;
		
		public RespawnNewAssassin(Location loc)
		{
			_loc = loc;
		}
		
		@Override
		public void run()
		{
			addSpawn(ASSASSIN_IDS[getRandom(ASSASSIN_IDS.length)], _loc, true, 1800000);
		}
	}
	
	@RegisterEvent(EventType.ON_CREATURE_DEATH)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerDeath(OnCreatureDeath event)
	{
		if (_isInSiege && GAINAK_SIEGE_ZONE.getCharactersInside().contains(event.getTarget()))
		{
			if (event.getAttacker().isPlayer() && event.getTarget().isPlayer())
			{
				final L2PcInstance attackerPlayer = event.getAttacker().getActingPlayer();
				attackerPlayer.setPvpKills(attackerPlayer.getPvpKills() + 1);
				attackerPlayer.sendPacket(new UserInfo(attackerPlayer));
			}
		}
	}
	
	public static void main(String[] args)
	{
		new GainakSiege();
	}
}