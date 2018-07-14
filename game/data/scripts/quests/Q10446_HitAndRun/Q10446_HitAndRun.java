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
package quests.Q10446_HitAndRun;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10445_AnImpendingThreat.Q10445_AnImpendingThreat;

/**
 * Hit and Run (10446)
 * @URL https://l2wiki.com/Hit_and_Run
 * @author Gigi
 */
public final class Q10446_HitAndRun extends Quest
{
	// NPCs
	private static final int BRUENER = 33840;
	// Monster
	private static final int NARVA_ORC_PREFECT = 23322;
	// Item
	private static final int ETERNAL_ENHANCEMENT_STONE = 35569;
	private static final int ELMORE_SUPPORT_BOX = 37020;
	// Misc
	private static final int MIN_LEVEL = 99;
	private static final String KILL_COUNT_VAR = "KillCounts";
	
	public Q10446_HitAndRun()
	{
		super(10446);
		addStartNpc(BRUENER);
		addTalkId(BRUENER);
		addKillId(NARVA_ORC_PREFECT);
		addCondMinLevel(MIN_LEVEL, "33840-00.htm");
		addCondCompletedQuest(Q10445_AnImpendingThreat.class.getSimpleName(), "33840-00.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "33840-02.htm":
			case "33840-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33840-04.htm":
			{
				qs.startQuest();
				break;
			}
			case "33840-07.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, ETERNAL_ENHANCEMENT_STONE, 1);
					giveItems(player, ELMORE_SUPPORT_BOX, 1);
					qs.exitQuest(false, true);
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
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "33840-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "33840-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "33840-06.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getNoQuestMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, 1, 3, npc);
		if ((qs != null) && qs.isCond(1))
		{
			int count = qs.getInt(KILL_COUNT_VAR);
			qs.set(KILL_COUNT_VAR, ++count);
			if (count >= 10)
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
				holder.add(new NpcLogListHolder(NARVA_ORC_PREFECT, false, killCounts));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}