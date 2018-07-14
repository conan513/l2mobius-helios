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
package quests.Q10783_TracesOfAnAmbush;

import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;

/**
 * Traces of an Ambush (10783)
 * @author malyelfik
 */
public final class Q10783_TracesOfAnAmbush extends Quest
{
	// NPC
	private static final int NOVAIN = 33866;
	// Monsters
	private static final int EMBRYO_PREDATOR = 27539;
	private static final int[] MONSTERS =
	{
		20679, // Marsh Stalker
		20680, // Marsh Drake
		21017, // Fallen Orc
		21018, // Ancient Gargoyle
		21019, // Fallen Orc Archer
		21020, // Fallen Orc Shaman
		21021, // Sharp Talon Tiger
		21022, // Fallen Orc Captain
		21258, // Fallen Orc Shaman
		21259, // Fallen Orc Shaman
	};
	// Items
	private static final int MISSIVE_SCRAPS = 39722;
	private static final int ENCHANT_ARMOR_B = 948;
	// Messages
	private static final NpcStringId[] MESSAGES =
	{
		NpcStringId.I_WILL_GIVE_YOU_DEATH,
		NpcStringId.BACK_FOR_MORE_HUH,
		NpcStringId.YOU_LITTLE_PUNK_TAKE_THAT
	};
	// Misc
	private static final int MIN_LEVEL = 58;
	private static final int MAX_LEVEL = 61;
	private static final int SPAWN_RATE = 70;
	private static final int DROP_RATE = 80;
	
	public Q10783_TracesOfAnAmbush()
	{
		super(10783);
		addStartNpc(NOVAIN);
		addTalkId(NOVAIN);
		addKillId(MONSTERS);
		addKillId(EMBRYO_PREDATOR);
		
		addCondRace(Race.ERTHEIA, "33866-00.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33866-01.htm");
		registerQuestItems(MISSIVE_SCRAPS);
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
			case "33866-03.htm":
			case "33866-04.htm":
			{
				break;
			}
			case "33866-05.htm":
			{
				qs.startQuest();
				break;
			}
			case "33866-08.html":
			{
				giveItems(player, ENCHANT_ARMOR_B, 5);
				giveStoryQuestReward(player, 34);
				addExpAndSp(player, 5482574, 1315);
				qs.exitQuest(false, true);
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
				htmltext = "33866-02.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "33866-06.html" : "33866-07.html";
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
			if (npc.getId() == EMBRYO_PREDATOR)
			{
				if (getRandom(100) < DROP_RATE)
				{
					giveItems(killer, MISSIVE_SCRAPS, 1);
					if (getQuestItemsCount(killer, MISSIVE_SCRAPS) >= 10)
					{
						qs.setCond(2, true);
					}
					else
					{
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
			}
			else if (getRandom(100) < SPAWN_RATE)
			{
				final L2Npc mob = addSpawn(EMBRYO_PREDATOR, npc, false, 120000);
				addAttackPlayerDesire(mob, killer);
				mob.broadcastSay(ChatType.NPC_GENERAL, MESSAGES[getRandom(MESSAGES.length)]);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}