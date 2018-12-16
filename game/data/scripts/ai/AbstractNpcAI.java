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
package ai;

import java.util.logging.Logger;

import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.MinionHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.util.Util;

/**
 * Abstract NPC AI class for datapack based AIs.
 * @author UnAfraid, Zoey76
 */
public abstract class AbstractNpcAI extends Quest
{
	protected final Logger LOGGER = Logger.getLogger(getClass().getName());
	
	public AbstractNpcAI()
	{
		super(-1);
	}
	
	/**
	 * Simple on first talk event handler.
	 */
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return npc.getId() + ".html";
	}
	
	/**
	 * Registers the following events to the current script:<br>
	 * <ul>
	 * <li>ON_ATTACK</li>
	 * <li>ON_KILL</li>
	 * <li>ON_SPAWN</li>
	 * <li>ON_SPELL_FINISHED</li>
	 * <li>ON_SKILL_SEE</li>
	 * <li>ON_FACTION_CALL</li>
	 * <li>ON_AGGR_RANGE_ENTER</li>
	 * </ul>
	 * @param mobs
	 */
	public void registerMobs(int... mobs)
	{
		addAttackId(mobs);
		addKillId(mobs);
		addSpawnId(mobs);
		addSpellFinishedId(mobs);
		addSkillSeeId(mobs);
		addAggroRangeEnterId(mobs);
		addFactionCallId(mobs);
	}
	
	public void spawnMinions(L2Npc npc, String spawnName)
	{
		for (MinionHolder is : npc.getParameters().getMinionList(spawnName))
		{
			addMinion((L2MonsterInstance) npc, is.getId());
		}
	}
	
	protected void followNpc(final L2Npc npc, int followedNpcId, int followingAngle, int minDistance, int maxDistance)
	{
		L2World.getInstance().forEachVisibleObject(npc, L2Npc.class, npcAround ->
		{
			if (npcAround.getId() != followedNpcId)
			{
				return;
			}
			
			final double distance = npc.calculateDistance3D(npcAround);
			if ((distance >= maxDistance) && npc.isScriptValue(0))
			{
				npc.setRunning();
				npc.setScriptValue(1);
			}
			else if ((distance <= (minDistance * 1.5)) && npc.isScriptValue(1))
			{
				npc.setWalking();
				npc.setScriptValue(0);
			}
			
			final double course = Math.toRadians(followingAngle);
			final double radian = Math.toRadians(Util.convertHeadingToDegree(npcAround.getHeading()));
			final double nRadius = npc.getCollisionRadius() + npcAround.getCollisionRadius() + minDistance;
			final int x = npcAround.getLocation().getX() + (int) (Math.cos(Math.PI + radian + course) * nRadius);
			final int y = npcAround.getLocation().getY() + (int) (Math.sin(Math.PI + radian + course) * nRadius);
			final int z = npcAround.getLocation().getZ();
			
			npc.getAI().moveTo(new Location(x, y, z));
		});
	}
}