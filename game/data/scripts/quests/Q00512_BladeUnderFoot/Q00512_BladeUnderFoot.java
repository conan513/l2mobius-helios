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
package quests.Q00512_BladeUnderFoot;

import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.L2Clan;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.entity.Castle;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;

/**
 * Blade Under Foot (512)
 * @author Mobius
 */
public final class Q00512_BladeUnderFoot extends Quest
{
	// NPCs
	private static final int[] NPCS =
	{
		36403, // Gludio
		36404, // Dion
		36405, // Giran
		36406, // Oren
		36407, // Aden
		36408, // Innadril
		36409, // Goddard
		36410, // Rune
		36411, // Schuttgart
	};
	// Items
	private static final int MARK = 9798;
	private static final int KNIGHT_EPALUETTE = 9912;
	// Misc
	private static final int MIN_LEVEL = 90;
	
	public Q00512_BladeUnderFoot()
	{
		super(512);
		addStartNpc(NPCS);
		addTalkId(NPCS);
		addCondMinLevel(MIN_LEVEL, "Warden-00a.htm");
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
			case "Warden-03.html":
			case "Warden-04.html":
			case "Warden-05.html":
			case "Warden-06.html":
			case "Warden-09.html":
			{
				break;
			}
			case "Warden-02.htm":
			{
				qs.startQuest();
				break;
			}
			case "Warden-10.html":
			{
				qs.exitQuest(QuestType.REPEATABLE);
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
		
		if (qs.isCreated())
		{
			final Castle castle = npc.getCastle();
			final L2Clan clan = player.getClan();
			htmltext = ((castle != null) && (clan != null) && (clan.getCastleId() == castle.getResidenceId())) ? "Warden-01.htm" : "Warden-00b.htm";
		}
		else
		{
			final long itemCount = getQuestItemsCount(player, MARK);
			if (itemCount == 0)
			{
				htmltext = "Warden-07.html";
			}
			else
			{
				takeItems(player, MARK, itemCount);
				giveItems(player, KNIGHT_EPALUETTE, itemCount * 2);
				htmltext = "Warden-08.html";
			}
		}
		return htmltext;
	}
}