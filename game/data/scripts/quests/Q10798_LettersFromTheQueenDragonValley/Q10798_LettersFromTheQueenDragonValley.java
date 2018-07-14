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
package quests.Q10798_LettersFromTheQueenDragonValley;

import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.LetterQuest;

/**
 * Letters from the Queen: Dragon Valley (10798)
 * @URL https://l2wiki.com/Letters_from_the_Queen:_Dragon_Valley
 * @author Gigi
 */
public class Q10798_LettersFromTheQueenDragonValley extends LetterQuest
{
	// NPCs
	private static final int MAXIMILIAN = 30120;
	private static final int NAMO = 33973;
	// Items
	private static final int SOE_DRAGON_VALLEY = 39587;
	private static final int SOE_TOWN_OF_GIRAN = 39586;
	private static final int EWS = 959;
	// Misc
	private static final int MIN_LEVEL = 81;
	private static final int MAX_LEVEL = 84;
	// Teleport
	private static final Location TELEPORT_LOC = new Location(86674, 148630, -3401);
	
	public Q10798_LettersFromTheQueenDragonValley()
	{
		super(10798);
		addTalkId(MAXIMILIAN, NAMO);
		setIsErtheiaQuest(true);
		setLevel(MIN_LEVEL, MAX_LEVEL);
		setStartLocation(SOE_TOWN_OF_GIRAN, TELEPORT_LOC);
		setStartQuestSound("Npcdialog1.serenia_quest_11");
		registerQuestItems(SOE_TOWN_OF_GIRAN, SOE_DRAGON_VALLEY);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30120-02.html":
			case "33973-02.html":
			{
				htmltext = event;
				break;
			}
			case "30120-03.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
					giveItems(player, SOE_DRAGON_VALLEY, 1);
					htmltext = event;
				}
				break;
			}
			case "33973-03.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, EWS, 2);
					giveStoryQuestReward(player, 182);
					addExpAndSp(player, 1277640, 306);
					showOnScreenMsg(player, NpcStringId.YOU_HAVE_FINISHED_ALL_OF_QUEEN_NAVARI_S_LETTERS_GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_LETTERS_FROM_A_MINSTREL_AT_LV_85, ExShowScreenMessage.TOP_CENTER, 8000);
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
		if (qs == null)
		{
			return htmltext;
		}
		if (qs.isStarted())
		{
			if (npc.getId() == MAXIMILIAN)
			{
				htmltext = (qs.isCond(1)) ? "30120-01.html" : "30120-04.html";
			}
			else if (qs.isCond(2))
			{
				htmltext = "33973-01.html";
			}
		}
		return htmltext;
	}
}