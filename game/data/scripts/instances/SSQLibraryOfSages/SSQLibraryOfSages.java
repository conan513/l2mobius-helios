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
package instances.SSQLibraryOfSages;

import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.instancezone.Instance;
import com.l2jmobius.gameserver.network.NpcStringId;

import instances.AbstractInstance;

/**
 * Library of Sages instance zone.
 * @author Adry_85
 */
public final class SSQLibraryOfSages extends AbstractInstance
{
	// NPCs
	private static final int SOPHIA1 = 32596;
	private static final int PILE_OF_BOOKS1 = 32809;
	private static final int PILE_OF_BOOKS2 = 32810;
	private static final int PILE_OF_BOOKS3 = 32811;
	private static final int PILE_OF_BOOKS4 = 32812;
	private static final int PILE_OF_BOOKS5 = 32813;
	private static final int SOPHIA2 = 32861;
	private static final int SOPHIA3 = 32863;
	private static final int ELCADIA_INSTANCE = 32785;
	// Location
	private static final Location LIBRARY_LOC = new Location(37355, -50065, -1127);
	// Messages
	private static final NpcStringId[] ELCADIA_DIALOGS =
	{
		NpcStringId.I_MUST_ASK_LIBRARIAN_SOPHIA_ABOUT_THE_BOOK,
		NpcStringId.THIS_LIBRARY_IT_S_HUGE_BUT_THERE_AREN_T_MANY_USEFUL_BOOKS_RIGHT,
		NpcStringId.AN_UNDERGROUND_LIBRARY_I_HATE_DAMP_AND_SMELLY_PLACES,
		NpcStringId.THE_BOOK_THAT_WE_SEEK_IS_CERTAINLY_HERE_SEARCH_INCH_BY_INCH
	};
	// Misc
	private static final int TEMPLATE_ID = 156;
	
	public SSQLibraryOfSages()
	{
		super(TEMPLATE_ID);
		addFirstTalkId(SOPHIA2, ELCADIA_INSTANCE, PILE_OF_BOOKS1, PILE_OF_BOOKS2, PILE_OF_BOOKS3, PILE_OF_BOOKS4, PILE_OF_BOOKS5);
		addStartNpc(SOPHIA1, SOPHIA2, SOPHIA3);
		addTalkId(SOPHIA1, SOPHIA2, SOPHIA3);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final Instance world = player.getInstanceWorld();
		if (world != null)
		{
			final L2Npc elcadia = world.getParameters().getObject("elcadia", L2Npc.class);
			switch (event)
			{
				case "TELEPORT2":
				{
					player.teleToLocation(LIBRARY_LOC);
					elcadia.teleToLocation(LIBRARY_LOC);
					break;
				}
				case "exit":
				{
					cancelQuestTimer("FOLLOW", npc, player);
					world.setParameter("elcadia", null);
					elcadia.deleteMe();
					teleportPlayerOut(player, world);
					break;
				}
				case "FOLLOW":
				{
					npc.setRunning();
					npc.getAI().startFollow(player);
					npc.broadcastSay(ChatType.NPC_GENERAL, ELCADIA_DIALOGS[getRandom(ELCADIA_DIALOGS.length)]);
					startQuestTimer("FOLLOW", 10000, npc, player);
					break;
				}
				case "ENTER":
				{
					final Location loc = world.getEnterLocation();
					cancelQuestTimer("FOLLOW", npc, player);
					player.teleToLocation(loc, world);
					elcadia.teleToLocation(loc, world);
					break;
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		enterInstance(talker, npc, TEMPLATE_ID);
		return super.onTalk(npc, talker);
	}
	
	@Override
	protected void onEnter(L2PcInstance player, Instance instance, boolean firstEnter)
	{
		super.onEnter(player, instance, firstEnter);
		
		final L2Npc npc = addSpawn(ELCADIA_INSTANCE, player, false, 0, false, instance.getId());
		startQuestTimer("FOLLOW", 3000, npc, player);
		instance.setParameter("elcadia", npc);
	}
	
	public static void main(String[] args)
	{
		new SSQLibraryOfSages();
	}
}