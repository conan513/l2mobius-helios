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
package quests.Q10790_AMercenaryHelper;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.base.ClassId;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;

/**
 * A Mercenary Helper (10790)
 * @URL https://l2wiki.com/A_Mercenary_Helper
 * @author Gigi
 */

public class Q10790_AMercenaryHelper extends Quest
{
	// NPCs
	private static final int DOKARA = 33847;
	// Monsters
	private static final int[] MONSTERS =
	{
		21508, // Splinter Stakato
		21509, // Splinter Stakato Worker
		21510, // Splinter Stakato Soldier
		21511, // Splinter Stakato Drone
		21512, // Splinter Stakato Drone
		21513, // Needle Stakato
		21514, // Needle Stakato Worker
		21515, // Needle Stakato Soldier
		21516, // Needle Stakato Drone
		21517, // Needle Stakato Drone
		21518, // Frenzied Stakato Soldier
		21519 // Frenzied Stakato Drone
	};
	// Items
	private static final int EAA = 730;
	// Misc
	private static final int MIN_LEVEL = 65;
	private static final int MAX_LEVEL = 70;
	private static final String KILL_COUNT_VAR = "KillCounts";
	
	public Q10790_AMercenaryHelper()
	{
		super(10790);
		addStartNpc(DOKARA);
		addTalkId(DOKARA);
		addKillId(MONSTERS);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "no_level.html");
		addCondRace(Race.ERTHEIA, "noErtheia.html");
		addCondClassId(ClassId.MARAUDER, "no_quest.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "33847-02.htm":
			case "33847-03.htm":
			{
				break;
			}
			case "33847-04.htm":
			{
				qs.startQuest();
				break;
			}
			default:
			{
				if (qs.isCond(2) && (player.getLevel() >= MIN_LEVEL))
				{
					qs.exitQuest(false, true);
					giveStoryQuestReward(player, 3);
					giveItems(player, EAA, 3);
					addExpAndSp(player, 942650, 226);
					htmltext = "33847-07.html";
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "33847-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "33847-05.html" : "33847-06.html";
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
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			int count = qs.getInt(KILL_COUNT_VAR);
			qs.set(KILL_COUNT_VAR, ++count);
			if (count >= 50)
			{
				qs.setCond(2, true);
			}
			else
			{
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
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
			final int killCounts = qs.getInt(KILL_COUNT_VAR);
			if (killCounts > 0)
			{
				final Set<NpcLogListHolder> holder = new HashSet<>();
				holder.add(new NpcLogListHolder(NpcStringId.KILL_STAKATOS, killCounts));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}