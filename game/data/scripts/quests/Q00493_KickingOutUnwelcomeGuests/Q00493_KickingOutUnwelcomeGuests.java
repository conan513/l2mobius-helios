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
package quests.Q00493_KickingOutUnwelcomeGuests;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * Kicking Out Unwelcome Guests (493)
 * @author St3eT
 */
public final class Q00493_KickingOutUnwelcomeGuests extends Quest
{
	// NPCs
	private static final int GEORGIO = 33515;
	private static final int LUNATIC_CREATURE = 23148;
	private static final int RESURRECTED_CREATION = 23147;
	private static final int UNDEAD_CREATURE = 23149;
	private static final int SHILEN_MESSENGER = 23151;
	private static final int HELLISH_CREATURE = 23150;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q00493_KickingOutUnwelcomeGuests()
	{
		super(493);
		addStartNpc(GEORGIO);
		addTalkId(GEORGIO);
		addKillId(LUNATIC_CREATURE, RESURRECTED_CREATION, UNDEAD_CREATURE, SHILEN_MESSENGER, HELLISH_CREATURE);
		addCondMinLevel(MIN_LEVEL, "33515-10.htm");
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
			case "33515-02.htm":
			case "33515-03.htm":
			case "33515-04.htm":
			{
				htmltext = event;
				break;
			}
			case "33515-05.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "33515-08.html":
			{
				if (st.isCond(2))
				{
					addExpAndSp(player, 560_000_000, 134_400);
					st.exitQuest(QuestType.DAILY, true);
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
		
		if (npc.getId() == GEORGIO)
		{
			switch (st.getState())
			{
				case State.CREATED:
				{
					htmltext = "33515-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (st.isCond(1))
					{
						htmltext = "33515-06.html";
					}
					else if (st.isCond(2))
					{
						htmltext = "33515-07.html";
					}
					break;
				}
				case State.COMPLETED:
				{
					if (st.isNowAvailable())
					{
						st.setState(State.CREATED);
						htmltext = "33515-01.htm";
					}
					else
					{
						htmltext = "33515-09.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon)
	{
		final QuestState st = getQuestState(player, false);
		if ((st != null) && st.isCond(1))
		{
			final int killedCount = st.getInt(Integer.toString(npc.getId()));
			if (killedCount < 20)
			{
				st.set(Integer.toString(npc.getId()), killedCount + 1);
			}
			
			final int killedLunatic = st.getInt(Integer.toString(LUNATIC_CREATURE));
			final int killedRessurected = st.getInt(Integer.toString(RESURRECTED_CREATION));
			final int killedUndead = st.getInt(Integer.toString(UNDEAD_CREATURE));
			final int killedMessenger = st.getInt(Integer.toString(SHILEN_MESSENGER));
			final int killedHellish = st.getInt(Integer.toString(HELLISH_CREATURE));
			
			if ((killedLunatic == 20) && (killedRessurected == 20) && (killedUndead == 20) && (killedMessenger == 20) && (killedHellish == 20))
			{
				st.setCond(2, true);
			}
		}
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		executeForEachPlayer(player, npc, isSummon, true, false);
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if ((st != null) && st.isCond(1))
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(5);
			npcLogList.add(new NpcLogListHolder(LUNATIC_CREATURE, false, st.getInt(Integer.toString(LUNATIC_CREATURE))));
			npcLogList.add(new NpcLogListHolder(RESURRECTED_CREATION, false, st.getInt(Integer.toString(RESURRECTED_CREATION))));
			npcLogList.add(new NpcLogListHolder(UNDEAD_CREATURE, false, st.getInt(Integer.toString(UNDEAD_CREATURE))));
			npcLogList.add(new NpcLogListHolder(SHILEN_MESSENGER, false, st.getInt(Integer.toString(SHILEN_MESSENGER))));
			npcLogList.add(new NpcLogListHolder(HELLISH_CREATURE, false, st.getInt(Integer.toString(HELLISH_CREATURE))));
			return npcLogList;
		}
		return super.getNpcLogList(player);
	}
}