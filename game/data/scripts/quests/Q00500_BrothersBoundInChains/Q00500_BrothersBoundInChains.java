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
package quests.Q00500_BrothersBoundInChains;

import java.util.stream.IntStream;

import com.l2jmobius.commons.util.Rnd;
import com.l2jmobius.gameserver.data.xml.impl.SkillData;
import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.CharEffectList;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.ListenerRegisterType;
import com.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import com.l2jmobius.gameserver.model.events.annotations.RegisterType;
import com.l2jmobius.gameserver.model.events.impl.character.npc.OnAttackableKill;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.model.skills.Skill;

/**
 * @author Mathael
 * @URL https://l2wiki.com/Brothers_Bound_in_Chains
 * @version Infinite Odyssey
 */
public class Q00500_BrothersBoundInChains extends Quest
{
	// NPC
	private static final int DARK_JUDGE = 30981;
	// Items
	private static final int GEMSTONE_B = 2132;
	private static final int PENITENT_MANACLES = 36060;
	private static final int CRUMBS_OF_PENITENCE = 36077;
	// Skills
	private static final int HOUR_OF_PENITENCE[] =
	{
		15325,
		15326,
		15327,
		15328,
		15329
	};
	// Agathions
	private static final int SIN_EATERS[] =
	{
		16098,
		16099,
		16100,
		16101,
		16102
	};
	// Misc
	private static final int DROP_QI_CHANCE = 5; // in % TODO: check that value
	private static final int MIN_LEVEL = 85;
	
	public Q00500_BrothersBoundInChains()
	{
		super(500);
		addStartNpc(DARK_JUDGE);
		addTalkId(DARK_JUDGE);
		addSummonAgathion();
		registerQuestItems(PENITENT_MANACLES, CRUMBS_OF_PENITENCE);
		addCondMinLevel(MIN_LEVEL, "30981-nopk.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "buff":
			{
				if (player != null)
				{
					final Skill skill = SkillData.getInstance().getSkill(15325, 1); // Hour of Penitence
					skill.activateSkill(player, player);
					startQuestTimer("buff", 270000, null, player); // Rebuff every 4min30 (retail like)
				}
				return null;
			}
			case "30981-02.htm":
			case "30981-03.htm":
			{
				break;
			}
			case "30981-04.htm":
			{
				if (getQuestItemsCount(player, GEMSTONE_B) >= 200)
				{
					takeItems(player, GEMSTONE_B, 200);
					giveItems(player, PENITENT_MANACLES, 1);
				}
				else
				{
					event = "30981-05.html";
				}
				break;
			}
			case "30981-06.htm":
			{
				qs.startQuest();
				break;
			}
			case "30981-09.html": // not retail html.
			{
				if (getQuestItemsCount(player, CRUMBS_OF_PENITENCE) >= 10)
				{
					takeItems(player, CRUMBS_OF_PENITENCE, -1);
					player.setPkKills(Math.max(0, player.getPkKills() - Rnd.get(1, 10)));
					qs.exitQuest(QuestType.DAILY, true);
				}
				else
				{
					// If player delete QuestItems: Need check how it work on retail.
					qs.setCond(1);
					event = "30981-07.html";
				}
				break;
			}
			default:
			{
				event = getNoQuestMsg(player);
			}
		}
		
		return event;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = (talker.getPkKills() > 0) && (talker.getReputation() >= 0) ? "30981-01.htm" : "30981-nopk.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "30981-07.html";
						break;
					}
					case 2:
					{
						htmltext = "30981-08.html"; // not retail html.
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (qs.isNowAvailable())
				{
					qs.setState(State.CREATED);
					htmltext = "30981-01.htm";
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public void onSummonAgathion(L2PcInstance player, int agathionId)
	{
		if (IntStream.of(SIN_EATERS).anyMatch(x -> x == agathionId)) // TODO: Register IDs
		{
			startQuestTimer("buff", 2500, null, player);
		}
	}
	
	@RegisterEvent(EventType.ON_ATTACKABLE_KILL)
	@RegisterType(ListenerRegisterType.GLOBAL_MONSTERS)
	public void onAttackableKill(OnAttackableKill event)
	{
		final QuestState qs = getQuestState(event.getAttacker(), false);
		if (qs == null)
		{
			return;
		}
		
		// Player can drop more than 10 Crumbs of Penitence but there's no point in getting more than 10 (retail)
		boolean isAffectedByHourOfPenitence = false;
		final CharEffectList effects = event.getAttacker().getEffectList();
		for (int i = 0; !isAffectedByHourOfPenitence && (i < HOUR_OF_PENITENCE.length); i++)
		{
			if (effects.isAffectedBySkill(HOUR_OF_PENITENCE[i]))
			{
				isAffectedByHourOfPenitence = true;
			}
		}
		
		if (isAffectedByHourOfPenitence)
		{
			if (Rnd.get(1, 100) <= DROP_QI_CHANCE)
			{
				giveItems(event.getAttacker(), CRUMBS_OF_PENITENCE, 1);
				if (!qs.isCond(2) && (getQuestItemsCount(event.getAttacker(), CRUMBS_OF_PENITENCE) >= 10))
				{
					qs.setCond(2, true);
				}
			}
		}
	}
}
