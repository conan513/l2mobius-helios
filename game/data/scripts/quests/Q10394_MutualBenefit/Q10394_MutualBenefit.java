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
package quests.Q10394_MutualBenefit;

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

/**
 * Mutual Benefit (10394)
 * @author St3eT
 */
public final class Q10394_MutualBenefit extends Quest
{
	// NPCs
	private static final int KELIOS = 33862;
	private static final int GARGOYLE = 20241; // Hunter Gargoyle
	private static final int BASILISK = 20573; // Tarlk Basilisk
	private static final int ELDER_BASILISK = 20574; // Elder Tarlk Basilisk
	// Items
	private static final int EAC = 952; // Scroll: Enchant Armor (C-grade)
	// Misc
	private static final int MIN_LEVEL = 46;
	private static final int MAX_LEVEL = 52;
	
	public Q10394_MutualBenefit()
	{
		super(10394);
		addStartNpc(KELIOS);
		addTalkId(KELIOS);
		addKillId(GARGOYLE, BASILISK, ELDER_BASILISK);
		addCondNotRace(Race.ERTHEIA, "33862-09.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33862-08.htm");
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
			case "33862-02.htm":
			case "33862-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33862-04.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "33862-07.html":
			{
				if (st.isCond(2))
				{
					st.exitQuest(false, true);
					giveItems(player, EAC, 6);
					giveStoryQuestReward(player, 26);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 3_151_312, 756);
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
				htmltext = "33862-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					htmltext = "33862-05.html";
				}
				else if (st.isCond(2))
				{
					htmltext = "33862-06.html";
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
		final QuestState st = getQuestState(killer, false);
		
		if ((st != null) && st.isStarted() && st.isCond(1))
		{
			int killedGargoyle = st.getInt("killed_" + GARGOYLE);
			int killedBasilisk = st.getInt("killed_" + BASILISK);
			int killedElderBasilisk = st.getInt("killed_" + ELDER_BASILISK);
			
			switch (npc.getId())
			{
				case GARGOYLE:
				{
					if (killedGargoyle < 15)
					{
						killedGargoyle++;
						st.set("killed_" + GARGOYLE, killedGargoyle);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case BASILISK:
				{
					if (killedBasilisk < 20)
					{
						killedBasilisk++;
						st.set("killed_" + BASILISK, killedBasilisk);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case ELDER_BASILISK:
				{
					if (killedElderBasilisk < 20)
					{
						killedElderBasilisk++;
						st.set("killed_" + ELDER_BASILISK, killedElderBasilisk);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
			if ((killedGargoyle == 15) && (killedBasilisk == 20) && (killedElderBasilisk == 20))
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
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(3);
			npcLogList.add(new NpcLogListHolder(GARGOYLE, false, st.getInt("killed_" + GARGOYLE)));
			npcLogList.add(new NpcLogListHolder(BASILISK, false, st.getInt("killed_" + BASILISK)));
			npcLogList.add(new NpcLogListHolder(ELDER_BASILISK, false, st.getInt("killed_" + ELDER_BASILISK)));
			return npcLogList;
		}
		return super.getNpcLogList(activeChar);
	}
}