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
package quests.Q10780_AWeakenedBarrier;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;

/**
 * A Weakened Barrier (10780)
 * @author malyelfik
 */
public final class Q10780_AWeakenedBarrier extends Quest
{
	// NPCs
	private static final int ANDY = 33845;
	private static final int BACON = 33846;
	// Monsters
	private static final int[] MONSTERS =
	{
		20555, // Giant Fungus
		20558, // Rotting tree
		23305, // Corroded Skeleton
		23306, // Rotten Corpse
		23307, // Corpse Spider
		23308, // Explosive Spider
	};
	// Items
	private static final int ENCHANT_ARMOR_B = 948;
	// Misc
	private static final int MIN_LEVEL = 52;
	private static final int MAX_LEVEL = 58;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10780_AWeakenedBarrier()
	{
		super(10780);
		addStartNpc(ANDY);
		addTalkId(ANDY, BACON);
		addKillId(MONSTERS);
		
		addCondRace(Race.ERTHEIA, "33845-01.htm");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33845-02.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "33845-04.htm":
			case "33845-05.htm":
			{
				break;
			}
			case "33845-06.htm":
			{
				qs.startQuest();
				break;
			}
			case "33846-03.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, ENCHANT_ARMOR_B, 5);
					giveStoryQuestReward(player, 36);
					addExpAndSp(player, 15108843, 914);
					qs.exitQuest(false, true);
				}
				break;
			}
			default:
			{
				htmltext = null;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == ANDY)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					htmltext = "33845-03.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "33845-07.html";
					}
					break;
				}
				case State.COMPLETED:
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
			}
		}
		else if (qs.isStarted())
		{
			htmltext = (qs.isCond(1)) ? "33846-01.html" : "33846-02.html";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			int count = qs.getInt(KILL_COUNT_VAR);
			if (count < 80)
			{
				qs.set(KILL_COUNT_VAR, ++count);
				if (count >= 80)
				{
					qs.setCond(2, true);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final int killCount = qs.getInt(KILL_COUNT_VAR);
			if (killCount > 0)
			{
				final Set<NpcLogListHolder> holder = new HashSet<>(1);
				holder.add(new NpcLogListHolder(NpcStringId.KILL_MONSTERS_NEAR_THE_SEA_OF_SPORES, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}