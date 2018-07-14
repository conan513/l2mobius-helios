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
package quests.Q10779_LettersFromTheQueenSeaOfSpores;

import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.LetterQuest;

/**
 * Letters from the Queen: Sea of Spores (10779)
 * @author malyelfik
 */
public final class Q10779_LettersFromTheQueenSeaOfSpores extends LetterQuest
{
	// NPCs
	private static final int HOLINT = 30191;
	private static final int ANDY = 33845;
	// Items
	private static final int SOE_OREN = 39574;
	private static final int SOE_SEA_OF_SPORES = 39575;
	private static final int ENCHANT_WEAPON_B = 947;
	// Location
	private static final Location TELEPORT_LOC = new Location(83633, 53064, -1456);
	// Misc
	private static final int MIN_LEVEL = 52;
	private static final int MAX_LEVEL = 57;
	
	public Q10779_LettersFromTheQueenSeaOfSpores()
	{
		super(10779);
		addTalkId(HOLINT, ANDY);
		
		setIsErtheiaQuest(true);
		setLevel(MIN_LEVEL, MAX_LEVEL);
		setStartLocation(SOE_OREN, TELEPORT_LOC);
		setStartQuestSound("Npcdialog1.serenia_quest_5");
		registerQuestItems(SOE_OREN, SOE_SEA_OF_SPORES);
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
			case "30191-02.html":
			case "33845-02.html":
			{
				break;
			}
			case "30191-03.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
					giveItems(player, SOE_SEA_OF_SPORES, 1);
				}
				break;
			}
			case "33845-03.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, ENCHANT_WEAPON_B, 3);
					giveStoryQuestReward(player, 37);
					addExpAndSp(player, 635250, 152);
					showOnScreenMsg(player, NpcStringId.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_QUEEN_NAVARI_AT_LV_58, ExShowScreenMessage.TOP_CENTER, 8000);
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
		final QuestState qs = getQuestState(player, false);
		String htmltext = getNoQuestMsg(player);
		if (qs == null)
		{
			return htmltext;
		}
		
		if (qs.isStarted())
		{
			if (npc.getId() == HOLINT)
			{
				htmltext = (qs.isCond(1)) ? "30191-01.html" : "30191-04.html";
			}
			else if (qs.isCond(2))
			{
				htmltext = "33845-01.html";
			}
		}
		return htmltext;
	}
}
