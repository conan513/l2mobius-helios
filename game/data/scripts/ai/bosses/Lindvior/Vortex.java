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
package ai.bosses.Lindvior;

import java.util.Collection;

import com.l2jmobius.gameserver.ai.CtrlIntention;
import com.l2jmobius.gameserver.geoengine.GeoEngine;
import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.serverpackets.FlyToLocation;
import com.l2jmobius.gameserver.network.serverpackets.ValidateLocation;

import ai.AbstractNpcAI;

/**
 * Vortex AI
 * @author Gigi
 * @date 2017-07-23 - [10:32:50]
 */
public class Vortex extends AbstractNpcAI
{
	private static final int SMALL_VORTEX = 25898;
	private static final int BIG_VORTEX = 19427;
	
	public Vortex()
	{
		super();
		addSeeCreatureId(SMALL_VORTEX, BIG_VORTEX);
		addSpawnId(SMALL_VORTEX, BIG_VORTEX);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "rnd_small":
			{
				L2World.getInstance().forEachVisibleObjectInRange(npc, L2PcInstance.class, 250, attackers ->
				{
					if ((attackers != null) && !attackers.isDead() && !attackers.isAlikeDead())
					{
						attackers.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
						final int radians = (int) Math.toRadians(npc.calculateDirectionTo(attackers));
						final int x = (int) (attackers.getX() + (600 * Math.cos(radians)));
						final int y = (int) (attackers.getY() + (600 * Math.sin(radians)));
						final int z = attackers.getZ();
						final Location loc = GeoEngine.getInstance().canMoveToTargetLoc(attackers.getX(), attackers.getY(), attackers.getZ(), x, y, z, attackers.getInstanceWorld());
						attackers.broadcastPacket(new FlyToLocation(attackers, x, y, z, FlyToLocation.FlyType.THROW_UP, 800, 800, 800));
						attackers.setXYZ(loc);
						attackers.broadcastPacket(new ValidateLocation(attackers));
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
						startQuestTimer("stop_knock_down", 5000, npc, attackers);
						startQuestTimer("despawn_small", 5000, npc, null);
					}
				});
				break;
			}
			case "rnd_big":
			{
				L2World.getInstance().forEachVisibleObjectInRange(npc, L2PcInstance.class, 500, attackers ->
				{
					if ((attackers != null) && !attackers.isDead() && !attackers.isAlikeDead())
					{
						attackers.setCurrentHp(1.0);
						attackers.setCurrentMp(1.0);
						attackers.setCurrentCp(1.0);
						startQuestTimer("despawn_big", 600000, npc, null);
					}
				});
				break;
			}
			case "despawn_small":
			{
				if (npc != null)
				{
					cancelQuestTimers("rnd_small");
					npc.getSpawn().stopRespawn();
					npc.doDie(null);
				}
				break;
			}
			case "despawn_big":
			{
				if (npc != null)
				{
					cancelQuestTimers("despawn_big");
					npc.getSpawn().stopRespawn();
					npc.deleteMe();
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSeeCreature(L2Npc npc, L2Character player, boolean isSummon)
	{
		switch (npc.getId())
		{
			case SMALL_VORTEX:
			{
				startQuestTimer("rnd_small", 5000, npc, null, true);
				break;
			}
			case BIG_VORTEX:
			{
				startQuestTimer("rnd_big", 10000, npc, null, true);
				break;
			}
		}
		return super.onSeeCreature(npc, player, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getId())
		{
			case SMALL_VORTEX:
			{
				attackRandomTarget(npc);
				npc.setRandomWalking(true);
				npc.setRunning();
				break;
			}
			case BIG_VORTEX:
			{
				attackRandomTarget(npc);
				npc.setRandomWalking(true);
				npc.setRunning();
				break;
			}
		}
		return super.onSpawn(npc);
	}
	
	private void attackRandomTarget(L2Npc npc)
	{
		final Collection<L2PcInstance> players = L2World.getInstance().getVisibleObjects(npc, L2PcInstance.class);
		{
			if ((players == null) || players.isEmpty())
			{
				return;
			}
			if (players.size() > 0)
			{
				addAttackPlayerDesire(npc, players.stream().findAny().get());
			}
		}
	}
	
	public static void main(String[] args)
	{
		new Vortex();
	}
}
