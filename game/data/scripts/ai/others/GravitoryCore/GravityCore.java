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
package ai.others.GravitoryCore;

import com.l2jmobius.gameserver.instancemanager.WarpedSpaceManager;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.SkillHolder;

import ai.AbstractNpcAI;

/**
 * @author Sdw
 */
public class GravityCore extends AbstractNpcAI
{
	// NPCs
	private static final int GRAVITY_CORE = 13432; // Gravity Core
	
	private GravityCore()
	{
		addSpawnId(GRAVITY_CORE);
		addDespawnId(GRAVITY_CORE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("DELETE_ME") && (npc != null))
		{
			npc.deleteMe();
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		final L2Character summoner = npc.getSummoner();
		if ((summoner != null) && summoner.isPlayer())
		{
			final L2PcInstance player = summoner.getActingPlayer();
			final SkillHolder skill = npc.getParameters().getSkillHolder("skill");
			if (skill != null)
			{
				npc.doCast(skill.getSkill());
			}
			
			final int despawnTime = npc.getTemplate().getParameters().getInt("i_despawn_time", 30000);
			startQuestTimer("DELETE_ME", despawnTime, npc, player);
			
			WarpedSpaceManager.getInstance().addWarpedSpace(npc, 100);
		}
		
		return super.onSpawn(npc);
	}
	
	@Override
	public void onNpcDespawn(L2Npc npc)
	{
		WarpedSpaceManager.getInstance().removeWarpedSpace(npc);
	}
	
	public static void main(String[] args)
	{
		new GravityCore();
	}
}
