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
package quests.Q10422_AssassinationOfTheVarkaSilenosChief;

import com.l2jmobius.gameserver.enums.CategoryType;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10421_AssassinationOfTheVarkaSilenosCommander.Q10421_AssassinationOfTheVarkaSilenosCommander;

/**
 * Assassination of the Varka Silenos Chief (10422)
 * @author Stayway
 */
public final class Q10422_AssassinationOfTheVarkaSilenosChief extends Quest
{
	// NPCs
	private static final int HANSEN = 33853;
	private static final int CHIEF_HORUS = 27503;
	private static final int KAMPF = 27516;
	// Items
	private static final int EAS = 26353; // Scroll: Enchant Armor (S-grade)
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final int MAX_LEVEL = 80;
	
	public Q10422_AssassinationOfTheVarkaSilenosChief()
	{
		super(10422);
		addStartNpc(HANSEN);
		addTalkId(HANSEN);
		addKillId(CHIEF_HORUS);
		addSpawnId(KAMPF);
		addCondNotRace(Race.ERTHEIA, "33853-08.html");
		addCondInCategory(CategoryType.FIGHTER_GROUP, "33853-09.htm");
		addCondMinLevel(MIN_LEVEL, "33853-09.htm");
		addCondMaxLevel(MAX_LEVEL, "33853-09.htm");
		addCondCompletedQuest(Q10421_AssassinationOfTheVarkaSilenosCommander.class.getSimpleName(), "33853-09.htm");
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
			case "33853-02.htm":
			case "33853-04.html":
			{
				htmltext = event;
				break;
			}
			case "33853-03.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "reward_9546":
			case "reward_9547":
			case "reward_9548":
			case "reward_9549":
			case "reward_9550":
			case "reward_9551":
			{
				if (st.isCond(2))
				{
					final int stoneId = Integer.parseInt(event.replaceAll("reward_", ""));
					st.exitQuest(false, true);
					giveItems(player, stoneId, 15);
					giveItems(player, EAS, 2);
					giveStoryQuestReward(player, 30);
					if ((player.getLevel() >= MIN_LEVEL) && (player.getLevel() <= MAX_LEVEL))
					{
						addExpAndSp(player, 7_665_840, 1839);
					}
					htmltext = "33853-07.html";
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
		
		if (npc.getId() == HANSEN)
		{
			if (qs.getState() == State.CREATED)
			{
				htmltext = "33853-01.htm";
			}
			else if (qs.getState() == State.STARTED)
			{
				if (qs.isCond(1))
				{
					htmltext = "33853-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "33853-06.html";
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && qs.isCond(1))
		{
			qs.setCond(2, true);
		}
		if ((qs != null) && qs.isCond(2))
		{
			addSpawn(KAMPF, 105626, -43053, -1721, 0, true, 60000);
		}
		return super.onKill(npc, killer, isSummon);
	}
}