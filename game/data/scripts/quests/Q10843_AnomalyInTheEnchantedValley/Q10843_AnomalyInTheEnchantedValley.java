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
package quests.Q10843_AnomalyInTheEnchantedValley;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Anomaly in the Enchanted Valley (10843)
 * @URL https://l2wiki.com/Anomaly_in_the_Enchanted_Valley
 * @author Gigi
 */
public final class Q10843_AnomalyInTheEnchantedValley extends Quest
{
	// NPCs
	private static final int CRONOS = 30610;
	private static final int MIMYU = 30747;
	// Items
	private static final int SOE = 46257; // Scroll of Escape: Enchanted Valley
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q10843_AnomalyInTheEnchantedValley()
	{
		super(10843);
		addStartNpc(CRONOS);
		addTalkId(CRONOS, MIMYU);
		addCondMinLevel(MIN_LEVEL, "30610-00.htm");
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
			case "30610-02.htm":
			case "30610-03.htm":
			case "30610-04.htm":
			case "30747-02.html":
			{
				htmltext = event;
				break;
			}
			case "30610-05.htm":
			{
				qs.startQuest();
				giveItems(player, SOE, 1);
				showOnScreenMsg(player, NpcStringId.TALK_TO_MIMYU, ExShowScreenMessage.TOP_CENTER, 8000);
				htmltext = event;
				break;
			}
			case "30747-03.html":
			{
				giveItems(player, SOE, 3);
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
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == CRONOS)
				{
					htmltext = "30610-01.htm";
					break;
				}
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case CRONOS:
					{
						if (qs.isCond(1))
						{
							htmltext = "30610-06.html";
							break;
						}
					}
					case MIMYU:
					{
						if (qs.isCond(1))
						{
							htmltext = "30747-01.html";
							break;
						}
					}
				}
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
}