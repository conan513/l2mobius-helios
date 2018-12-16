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
package quests.Q00826_InSearchOfTheSecretWeapon;

import java.util.ArrayList;
import java.util.List;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * In Search of the Secret Weapon (826)
 * @URL https://l2wiki.com/In_Search_of_the_Secret_Weapon
 * @author Mobius, Liamxroy
 */
public class Q00826_InSearchOfTheSecretWeapon extends Quest
{
	// NPC
	private static final int NETI = 34095;
	private static final int[] COMMANDERS =
	{
		23653, // Unit Commander 1
		23654, // Unit Commander 2
		23655, // Unit Commander 2
		23656, // Unit Commander 2
		23657, // Unit Commander 3
		23658, // Unit Commander 4
		23659, // Unit Commander 4
		23660, // Unit Commander 5
		23661, // Unit Commander 6
		23662, // Unit Commander 7
		23663, // Unit Commander 8
		23664, // Unit Commander 8
	};
	// Items
	private static final int ASHEN_CERTIFICATE = 46371;
	private static final int SHADOW_WEAPON_COUPON = 46376;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q00826_InSearchOfTheSecretWeapon()
	{
		super(826);
		addStartNpc(NETI);
		addTalkId(NETI);
		addKillId(COMMANDERS);
		addCondMinLevel(MIN_LEVEL, "34095-00.htm");
		registerQuestItems(ASHEN_CERTIFICATE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "34095-02.htm":
			case "34095-03.htm":
			{
				htmltext = event;
				break;
			}
			case "34095-04.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34095-07.html":
			{
				if (qs.isCond(2))
				{
					takeItems(player, -1, ASHEN_CERTIFICATE);
					rewardItems(player, SHADOW_WEAPON_COUPON, 1);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
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
				htmltext = "34095-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "34095-05.html";
				}
				else
				{
					htmltext = "34095-06.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				if (!qs.isNowAvailable())
				{
					htmltext = "34095-08.html";
				}
				else
				{
					qs.setState(State.CREATED);
					htmltext = "34095-01.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		List<L2PcInstance> members = new ArrayList<>();
		if (player.getParty() != null)
		{
			members = player.getParty().getMembers();
		}
		else
		{
			members.add(player);
		}
		for (L2PcInstance member : members)
		{
			final QuestState qs = getQuestState(member, false);
			if ((qs != null) && qs.isCond(1) && member.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
			{
				if (giveItemRandomly(member, npc, ASHEN_CERTIFICATE, 1, 8, 1.0, true))
				{
					qs.setCond(2, true);
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
}
