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
package quests.Q00760_BlockTheExit;

import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * Block the Exit (760)
 * @author St3eT
 */
public final class Q00760_BlockTheExit extends Quest
{
	// NPCs
	private static final int KURTIZ = 30870;
	private static final int DARK_RIDER = 26102;
	// Items
	private static final int REWARD_BOX = 46560; // Curtiz's Reward Box
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q00760_BlockTheExit()
	{
		super(760);
		addStartNpc(KURTIZ);
		addTalkId(KURTIZ);
		addKillId(DARK_RIDER);
		addCondMinLevel(MIN_LEVEL, "30870-07.htm");
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
			case "30870-02.htm":
			case "30870-03.htm":
			{
				htmltext = event;
				break;
			}
			case "30870-04.html":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "30870-06.html":
			{
				if (st.isCond(2))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						st.exitQuest(QuestType.DAILY, true);
						giveItems(player, REWARD_BOX, 1);
						htmltext = event;
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
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
				htmltext = "30870-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = st.isCond(2) ? "30870-05.html" : "30870-04.html";
				break;
			}
			case State.COMPLETED:
			{
				if (st.isNowAvailable())
				{
					st.setState(State.CREATED);
					htmltext = "30870-01.htm";
				}
				else
				{
					htmltext = getAlreadyCompletedMsg(player, QuestType.DAILY);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		executeForEachPlayer(killer, npc, isSummon, true, false);
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon)
	{
		final QuestState st = getQuestState(player, false);
		if ((st != null) && st.isCond(1))
		{
			st.setCond(2, true);
		}
		super.actionForEachPlayer(player, npc, isSummon);
	}
}