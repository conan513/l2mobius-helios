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
package instances.JiniaGuildHideout2;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.instancezone.Instance;
import com.l2jmobius.gameserver.model.quest.QuestState;

import instances.AbstractInstance;
import quests.Q10285_MeetingSirra.Q10285_MeetingSirra;

/**
 * Jinia Guild Hideout instance zone.
 * @author Adry_85
 */
public final class JiniaGuildHideout2 extends AbstractInstance
{
	// NPC
	private static final int RAFFORTY = 32020;
	// Misc
	private static final int TEMPLATE_ID = 141;
	
	public JiniaGuildHideout2()
	{
		super(TEMPLATE_ID);
		addStartNpc(RAFFORTY);
		addTalkId(RAFFORTY);
	}
	
	@Override
	protected void onEnter(L2PcInstance player, Instance instance, boolean firstEnter)
	{
		super.onEnter(player, instance, firstEnter);
		if (firstEnter)
		{
			final QuestState qs = player.getQuestState(Q10285_MeetingSirra.class.getSimpleName());
			if (qs != null)
			{
				qs.setCond(2, true);
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		enterInstance(talker, npc, TEMPLATE_ID);
		return super.onTalk(npc, talker);
	}
	
	public static void main(String[] args)
	{
		new JiniaGuildHideout2();
	}
}
