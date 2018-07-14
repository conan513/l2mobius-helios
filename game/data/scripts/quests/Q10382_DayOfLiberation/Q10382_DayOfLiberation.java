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
package quests.Q10382_DayOfLiberation;

import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.util.Util;

import quests.Q10381_ToTheSeedOfHellfire.Q10381_ToTheSeedOfHellfire;

/**
 * @author hlwrave
 */
public class Q10382_DayOfLiberation extends Quest
{
	// NPCs
	private static final int SIZRAK = 33669;
	private static final int TAUTI = 29236;
	// Items
	private static final int TAUTIS_BRACELET = 35293;
	// Misc
	private static final int MIN_LEVEL = 97;
	
	public Q10382_DayOfLiberation()
	{
		super(10382);
		addStartNpc(SIZRAK);
		addTalkId(SIZRAK);
		addKillId(TAUTI);
		addCondMinLevel(MIN_LEVEL, "sofa_sizraku_q10382_04.html");
		addCondCompletedQuest(Q10381_ToTheSeedOfHellfire.class.getSimpleName(), "sofa_sizraku_q10382_05.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		switch (event)
		{
			case "sofa_sizraku_q10382_02.htm":
			case "sofa_sizraku_q10382_09.html":
			{
				htmltext = event;
				break;
			}
			case "sofa_sizraku_q10382_03.html":
			{
				qs.startQuest();
				qs.set(Integer.toString(TAUTI), 0);
				htmltext = event;
				break;
			}
			case "sofa_sizraku_q10382_10.html":
			{
				addExpAndSp(player, 951127800, 435041400);
				giveAdena(player, 3256740, true);
				giveItems(player, TAUTIS_BRACELET, 1);
				qs.exitQuest(QuestType.ONE_TIME, true);
				htmltext = event;
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
		
		if (qs.isCreated())
		{
			htmltext = "sofa_sizraku_q10382_01.htm";
		}
		else if (qs.isStarted())
		{
			if (qs.isCond(1))
			{
				htmltext = "sofa_sizraku_q10382_07.html";
			}
			else if (qs.isCond(2))
			{
				htmltext = "sofa_sizraku_q10382_08.html";
			}
		}
		else if (qs.isCompleted())
		{
			htmltext = "sofa_sizraku_q10382_06.html";
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if ((qs != null) && qs.isStarted() && qs.isCond(1) && Util.checkIfInRange(1500, npc, qs.getPlayer(), false))
		{
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}