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
package quests.Q10543_SheddingWeight;

import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmobius.gameserver.network.serverpackets.TutorialShowHtml;

import quests.Q10542_SearchingForNewPower.Q10542_SearchingForNewPower;

/**
 * Shedding Weight (10543)
 * @URL https://l2wiki.com/Shedding_Weight
 * @author Gigi
 */
public final class Q10543_SheddingWeight extends Quest
{
	// NPCs
	private static final int SHANNON = 32974;
	private static final int WILFORD = 30005;
	// Items
	// private static final int NOVICE_TRAINING_LOG = 1835; // TODO Find item ID
	private static final int APPRENTICE_ADVENTURERS_KNIFE = 7818;
	private static final int APPRENTICE_ADVENTURERS_LONG_SWORD = 7821;
	// Misc
	private static final int MAX_LEVEL = 20;
	
	public Q10543_SheddingWeight()
	{
		super(10543);
		addStartNpc(SHANNON);
		addTalkId(SHANNON, WILFORD);
		// registerQuestItems(NOVICE_TRAINING_LOG);
		addCondNotRace(Race.ERTHEIA, "noRace.html");
		addCondMaxLevel(MAX_LEVEL, "noLevel.html");
		addCondCompletedQuest(Q10542_SearchingForNewPower.class.getSimpleName(), "noLevel.html");
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
			case "32974-02.htm":
			case "30005-02.html":
			{
				htmltext = event;
				break;
			}
			case "32974-03.htm":
			{
				qs.startQuest();
				qs.setCond(2); // arrow hack
				qs.setCond(1);
				// giveItems(player, NOVICE_TRAINING_LOG, 1);
				htmltext = event;
				break;
			}
			case "30005-03.html":
			{
				giveItems(player, APPRENTICE_ADVENTURERS_KNIFE, 1);
				giveItems(player, APPRENTICE_ADVENTURERS_LONG_SWORD, 1);
				player.sendPacket(new TutorialShowHtml(npc.getObjectId(), "..\\L2Text\\QT_007_post_01.htm", TutorialShowHtml.LARGE_WINDOW));
				showOnScreenMsg(player, NpcStringId.WEAPONS_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY, ExShowScreenMessage.TOP_CENTER, 10000);
				addExpAndSp(player, 2630, 9);
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
				if (npc.getId() == SHANNON)
				{
					htmltext = "32974-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case SHANNON:
					{
						if (qs.isCond(1))
						{
							htmltext = "32974-04.html";
						}
						else if (qs.isCond(5))
						{
							htmltext = "32974-05.html";
						}
						break;
					}
					case WILFORD:
					{
						if (qs.isCond(1))
						{
							htmltext = "30005-01.html";
						}
						break;
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