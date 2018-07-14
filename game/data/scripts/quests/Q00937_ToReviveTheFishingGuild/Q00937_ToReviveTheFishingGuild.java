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
package quests.Q00937_ToReviveTheFishingGuild;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.gameserver.enums.Faction;
import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.ListenerRegisterType;
import com.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import com.l2jmobius.gameserver.model.events.annotations.RegisterType;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerFishing;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.fishing.ExFishingEnd.FishingEndReason;

/**
 * To Revive The Fishing Guild (937)
 * @author Gigi
 * @date 2017-04-23 - [20:42:23]
 */
public class Q00937_ToReviveTheFishingGuild extends Quest
{
	// NPCs
	private static final int OFULLE = 31572;
	private static final int LINNAEUS = 31577;
	private static final int PERELIN = 31563;
	private static final int BLEAKER = 31567;
	private static final int CYANO = 31569;
	private static final int PAMFUS = 31568;
	private static final int LANOSCO = 31570;
	private static final int HUFS = 31571;
	private static final int MONAKAN = 31573;
	private static final int BERIX = 31576;
	private static final int LITULON = 31575;
	private static final int WILLIE = 31574;
	private static final int HILGENDORF = 31578;
	private static final int PLATIS = 31696;
	private static final int KLAUS = 31579;
	private static final int BATIDAE = 31989;
	private static final int EINDARKNER = 31697;
	private static final int GALBA = 32007;
	// Reward
	private static final int BASIC_SUPPLY_BOX = 47571;
	private static final int INTERMEDIATE_SUPPLY_BOX = 47572;
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final String COUNT_VAR = "FishWinCount";
	
	public Q00937_ToReviveTheFishingGuild()
	{
		super(937);
		addStartNpc(OFULLE, LINNAEUS, PERELIN, BLEAKER, CYANO, PAMFUS, LANOSCO, HUFS, MONAKAN, BERIX, LITULON, WILLIE, HILGENDORF, PLATIS, KLAUS, BATIDAE, EINDARKNER, GALBA);
		addTalkId(OFULLE, LINNAEUS, PERELIN, BLEAKER, CYANO, PAMFUS, LANOSCO, HUFS, MONAKAN, BERIX, LITULON, WILLIE, HILGENDORF, PLATIS, KLAUS, BATIDAE, EINDARKNER, GALBA);
		addCondMinLevel(MIN_LEVEL, "noLevel.htm");
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
			case "Guild-02.htm":
			case "Guild-03.htm":
			{
				htmltext = event;
				break;
			}
			case "Guild-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "Guild-07.html":
			{
				if (qs.isCond(2))
				{
					if (player.getFactionLevel(Faction.FISHING_GUILD) <= 2) // Fisher Guild Lvl: 2
					{
						giveItems(player, BASIC_SUPPLY_BOX, 1);
					}
					else if (player.getFactionLevel(Faction.FISHING_GUILD) > 2)
					{
						giveItems(player, INTERMEDIATE_SUPPLY_BOX, 1);
					}
					addFactionPoints(player, Faction.FISHING_GUILD, 100);
					qs.exitQuest(QuestType.REPEATABLE, true);
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
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "Guild-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "Guild-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "Guild-06.html";
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
	
	@RegisterEvent(EventType.ON_PLAYER_FISHING)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerFishing(OnPlayerFishing event)
	{
		final L2PcInstance player = event.getActiveChar();
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1) && (event.getReason() == FishingEndReason.WIN))
		{
			int count = qs.getInt(COUNT_VAR);
			qs.set(COUNT_VAR, ++count);
			if (count >= 100)
			{
				qs.setCond(2, true);
			}
			else
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final int Count = qs.getInt(COUNT_VAR);
			if (Count > 0)
			{
				final Set<NpcLogListHolder> holder = new HashSet<>();
				holder.add(new NpcLogListHolder(NpcStringId.FISHING, Count));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}
