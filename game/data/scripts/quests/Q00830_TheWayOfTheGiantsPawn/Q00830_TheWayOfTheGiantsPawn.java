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
package quests.Q00830_TheWayOfTheGiantsPawn;

import java.util.ArrayList;
import java.util.List;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExQuestNpcLogList;

/**
 * The Way of the Giant's Pawn (830)
 * @URL https://l2wiki.com/The_Way_of_the_Giant%27s_Pawn
 * @author Liamxroy
 */
public class Q00830_TheWayOfTheGiantsPawn extends Quest
{
	// NPC
	private static final int YENICHE = 34099;
	private static final int YENICHE_FINISH = 34154;
	private static final int[] UNIT_ELITE_SOLDIER =
	{
		23616, // Unit 1 Elite Soldier
		23617, // Unit 2 Elite Soldier
		23618, // Unit 3 Elite Soldier
		23619, // Unit 4 Elite Soldier
		23620, // Unit 5 Elite Soldier
		23621, // Unit 6 Elite Soldier
		23622, // Unit 7 Elite Soldier
		23623, // Unit 8 Elite Soldier
		23624, // Unit 1 Elite Soldier
		23625, // Unit 2 Elite Soldier
		23626, // Unit 3 Elite Soldier
		23627, // Unit 4 Elite Soldier
		23628, // Unit 5 Elite Soldier
		23629, // Unit 6 Elite Soldier
		23630, // Unit 7 Elite Soldier
		23631, // Unit 8 Elite Soldier
		23632, // Unit 1 Elite Soldier
		23633, // Unit 2 Elite Soldier
		23634, // Unit 3 Elite Soldier
		23635, // Unit 4 Elite Soldier
		23636, // Unit 5 Elite Soldier
		23637, // Unit 6 Elite Soldier
		23638, // Unit 7 Elite Soldier
		23639, // Unit 8 Elite Soldier
		23640, // Unit 1 Elite Soldier
		23641, // Unit 2 Elite Soldier
		23642, // Unit 3 Elite Soldier
		23643, // Unit 4 Elite Soldier
		23644, // Unit 5 Elite Soldier
		23645, // Unit 6 Elite Soldier
		23646, // Unit 7 Elite Soldier
		23647, // Unit 8 Elite Soldier
	};
	// Items
	private static final int GLUDIN_HERO_REWARD = 46375;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q00830_TheWayOfTheGiantsPawn()
	{
		super(830);
		addStartNpc(YENICHE);
		addTalkId(YENICHE, YENICHE_FINISH);
		addKillId(UNIT_ELITE_SOLDIER);
		addCondMinLevel(MIN_LEVEL, "34099-00.htm");
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
			case "34099-02.htm":
			case "34099-03.htm":
			{
				htmltext = event;
				break;
			}
			case "34099-04.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34154-02.html":
			{
				if (qs.isCond(2))
				{
					rewardItems(player, GLUDIN_HERO_REWARD, 1);
					addExpAndSp(player, 2422697985L, 5814450);
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
				if (npc.getId() == YENICHE)
				{
					htmltext = "34099-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == YENICHE)
				{
					htmltext = "34099-05.html";
				}
				else
				{
					htmltext = "34154-01.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				if (qs.isNowAvailable() && (npc.getId() == YENICHE))
				{
					qs.setState(State.CREATED);
					htmltext = "34099-01.htm";
				}
				else
				{
					htmltext = "34099-06.html";
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
				int count = qs.getMemoState();
				count++;
				if (count < 45)
				{
					qs.setMemoState(count);
					final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
					log.addNpcString(NpcStringId.DEFEAT_THE_ELITE_SOLDIERS_OF_THE_REVOLUTIONARIES, count);
					member.sendPacket(log);
				}
				if (count >= 45)
				{
					qs.setCond(2, true);
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
}
