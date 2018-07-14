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
package quests.Q10407_LetsGoFishing;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.ListenerRegisterType;
import com.l2jmobius.gameserver.model.events.annotations.Id;
import com.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import com.l2jmobius.gameserver.model.events.annotations.RegisterType;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerItemAdd;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;

/**
 * Lets Go Fishing (10407)
 * @author Gigi
 * @date 2017-04-23 - [11:23:40]
 */
public class Q10407_LetsGoFishing extends Quest
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
	// Item
	private static final int PRACTICE_FISHING_ROD = 47580;
	private static final int PRACTICE_BAIT = 46737;
	private static final int PRACTICE_FISH = 46736;
	private static final int FISHING_SHOT = 38154;
	private static final int REWARD_FISHING_ROD_PACK = 46739;
	private static final int BAIT = 47547;
	// Misc
	private static final int MIN_LEVEL = 85;
	private static final String COUNT_VAR = "FishCount";
	
	public Q10407_LetsGoFishing()
	{
		super(10407);
		addStartNpc(OFULLE, LINNAEUS, PERELIN, BLEAKER, CYANO, PAMFUS, LANOSCO, HUFS, MONAKAN, BERIX, LITULON, WILLIE, HILGENDORF, PLATIS, KLAUS, BATIDAE, EINDARKNER, GALBA);
		addTalkId(OFULLE, LINNAEUS, PERELIN, BLEAKER, CYANO, PAMFUS, LANOSCO, HUFS, MONAKAN, BERIX, LITULON, WILLIE, HILGENDORF, PLATIS, KLAUS, BATIDAE, EINDARKNER, GALBA);
		registerQuestItems(PRACTICE_FISHING_ROD, PRACTICE_BAIT, PRACTICE_FISH);
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
			case "31572-02.htm":
			case "31572-04.html":
			case "31572-05.html":
			case "31572-08.html":
			case "31572-09.html":
			case "31572-10.html":
			case "31572-11.html":
			{
				htmltext = event;
				break;
			}
			case "give_rod":
			{
				if (!hasQuestItems(player, PRACTICE_FISHING_ROD))
				{
					giveItems(player, PRACTICE_FISHING_ROD, 1);
					htmltext = getHtm(player, "GiveRod.html").replace("%name%", npc.getName());
					break;
				}
				htmltext = getHtm(player, "noNeed.html").replace("%name%", npc.getName());
				break;
			}
			case "give_bait":
			{
				if (!hasQuestItems(player, PRACTICE_BAIT))
				{
					giveItems(player, PRACTICE_BAIT, 20);
					htmltext = getHtm(player, "GiveBait.html").replace("%name%", npc.getName());
					break;
				}
				htmltext = getHtm(player, "noNeed.html").replace("%name%", npc.getName());
				break;
			}
			case "31572-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "31572-06.html":
			{
				giveItems(player, PRACTICE_FISHING_ROD, 1);
				giveItems(player, PRACTICE_BAIT, 20);
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "31572-14.html":
			{
				if (qs.isCond(3))
				{
					addExpAndSp(player, 2469600, 2963);
					giveItems(player, FISHING_SHOT, 60);
					giveItems(player, REWARD_FISHING_ROD_PACK, 1);
					giveItems(player, BAIT, 60);
					qs.exitQuest(false, true);
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
				htmltext = "31572-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = getHtm(player, "31572-12.html").replace("%name%", npc.getName());
				}
				else if (qs.isCond(2))
				{
					htmltext = getHtm(player, "31572-07.html").replace("%name%", npc.getName());
				}
				else if (qs.isCond(3))
				{
					htmltext = getHtm(player, "31572-13.html").replace("%name%", npc.getName());
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
	
	@RegisterEvent(EventType.ON_PLAYER_ITEM_ADD)
	@RegisterType(ListenerRegisterType.ITEM)
	@Id(PRACTICE_FISH)
	public void onItemAdd(OnPlayerItemAdd event)
	{
		final L2PcInstance player = event.getActiveChar();
		final QuestState qs = getQuestState(player, false);
		if (qs != null)
		{
			int count = qs.getInt(COUNT_VAR);
			qs.set(COUNT_VAR, ++count);
			if (qs.isCond(2))
			{
				if ((count >= 5) && (getQuestItemsCount(player, PRACTICE_FISH) == 5))
				{
					qs.setCond(0);
					qs.setCond(3, true);
				}
				else
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(2))
		{
			final int Count = qs.getInt(COUNT_VAR);
			if (Count > 0)
			{
				final Set<NpcLogListHolder> holder = new HashSet<>();
				holder.add(new NpcLogListHolder(NpcStringId.FISHING_PRACTICE, Count));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}
