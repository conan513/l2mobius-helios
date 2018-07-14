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
package quests.Q00755_InNeedOfPetras;

import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;

/**
 * @author hlwrave
 */
public class Q00755_InNeedOfPetras extends Quest
{
	// NPCs
	private static final int AKU = 33671;
	// Monsters
	private static final int[] MONSTERS =
	{
		23213,
		23214,
		23227,
		23228,
		23229,
		23230,
		23215,
		23216,
		23217,
		23218,
		23231,
		23232,
		23233,
		23234,
		23237,
		23219
	};
	// Items
	private static final int AKUS_SUPPLY_BOX = 35550;
	private static final int ENERGY_OF_DESTRUCTION = 35562;
	private static final int PETRA = 34959;
	// Other
	private static final int MIN_LEVEL = 97;
	
	public Q00755_InNeedOfPetras()
	{
		super(755);
		addStartNpc(AKU);
		addTalkId(AKU);
		addKillId(MONSTERS);
		registerQuestItems(PETRA);
		addCondMinLevel(MIN_LEVEL, "sofa_aku_q0755_05.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		if (event.equals("sofa_aku_q0755_04.html"))
		{
			qs.startQuest();
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (qs.isCreated())
		{
			htmltext = "sofa_aku_q0755_01.htm";
		}
		else if (qs.isStarted())
		{
			if (qs.isCond(1))
			{
				htmltext = "sofa_aku_q0755_07.html";
			}
			else if (qs.isCond(2))
			{
				takeItems(player, PETRA, -1L);
				addExpAndSp(player, 570676680, 26102484);
				giveItems(player, AKUS_SUPPLY_BOX, 1);
				giveItems(player, ENERGY_OF_DESTRUCTION, 1);
				qs.exitQuest(QuestType.DAILY, true);
				htmltext = "sofa_aku_q0755_08.html";
			}
		}
		else if (qs.isCompleted())
		{
			htmltext = "sofa_aku_q0755_06.html";
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1) && qs.isStarted() && giveItemRandomly(killer, npc, PETRA, 1, 50, 0.75, true))
		{
			qs.setCond(2);
		}
		return super.onKill(npc, killer, isSummon);
	}
}