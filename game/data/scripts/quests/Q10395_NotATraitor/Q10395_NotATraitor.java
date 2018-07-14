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
package quests.Q10395_NotATraitor;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;

import quests.Q10394_MutualBenefit.Q10394_MutualBenefit;

/**
 * Not a Traitor (10395)
 * @author St3eT
 */
public final class Q10395_NotATraitor extends Quest
{
	// NPCs
	private static final int LEO = 33863;
	private static final int KELIOS = 33862;
	private static final int[] MONSTERS =
	{
		20161, // Oel Mahum
		20575, // Oel Mahum Warrior
		20576, // Oel Mahum Shaman
		21261, // Ol Mahum Transcender
	};
	// Items
	private static final int EAC = 952; // Scroll: Enchant Armor (C-grade)
	// Misc
	private static final int MIN_LEVEL = 46;
	private static final int MAX_LEVEL = 52;
	
	public Q10395_NotATraitor()
	{
		super(10395);
		addStartNpc(LEO);
		addTalkId(LEO, KELIOS);
		addKillId(MONSTERS);
		addCondNotRace(Race.ERTHEIA, "33863-06.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33863-05.htm");
		addCondCompletedQuest(Q10394_MutualBenefit.class.getSimpleName(), "33863-05.htm");
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
			case "33863-02.htm":
			case "33863-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33863-04.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "33862-03.html":
			{
				if (st.isCond(2))
				{
					st.exitQuest(false, true);
					giveItems(player, EAC, 5);
					giveStoryQuestReward(player, 32);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 3_781_574, 907);
					}
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
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == LEO)
				{
					htmltext = "33863-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					htmltext = npc.getId() == LEO ? "33863-04.htm" : "33862-01.html";
				}
				else if (st.isCond(2))
				{
					htmltext = npc.getId() == LEO ? "33863-04.htm" : "33862-02.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == LEO)
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		
		if ((st != null) && st.isStarted() && st.isCond(1) && (getRandom(100) < 75))
		{
			final int killedMonsters = st.getInt("killedMonsters") + 1;
			st.set("killedMonsters", killedMonsters);
			playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			
			if (killedMonsters == 50)
			{
				st.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance activeChar)
	{
		final QuestState st = getQuestState(activeChar, false);
		if ((st != null) && st.isStarted() && st.isCond(1))
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(1);
			npcLogList.add(new NpcLogListHolder(NpcStringId.ELIMINATE_THE_OEL_MAHUM_MONSTERS, st.getInt("killedMonsters")));
			return npcLogList;
		}
		return super.getNpcLogList(activeChar);
	}
}