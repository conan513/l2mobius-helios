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
package ai.bosses.Lindvior.KatoSicanus;

import java.util.List;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.instancemanager.GrandBossManager;
import com.l2jmobius.gameserver.model.L2Party;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

import ai.AbstractNpcAI;

/**
 * Kato Sicanus Teleporter AI
 * @author Gigi
 * @date 2017-07-13 - [22:17:16]
 */
public class KatoSicanus extends AbstractNpcAI
{
	// NPCs
	private static final int KATO_SICANUS = 33881;
	private static final int LINDVIOR_RAID = 29240;
	private static final int INVISIBLE = 8572;
	// Location
	private static final Location LINDVIOR_LOCATION = new Location(46929, -28807, -1400);
	
	public KatoSicanus()
	{
		addFirstTalkId(KATO_SICANUS);
		addTalkId(KATO_SICANUS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("teleport"))
		{
			final int status = GrandBossManager.getInstance().getBossStatus(LINDVIOR_RAID);
			if (player.isGM())
			{
				player.teleToLocation(LINDVIOR_LOCATION, true);
				addSpawn(INVISIBLE, 46707, -28586, -1400, 0, false, 60000, false);
				GrandBossManager.getInstance().setBossStatus(LINDVIOR_RAID, 1);
			}
			else
			{
				if (status == 2)
				{
					return "33881-1.html";
				}
				if (status == 3)
				{
					return "33881-2.html";
				}
				if (!player.isInParty())
				{
					return "33881-3.html";
				}
				final L2Party party = player.getParty();
				final boolean isInCC = party.isInCommandChannel();
				final List<L2PcInstance> members = (isInCC) ? party.getCommandChannel().getMembers() : party.getMembers();
				final boolean isPartyLeader = (isInCC) ? party.getCommandChannel().isLeader(player) : party.isLeader(player);
				if (!isPartyLeader)
				{
					return "33881-3.html";
				}
				if ((members.size() < Config.LINDVIOR_MIN_PLAYERS) || (members.size() > Config.LINDVIOR_MAX_PLAYERS))
				{
					final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
					packet.setHtml(getHtm(player, "33881-4.html"));
					packet.replace("%min%", Integer.toString(Config.LINDVIOR_MIN_PLAYERS));
					packet.replace("%max%", Integer.toString(Config.LINDVIOR_MAX_PLAYERS));
					player.sendPacket(packet);
					return null;
				}
				for (L2PcInstance member : members)
				{
					if (member.getLevel() < Config.LINDVIOR_MIN_PLAYER_LVL)
					{
						final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
						packet.setHtml(getHtm(player, "33881-5.html"));
						packet.replace("%minlvl%", Integer.toString(Config.LINDVIOR_MIN_PLAYER_LVL));
						player.sendPacket(packet);
						return null;
					}
				}
				for (L2PcInstance member : members)
				{
					if (member.isInsideRadius3D(npc, 1500))
					{
						member.teleToLocation(LINDVIOR_LOCATION, true);
						addSpawn(INVISIBLE, 46707, -28586, -1400, 0, false, 0, false);
						GrandBossManager.getInstance().setBossStatus(LINDVIOR_RAID, 1);
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return "33881.html";
	}
	
	public static void main(String[] args)
	{
		new KatoSicanus();
	}
}
