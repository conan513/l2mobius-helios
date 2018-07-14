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
package quests.Q10315_ToThePrisonOfDarkness;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10307_TheCorruptedLeaderHisTruth.Q10307_TheCorruptedLeaderHisTruth;
import quests.Q10311_PeacefulDaysAreOver.Q10311_PeacefulDaysAreOver;

/**
 * To the Prison of Darkness (10315)
 * @URL https://l2wiki.com/To_the_Prison_of_Darkness
 * @author Gigi
 */
public final class Q10315_ToThePrisonOfDarkness extends Quest
{
	// NPCs
	private static final int SLASKI = 32893;
	private static final int OPERA = 32946;
	// Misc
	private static final int MIN_LEVEL = 90;
	// Item's
	private static final int EWR = 17526;
	
	public Q10315_ToThePrisonOfDarkness()
	{
		super(10315);
		addStartNpc(SLASKI);
		addTalkId(SLASKI, OPERA);
		addCondMinLevel(MIN_LEVEL, "32893-00.htm");
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
			case "32893-02.htm":
			case "32893-03.htm":
			case "32946-02.html":
			{
				htmltext = event;
				break;
			}
			case "32893-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32946-03.html":
			{
				giveAdena(player, 279513, false);
				giveItems(player, EWR, 1);
				addExpAndSp(player, 4038093, 969);
				qs.exitQuest(false, true);
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
		final QuestState qs1 = player.getQuestState(Q10307_TheCorruptedLeaderHisTruth.class.getSimpleName());
		final QuestState qs2 = player.getQuestState(Q10311_PeacefulDaysAreOver.class.getSimpleName());
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == SLASKI)
				{
					if (((qs1 != null) && qs1.isCompleted()) || ((qs2 != null) && qs2.isCompleted()))
					{
						htmltext = "32893-01.htm";
						break;
					}
					htmltext = "32893-00.htm";
					break;
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case SLASKI:
					{
						if (qs.isCond(1))
						{
							htmltext = "32893-05.html";
							break;
						}
					}
					case OPERA:
					{
						if (qs.isCond(1))
						{
							htmltext = "32946-01.html";
							break;
						}
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				switch (npc.getId())
				{
					case SLASKI:
					{
						htmltext = "Complete.html";
						break;
					}
					case OPERA:
					{
						htmltext = "32946-00.html";
						break;
					}
				}
			}
		}
		return htmltext;
	}
}