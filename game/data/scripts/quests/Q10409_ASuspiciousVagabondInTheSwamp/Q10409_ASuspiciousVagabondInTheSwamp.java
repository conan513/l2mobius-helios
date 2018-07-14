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
package quests.Q10409_ASuspiciousVagabondInTheSwamp;

import com.l2jmobius.gameserver.enums.CategoryType;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * A Suspicious Vagabond in the Swamp (10409)
 * @author St3eT
 */
public final class Q10409_ASuspiciousVagabondInTheSwamp extends Quest
{
	// NPCs
	private static final int DOKARA = 33847;
	private static final int VAGABOND = 33848; // Critically-injured Suspicious Vagabond
	// Items
	private static final int EAA = 730; // Scroll: Enchant Armor (A-grade)
	// Misc
	private static final int MIN_LEVEL = 65;
	private static final int MAX_LEVEL = 70;
	
	public Q10409_ASuspiciousVagabondInTheSwamp()
	{
		super(10409);
		addStartNpc(DOKARA);
		addTalkId(DOKARA, VAGABOND);
		addCondNotRace(Race.ERTHEIA, "33847-09.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33847-08.htm");
		addCondInCategory(CategoryType.FIGHTER_GROUP, "33847-08.htm");
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
			case "33847-02.htm":
			case "33847-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33847-04.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "33847-07.html":
			{
				if (st.isCond(2))
				{
					st.exitQuest(false, true);
					giveItems(player, EAA, 3);
					giveStoryQuestReward(player, 3);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 942_690, 226);
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
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == DOKARA)
				{
					htmltext = "33847-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == DOKARA)
				{
					htmltext = st.isCond(1) ? "33847-05.html" : "33847-06.html";
				}
				else if ((npc.getId() == VAGABOND) && st.isCond(1))
				{
					st.setCond(2, true);
					htmltext = "33848-01.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == DOKARA)
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
		}
		return htmltext;
	}
}