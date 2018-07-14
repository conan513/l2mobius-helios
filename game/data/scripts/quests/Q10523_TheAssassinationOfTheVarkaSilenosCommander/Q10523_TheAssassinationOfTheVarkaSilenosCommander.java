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
package quests.Q10523_TheAssassinationOfTheVarkaSilenosCommander;

import com.l2jmobius.gameserver.enums.CategoryType;
import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10522_TheDarkSecretOfVarkaSilenos.Q10522_TheDarkSecretOfVarkaSilenos;

/**
 * The Assassination of the Varka Silenos Commander Mos (10523)
 * @author Gigi
 * @date 2017-11-18 - [12:27:08]
 */
public class Q10523_TheAssassinationOfTheVarkaSilenosCommander extends Quest
{
	// NPCs
	private static final int HANSEN = 33853;
	// Monsters
	private static final int VARKAS_COMMANDER_MOS = 27502;
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final int MAX_LEVEL = 80;
	
	public Q10523_TheAssassinationOfTheVarkaSilenosCommander()
	{
		super(10523);
		addStartNpc(HANSEN);
		addTalkId(HANSEN);
		addKillId(VARKAS_COMMANDER_MOS);
		addCondRace(Race.ERTHEIA, "33853-00.html");
		addCondStart(p -> p.isInCategory(CategoryType.FIGHTER_GROUP), "33853-00a.htm");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33853-00.html");
		addCondCompletedQuest(Q10522_TheDarkSecretOfVarkaSilenos.class.getSimpleName(), "33853-00.html");
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
			case "33853-02.htm":
			case "33853-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33853-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33853-07.html":
			{
				if (qs.isCond(2))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 327446943, 1839);
						qs.exitQuest(QuestType.ONE_TIME, true);
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
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "33853-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "33853-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "33853-06.html";
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
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, true);
		if ((qs != null) && qs.isCond(1))
		{
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
