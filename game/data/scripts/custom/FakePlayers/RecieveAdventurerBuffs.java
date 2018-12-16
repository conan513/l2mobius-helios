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
package custom.FakePlayers;

import com.l2jmobius.Config;
import com.l2jmobius.commons.util.CommonUtil;
import com.l2jmobius.gameserver.data.xml.impl.FakePlayerData;
import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.base.ClassId;
import com.l2jmobius.gameserver.model.holders.SkillHolder;
import com.l2jmobius.gameserver.model.skills.SkillCaster;

import ai.AbstractNpcAI;

/**
 * Town Fake Player walkers that receive buffs from Adventurer NPC.
 * @author Mobius
 */
public class RecieveAdventurerBuffs extends AbstractNpcAI
{
	// NPCs
	private static final int[] ADVENTURERS_GUIDE =
	{
		32327,
		33950,
	};
	private static final int[] FAKE_PLAYER_IDS =
	{
		80000
	};
	// Skills
	// private static final SkillHolder KNIGHT = new SkillHolder(15648, 1); // Knight's Harmony (Adventurer)
	private static final SkillHolder WARRIOR = new SkillHolder(15649, 1); // Warrior's Harmony (Adventurer)
	private static final SkillHolder WIZARD = new SkillHolder(15650, 1); // Wizard's Harmony (Adventurer)
	private static final SkillHolder[] GROUP_BUFFS =
	{
		new SkillHolder(15642, 1), // Horn Melody (Adventurer)
		new SkillHolder(15643, 1), // Drum Melody (Adventurer)
		new SkillHolder(15644, 1), // Pipe Organ Melody (Adventurer)
		new SkillHolder(15645, 1), // Guitar Melody (Adventurer)
		new SkillHolder(15646, 1), // Harp Melody (Adventurer)
		new SkillHolder(15647, 1), // Lute Melody (Adventurer)
		new SkillHolder(15651, 1), // Prevailing Sonata (Adventurer)
		new SkillHolder(15652, 1), // Daring Sonata (Adventurer)
		new SkillHolder(15653, 1), // Refreshing Sonata (Adventurer)
	};
	
	private RecieveAdventurerBuffs()
	{
		if (Config.FAKE_PLAYERS_ENABLED)
		{
			addSpawnId(FAKE_PLAYER_IDS);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.startsWith("AUTOBUFF") && (npc != null) && !npc.isDead())
		{
			if (!npc.isMoving())
			{
				for (L2Npc nearby : L2World.getInstance().getVisibleObjectsInRange(npc, L2Npc.class, 100))
				{
					if (CommonUtil.contains(ADVENTURERS_GUIDE, nearby.getId()))
					{
						for (SkillHolder holder : GROUP_BUFFS)
						{
							SkillCaster.triggerCast(nearby, npc, holder.getSkill());
						}
						if (ClassId.getClassId(FakePlayerData.getInstance().getInfo(npc.getId()).getClassId()).isMage())
						{
							SkillCaster.triggerCast(nearby, npc, WIZARD.getSkill());
						}
						else
						{
							SkillCaster.triggerCast(nearby, npc, WARRIOR.getSkill());
						}
						break;
					}
				}
			}
			startQuestTimer("AUTOBUFF" + npc.getObjectId(), 30000, npc, null);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		startQuestTimer("AUTOBUFF" + npc.getObjectId(), 1000, npc, null);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new RecieveAdventurerBuffs();
	}
}
