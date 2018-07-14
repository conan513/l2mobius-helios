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
package quests.Q10528_TheAssassinationOfTheKetraOrcChief;

import com.l2jmobius.gameserver.enums.CategoryType;
import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;

import quests.Q10527_TheAssassinationOfTheKetraOrcCommander.Q10527_TheAssassinationOfTheKetraOrcCommander;

/**
 * The Assassination of the Ketra Orc Chief (10528)
 * @URL https://l2wiki.com/The_Assassination_of_the_Ketra_Orc_Chief#Ertheia
 * @author Gigi
 * @date 2017-11-22 - [21:34:59]
 */
public class Q10528_TheAssassinationOfTheKetraOrcChief extends Quest
{
	// NPCs
	private static final int LUGONNES = 33852;
	// Monsters
	private static final int KETRAS_CHIEF_BRAKKI = 27501;
	private static final int BELOS = 27513;
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final int MAX_LEVEL = 80;
	
	public Q10528_TheAssassinationOfTheKetraOrcChief()
	{
		super(10528);
		addStartNpc(LUGONNES);
		addTalkId(LUGONNES);
		addKillId(KETRAS_CHIEF_BRAKKI);
		addCondRace(Race.ERTHEIA, "33852-00.html");
		addCondStart(p -> p.isInCategory(CategoryType.MAGE_GROUP), "33852-00a.htm");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33852-00a.htm");
		addCondCompletedQuest(Q10527_TheAssassinationOfTheKetraOrcCommander.class.getSimpleName(), "33852-00a.htm");
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
			case "33852-02.htm":
			case "33852-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33852-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33852-07.html":
			{
				if (qs.isCond(2))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 351479151, 1839);
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
				htmltext = "33852-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "33852-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "33852-06.html";
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
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.COME_BELOS_PROTECT_ME);
			final L2Npc mob = addSpawn(BELOS, npc, false, 120000);
			addAttackPlayerDesire(mob, killer);
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
