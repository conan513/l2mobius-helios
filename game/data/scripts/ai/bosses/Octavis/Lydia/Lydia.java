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
package ai.bosses.Octavis.Lydia;

import com.l2jmobius.gameserver.instancemanager.InstanceManager;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.instancezone.Instance;

import ai.AbstractNpcAI;

/**
 * Lydia AI.
 * @author St3eT
 */
public final class Lydia extends AbstractNpcAI
{
	// NPCs
	private static final int LYDIA = 32892;
	// Misc
	private static final int OCTAVIS_TEMPLATE_ID = 180;
	private static final int OCTAVIS_EXTREME_TEMPLATE_ID = 181;
	
	private Lydia()
	{
		addStartNpc(LYDIA);
		addFirstTalkId(LYDIA);
		addTalkId(LYDIA);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final Instance instance = InstanceManager.getInstance().getPlayerInstance(player, false);
		if ((instance != null) && ((instance.getTemplateId() == OCTAVIS_TEMPLATE_ID) || (instance.getTemplateId() == OCTAVIS_EXTREME_TEMPLATE_ID)))
		{
			htmltext = "Lydia-02.html";
		}
		else
		{
			htmltext = "Lydia-01.html";
		}
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "Lydia-03.html":
			case "Lydia-04.html":
			case "Lydia-05.html":
			{
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Lydia();
	}
}