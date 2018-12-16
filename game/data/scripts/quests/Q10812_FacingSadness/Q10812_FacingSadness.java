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
package quests.Q10812_FacingSadness;

import com.l2jmobius.Config;
import com.l2jmobius.commons.util.CommonUtil;
import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.instancemanager.QuestManager;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10811_ExaltedOneWhoFacesTheLimit.Q10811_ExaltedOneWhoFacesTheLimit;

/**
 * Facing Sadness (10812)
 * @author Gladicek
 */
public final class Q10812_FacingSadness extends Quest
{
	// Npc
	private static final int ELIKIA = 31620;
	// Items
	private static final int ELIKIA_CERTIFICATE = 45623;
	private static final int PROOF_OF_DISPOSAL = 45871;
	// Monsters (first group for one quest item, second for two quest items)
	// TODO: Monsters from Raider's Crossroads
	private static final int[] MONSTERS_1 =
	{
		19503, // Bridget
		19506, // Flox Golem
		19509, // Edan
		19512, // Deathmoz
		19574, // Cowing
		23354, // Decay Hannibal
		23355, // Armor Beast
		23356, // Klein Soldier
		23357, // Disorder Warrior
		23358, // Blow Archer
		23360, // Bizuard
		23361, // Mutated Fly
		23362, // Amos Soldier
		23363, // Amos Officer
		23364, // Amos Master
		23365, // Ailith Hunter
		23366, // Durable Charger
		23374, // Lavi
		23375, // Lavi
		23401, // Bridget
	};
	private static final int[] MONSTERS_2 =
	{
		23384, // Smaug
		23385, // Lunatikan
		23386, // Jabberwok
		23387, // Kanzaroth
		23388, // Kandiloth
		23395, // Garion
		23396, // Garion Neti
		23397, // Desert Wendigo
		23398, // Koraza
		23399, // Bend Beetle
	};
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q10812_FacingSadness()
	{
		super(10812);
		addStartNpc(ELIKIA);
		addTalkId(ELIKIA);
		addKillId(MONSTERS_1);
		addKillId(MONSTERS_2);
		addCondMinLevel(MIN_LEVEL, "31620-09.htm");
		addCondStartedQuest(Q10811_ExaltedOneWhoFacesTheLimit.class.getSimpleName(), "31620-06.htm");
		registerQuestItems(PROOF_OF_DISPOSAL);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		
		switch (event)
		{
			case "31620-02.htm":
			case "31620-03.htm":
			{
				htmltext = event;
				break;
			}
			case "31620-04.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "31620-08.html":
			{
				if (qs.isCond(2))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						takeItems(player, PROOF_OF_DISPOSAL, -1);
						giveItems(player, ELIKIA_CERTIFICATE, 1);
						addExpAndSp(player, 0, 498_204_432);
						qs.exitQuest(false, true);
						
						final Quest mainQ = QuestManager.getInstance().getQuest(Q10811_ExaltedOneWhoFacesTheLimit.class.getSimpleName());
						if (mainQ != null)
						{
							mainQ.notifyEvent("SUBQUEST_FINISHED_NOTIFY", npc, player);
						}
						htmltext = event;
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
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
				htmltext = "31620-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "31620-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "31620-07.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		executeForEachPlayer(player, npc, isSummon, true, false);
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && player.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
		{
			giveItems(player, PROOF_OF_DISPOSAL, CommonUtil.contains(MONSTERS_1, npc.getId()) ? 1 : 2);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			
			if (getQuestItemsCount(player, PROOF_OF_DISPOSAL) >= 8000)
			{
				qs.setCond(2, true);
			}
		}
	}
}