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
package ai.areas.AncientCityArcan.Mumu;

import com.l2jmobius.gameserver.enums.Movie;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;

import ai.AbstractNpcAI;

/**
 * Mumu AI.
 * @author St3eT
 */
public final class Mumu extends AbstractNpcAI
{
	// NPC
	private static final int MUMU = 32900; // Mumu
	
	public Mumu()
	{
		addStartNpc(MUMU);
		addFirstTalkId(MUMU);
		addTalkId(MUMU);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "32900-1.html":
			{
				htmltext = event;
				break;
			}
			case "playMovie":
			{
				playMovie(player, Movie.SI_ARKAN_ENTER);
				break;
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Mumu();
	}
}