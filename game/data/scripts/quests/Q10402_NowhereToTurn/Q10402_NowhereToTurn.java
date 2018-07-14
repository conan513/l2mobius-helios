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
package quests.Q10402_NowhereToTurn;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;

/**
 * Nowhere to Turn (10402)
 * @author St3eT
 */
public final class Q10402_NowhereToTurn extends Quest
{
	// NPCs
	private static final int EBLUNE = 33865;
	private static final int[] MONSTERS =
	{
		20679, // Marsh Stalker
		20680, // Marsh Drake
		21017, // Fallen Orc
		21018, // Ancient Gargoyle
		21019, // Fallen Orc Archer
		21020, // Fallen Orc Shaman
		21021, // Sharp Talon Tiger
		21022, // Fallen Orc Captain
	};
	// Items
	private static final int EAB = 948; // Scroll: Enchant Armor (B-grade)
	// Misc
	private static final int MIN_LEVEL = 58;
	private static final int MAX_LEVEL = 61;
	
	public Q10402_NowhereToTurn()
	{
		super(10402);
		addStartNpc(EBLUNE);
		addTalkId(EBLUNE);
		addKillId(MONSTERS);
		addCondNotRace(Race.ERTHEIA, "33865-08.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33865-09.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "33865-02.htm":
			case "33865-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33865-04.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "33865-07.html":
			{
				if (st.isCond(2))
				{
					st.exitQuest(false, true);
					giveItems(player, EAB, 5);
					giveStoryQuestReward(player, 34);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 5_482_574, 1_315);
					}
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = "33865-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = st.isCond(1) ? "33865-05.html" : "33865-06.html";
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		
		if ((st != null) && st.isStarted() && st.isCond(1))
		{
			int killCount = st.getInt("KILLED_COUNT");
			
			if (killCount < 40)
			{
				killCount++;
				st.set("KILLED_COUNT", killCount);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			
			if (killCount == 40)
			{
				st.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance activeChar)
	{
		final QuestState st = getQuestState(activeChar, false);
		if ((st != null) && st.isStarted() && st.isCond(1))
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(1);
			npcLogList.add(new NpcLogListHolder(NpcStringId.ELIMINATE_MONSTERS_IN_THE_FORSAKEN_PLAINS, st.getInt("KILLED_COUNT")));
			return npcLogList;
		}
		return super.getNpcLogList(activeChar);
	}
}