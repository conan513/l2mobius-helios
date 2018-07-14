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
package quests.Q00488_WondersOfCaring;

import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * Wonders of Caring (488)
 * @author St3eT
 */
public final class Q00488_WondersOfCaring extends Quest
{
	// NPCs
	private static final int ADVENTURER = 32327;
	private static final int DOLPHREN = 32880;
	private static final int[] MONSTERS =
	{
		20965, // Chimera Piece
		20970, // Soldier of Ancient Times
		20966, // Mutated Creation
		20971, // Warrior of Ancient Times
		20972, // Shaman of Ancient Times
		20967, // Creature of the Past
		20973, // Forgotten Ancient People
		20968, // Forgotten Face
		20969, // Giant's Shadow
	};
	// Items
	private static final int BOX = 19500; // Relic Box
	// Misc
	private static final int MIN_LEVEL = 75;
	private static final int MAX_LEVEL = 79;
	
	public Q00488_WondersOfCaring()
	{
		super(488);
		addStartNpc(ADVENTURER);
		addTalkId(ADVENTURER, DOLPHREN);
		addKillId(MONSTERS);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "");
		registerQuestItems(BOX);
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
			case "32327-02.htm":
			case "32327-03.htm":
			{
				htmltext = event;
				break;
			}
			case "32327-04.htm":
			{
				st.startQuest();
				htmltext = event;
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
				if (npc.getId() == ADVENTURER)
				{
					htmltext = "32327-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					htmltext = npc.getId() == ADVENTURER ? "32327-05.html" : "32880-01.html";
				}
				else if (st.isCond(2))
				{
					if (npc.getId() == ADVENTURER)
					{
						htmltext = "32327-05.html";
					}
					else if (npc.getId() == DOLPHREN)
					{
						st.exitQuest(QuestType.DAILY, true);
						giveAdena(player, 490_545, true);
						if (player.getLevel() >= MIN_LEVEL)
						{
							addExpAndSp(player, 22_901_550, 5_496);
						}
						htmltext = "32880-02.html";
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if ((npc.getId() == ADVENTURER) && st.isNowAvailable())
				{
					st.setState(State.CREATED);
					htmltext = "32327-01.htm";
				}
				else if ((npc.getId() == DOLPHREN) && st.isCompleted() && !st.isNowAvailable())
				{
					htmltext = "32880-03.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		
		if ((st != null) && st.isCond(1))
		{
			if (giveItemRandomly(killer, BOX, 1, 50, 0.4, true))
			{
				st.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}