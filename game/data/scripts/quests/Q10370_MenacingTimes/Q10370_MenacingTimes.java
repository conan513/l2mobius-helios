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
package quests.Q10370_MenacingTimes;

import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.base.ClassLevel;
import com.l2jmobius.gameserver.model.holders.ItemHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * Menacing Times (10370)
 * @URL https://l2wiki.com/Menacing_Times
 * @author Gigi
 */
public class Q10370_MenacingTimes extends Quest
{
	// NPCs
	private static final int ORVEN = 30857; // Human
	private static final int WINONIN = 30856; // Elf
	private static final int OLTRAN = 30862; // DarkElf
	private static final int LADANZA = 30865; // Orc
	private static final int FERRIS = 30847; // Dvarf
	private static final int BROME = 32221; // Kamael
	private static final int ANDREI = 31292;
	private static final int GERKENSHTEIN = 33648;
	// Monster's
	private static final int GRAVE_SCARAB = 21646;
	private static final int GRAVE_ANT = 21648;
	private static final int SHRINE_KNIGHT = 21650;
	// Items
	private static final int REMNANT_ASHES = 34765;
	private static final ItemHolder ADENA = new ItemHolder(57, 479620);
	// Reward
	private static final int EXP_REWARD = 22451400;
	private static final int SP_REWARD = 5388;
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final int MAX_LEVEL = 81;
	
	public Q10370_MenacingTimes()
	{
		super(10370);
		addStartNpc(ORVEN, WINONIN, OLTRAN, LADANZA, FERRIS, BROME);
		addTalkId(ORVEN, WINONIN, OLTRAN, LADANZA, FERRIS, BROME, ANDREI, GERKENSHTEIN);
		addKillId(GRAVE_SCARAB, GRAVE_ANT, SHRINE_KNIGHT);
		registerQuestItems(REMNANT_ASHES);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "no_level.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "30857-02.htm":
			case "30856-02.htm":
			case "30862-02.htm":
			case "30865-02.htm":
			case "30847-02.htm":
			case "32221-02.htm":
			case "31292-02.html":
			{
				htmltext = event;
				break;
			}
			case "30857-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30856-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30862-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30865-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30847-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32221-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "31292-03.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "33648-02.html":
			{
				qs.setCond(0);
				qs.setCond(3, true);
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
				switch (npc.getId())
				{
					case ORVEN:
					{
						if ((player.getRace() == Race.HUMAN) && (player.getClassId().level() == ClassLevel.FOURTH.ordinal()))
						{
							htmltext = "30857-01.htm";
						}
						else
						{
							htmltext = "noRace.htm";
						}
						break;
					}
					case WINONIN:
					{
						if ((player.getRace() == Race.ELF) && (player.getClassId().level() == ClassLevel.FOURTH.ordinal()))
						{
							htmltext = "30856-01.htm";
						}
						else
						{
							htmltext = "noRace.htm";
						}
						break;
					}
					case OLTRAN:
					{
						if ((player.getRace() == Race.DARK_ELF) && (player.getClassId().level() == ClassLevel.FOURTH.ordinal()))
						{
							htmltext = "30862-01.htm";
						}
						else
						{
							htmltext = "noRace.htm";
						}
						break;
					}
					case LADANZA:
					{
						if ((player.getRace() == Race.ORC) && (player.getClassId().level() == ClassLevel.FOURTH.ordinal()))
						{
							htmltext = "30865-01.htm";
						}
						else
						{
							htmltext = "noRace.htm";
						}
						break;
					}
					case FERRIS:
					{
						if ((player.getRace() == Race.DWARF) && (player.getClassId().level() == ClassLevel.FOURTH.ordinal()))
						{
							htmltext = "30847-01.htm";
						}
						else
						{
							htmltext = "noRace.htm";
						}
						break;
					}
					case BROME:
					{
						if ((player.getRace() == Race.KAMAEL) && (player.getClassId().level() == ClassLevel.FOURTH.ordinal()))
						{
							htmltext = "32221-01.htm";
						}
						else
						{
							htmltext = "noRace.htm";
						}
						break;
					}
				}
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case ANDREI:
					{
						if (qs.isCond(1))
						{
							htmltext = "31292-01.html";
						}
						else if (qs.getCond() > 1)
						{
							htmltext = "31292-04.html";
						}
						break;
					}
					case GERKENSHTEIN:
					{
						if (qs.isCond(2))
						{
							htmltext = "33648-01.html";
						}
						else if (qs.getCond() == 3)
						{
							htmltext = "33648-03.html";
						}
						else if (qs.isCond(4))
						{
							htmltext = "33648-04.html";
							takeItems(player, REMNANT_ASHES, -1);
							addExpAndSp(player, EXP_REWARD, SP_REWARD);
							giveItems(player, ADENA);
							qs.exitQuest(false, true);
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				switch (npc.getId())
				{
					case ANDREI:
					{
						htmltext = "31292-05.html";
					}
						break;
					case GERKENSHTEIN:
					{
						htmltext = "33648-05.html";
					}
						break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && (qs.isCond(3)))
		{
			if (giveItemRandomly(killer, npc, REMNANT_ASHES, 1, 30, 0.15, true))
			{
				qs.setCond(4, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}