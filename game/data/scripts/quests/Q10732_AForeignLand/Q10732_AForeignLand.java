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
package quests.Q10732_AForeignLand;

import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.network.serverpackets.ExShowUsm;
import com.l2jmobius.gameserver.network.serverpackets.TutorialShowHtml;

/**
 * A Foreign Land (10732)
 * @author Sdw
 */
public final class Q10732_AForeignLand extends Quest
{
	// NPCs
	private static final int NAVARI = 33931;
	private static final int GERETH = 33932;
	// Misc
	private static final int MAX_LEVEL = 20;
	
	public Q10732_AForeignLand()
	{
		super(10732);
		addStartNpc(NAVARI);
		addTalkId(NAVARI, GERETH);
		addCondRace(Race.ERTHEIA, "");
		addCondMaxLevel(MAX_LEVEL, "33931-00.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "33931-02.htm":
			{
				break;
			}
			case "33931-03.htm":
			{
				qs.startQuest();
				qs.setCond(2); // arrow hack
				qs.setCond(1);
				player.sendPacket(ExShowUsm.ERTHEIA_FIRST_QUEST);
				break;
			}
			case "33932-02.html":
			{
				if (qs.isStarted())
				{
					player.sendPacket(new TutorialShowHtml(npc.getObjectId(), "..\\L2Text\\QT_001_Radar_01.htm", TutorialShowHtml.LARGE_WINDOW));
					giveAdena(player, 3000, true);
					addExpAndSp(player, 75, 2);
					qs.exitQuest(false, true);
				}
				break;
			}
			default:
			{
				htmltext = null;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		if (st.isCompleted())
		{
			return getAlreadyCompletedMsg(player);
		}
		
		String htmltext = getNoQuestMsg(player);
		switch (npc.getId())
		{
			case NAVARI:
			{
				if (st.isCreated())
				{
					htmltext = "33931-01.htm";
				}
				else if (st.isStarted())
				{
					htmltext = "33931-04.html";
				}
				break;
			}
			case GERETH:
			{
				if (st.isStarted())
				{
					htmltext = "33932-01.html";
				}
				break;
			}
		}
		return htmltext;
	}
}