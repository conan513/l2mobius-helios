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
package quests.Q10763_TerrifyingChertuba;

import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10762_MarionetteSpirit.Q10762_MarionetteSpirit;

/**
 * Terrifying Chertuba (10763)
 * @author malyelfik
 */
public final class Q10763_TerrifyingChertuba extends Quest
{
	// NPC
	private static final int VORBOS = 33966;
	// Monsters
	private static final int CHERTUBA_MIRAGE = 23421;
	private static final int CHERTUBA_ILLUSION = 23422;
	// Items
	private static final int MAGIC_CHAIN_KEY = 39489;
	// Misc
	private static final int MIN_LEVEL = 34;
	
	public Q10763_TerrifyingChertuba()
	{
		super(10763);
		addStartNpc(VORBOS);
		addTalkId(VORBOS);
		addKillId(CHERTUBA_ILLUSION, CHERTUBA_MIRAGE);
		
		addCondRace(Race.ERTHEIA, "33966-00.htm");
		addCondMinLevel(MIN_LEVEL, "33966-00.htm");
		addCondCompletedQuest(Q10762_MarionetteSpirit.class.getSimpleName(), "33966-00.htm");
		registerQuestItems(MAGIC_CHAIN_KEY);
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
			case "33966-02.htm":
			{
				break;
			}
			case "33966-03.htm":
			{
				qs.startQuest();
				break;
			}
			case "33966-06.html":
			{
				if (qs.isCond(2))
				{
					giveStoryQuestReward(player, 10);
					addExpAndSp(player, 896996, 215);
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
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "33966-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "33966-04.html" : "33966-05.html";
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
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			giveItems(killer, MAGIC_CHAIN_KEY, 1);
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}
