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
package ai.others.AlchemistManager;

import java.util.List;

import com.l2jmobius.gameserver.data.xml.impl.SkillTreesData;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.L2SkillLearn;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.base.AcquireSkillType;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.ExAcquirableSkillListByClass;
import com.l2jmobius.gameserver.network.serverpackets.TutorialShowHtml;

import ai.AbstractNpcAI;

/**
 * Alchemist Manager AI.
 * @author Sdw
 */
public final class AlchemistManager extends AbstractNpcAI
{
	// NPCs
	private static final int ALCHEMISTS[] =
	{
		33978, // Zephyra
		33977, // Veruti
	};
	// Misc
	private static final String TUTORIAL_LINK = "..\\L2text\\QT_026_alchemy_01.htm";
	
	private AlchemistManager()
	{
		addStartNpc(ALCHEMISTS);
		addTalkId(ALCHEMISTS);
		addFirstTalkId(ALCHEMISTS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "33978.html":
			case "33977.html":
			{
				htmltext = event;
				break;
			}
			case "open_tutorial":
			{
				player.sendPacket(new TutorialShowHtml(npc.getObjectId(), TUTORIAL_LINK, TutorialShowHtml.LARGE_WINDOW));
				htmltext = npc.getId() + "-1.html";
				break;
			}
			case "learn_skill":
			{
				if (player.getRace() == Race.ERTHEIA)
				{
					final List<L2SkillLearn> alchemySkills = SkillTreesData.getInstance().getAvailableAlchemySkills(player);
					
					if (alchemySkills.isEmpty())
					{
						player.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
					}
					else
					{
						player.sendPacket(new ExAcquirableSkillListByClass(alchemySkills, AcquireSkillType.ALCHEMY));
					}
				}
				else
				{
					htmltext = npc.getId() + "-2.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new AlchemistManager();
	}
}