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
package quests.Q10461_TappingThePowerWithin;

import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmobius.gameserver.network.serverpackets.TutorialShowHtml;
import com.l2jmobius.gameserver.network.serverpackets.ability.ExShowAPListWnd;

/**
 * Tapping The Power Within (10461)
 * @author Gladicek
 */
public final class Q10461_TappingThePowerWithin extends Quest
{
	// NPCs
	private static final int LIONEL_HUNTER = 33907;
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q10461_TappingThePowerWithin()
	{
		super(10461);
		addStartNpc(LIONEL_HUNTER);
		addTalkId(LIONEL_HUNTER);
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
			case "33907-02.htm":
			case "33907-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33907-04.htm":
			{
				qs.startQuest();
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DOES_YOUR_FATE_NOT_WEIGH_UPON_YOU);
				player.sendPacket(new TutorialShowHtml(npc.getObjectId(), "..\\L2text\\QT_025_ability_01.htm", TutorialShowHtml.LARGE_WINDOW));
				break;
			}
			case "33907-06.html":
			{
				if (!player.isSubClassActive() || (player.isDualClassActive()))
				{
					showOnScreenMsg(player, NpcStringId.OPEN_THE_ABILITY_SCREEN_IN_THE_CHARACTER_INFORMATION_SCREEN_NPRESS_ADJUST_POINTS_TO_ADJUST_THE_ACQUIRED_SP_AND_ABILITY_POINTS, ExShowScreenMessage.TOP_CENTER, 5000);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THIS_IS_JUST_THE_BEGINNING);
					player.sendPacket(ExShowAPListWnd.STATIC_PACKET);
					addExpAndSp(player, 0, 250_000_000);
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
				if (((player.getLevel() >= MIN_LEVEL) && (player.getNobleLevel() > 0)))
				{
					htmltext = "33907-01.htm";
					break;
				}
				htmltext = "33907-07.html";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "33907-05.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = "33907-08.html";
				break;
			}
		}
		return htmltext;
	}
}