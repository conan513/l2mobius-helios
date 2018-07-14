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
package quests.Q00756_TopQualityPetra;

import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * @author hlwrave
 */
public class Q00756_TopQualityPetra extends Quest
{
	// Npcs
	private final int AKU = 33671;
	// Items
	private final int AKU_MARK = 34910;
	private final int TOP_QUALITY_PETRA = 35703;
	private final int ZAHAK_PETRA = 35702;
	// Other
	private static final int MIN_LEVEL = 97;
	
	public Q00756_TopQualityPetra()
	{
		super(756);
		addTalkId(AKU);
		addItemTalkId(ZAHAK_PETRA);
		registerQuestItems(TOP_QUALITY_PETRA);
		addCondMinLevel(MIN_LEVEL, "sofa_aku_q0755_05.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		
		switch (event)
		{
			case "petra_of_zahaq_q0756_03.html":
			{
				qs.startQuest();
				takeItems(player, ZAHAK_PETRA, 1);
				giveItems(player, TOP_QUALITY_PETRA, 1);
			}
			case "sofa_aku_q0756_02.html":
			{
				takeItems(player, TOP_QUALITY_PETRA, -1);
				addExpAndSp(player, 570676680, 26102484);
				giveItems(player, AKU_MARK, 1);
				qs.exitQuest(QuestType.DAILY, true);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onItemTalk(L2ItemInstance item, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		boolean startQuest = false;
		switch (qs.getState())
		{
			case State.CREATED:
			{
				startQuest = true;
				break;
			}
			case State.COMPLETED:
			{
				if (qs.isNowAvailable())
				{
					qs.setState(State.CREATED);
					startQuest = true;
				}
				break;
			}
		}
		
		if (startQuest)
		{
			if (player.getLevel() >= MIN_LEVEL)
			{
				qs.startQuest();
				takeItems(player, ZAHAK_PETRA, 1);
				giveItems(player, TOP_QUALITY_PETRA, 1);
				htmltext = "petra_of_zahaq_q0756_03.html";
			}
			else
			{
				htmltext = "petra_of_zahaq_q0756_02.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (qs.isCond(1) && hasQuestItems(player, TOP_QUALITY_PETRA))
		{
			htmltext = "sofa_aku_q0756_01.html";
		}
		else
		{
			htmltext = "sofa_aku_q0756_03.html";
		}
		return htmltext;
	}
}