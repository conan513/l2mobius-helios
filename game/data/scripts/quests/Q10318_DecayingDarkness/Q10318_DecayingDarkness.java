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
package quests.Q10318_DecayingDarkness;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10317_OrbisWitch.Q10317_OrbisWitch;

/**
 * Decaying Darkness (10318)
 * @URL https://l2wiki.com/Decaying_Darkness_(quest)
 * @author Gigi
 */
public final class Q10318_DecayingDarkness extends Quest
{
	// NPC
	private static final int LYDIA = 32892;
	// Summoners
	private static final int ORBIS_VICTIM = 22911;
	private static final int ORBIS_CURATOR = 22921;
	private static final int ORBIS_THROWER = 22917;
	private static final int ORBIS_ANCIENT_HERO = 22924;
	private static final int ORBIS_GUARD = 22915;
	private static final int ORBIS_CHIEF = 22927;
	// Monsters
	private static final int[] MONSTERS =
	{
		18978, // Orbis' Victim Cursed
		18979, // Orbis' Guard Cursed
		18980, // Orbis' Thrower Cursed
		18981, // Orbis' Curator Cursed
		18982, // Orbis' Ancient Hero Cursed
		18983 // Orbis' Chief Curator Cursed
	};
	// Item
	private static final int CURSE_RESIDUE = 17733;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q10318_DecayingDarkness()
	{
		super(10318);
		addStartNpc(LYDIA);
		addTalkId(LYDIA);
		addAttackId(ORBIS_VICTIM, ORBIS_CURATOR, ORBIS_THROWER, ORBIS_ANCIENT_HERO, ORBIS_GUARD, ORBIS_CHIEF);
		addKillId(MONSTERS);
		registerQuestItems(CURSE_RESIDUE);
		addCondMinLevel(MIN_LEVEL, "32892-09.html");
		addCondCompletedQuest(Q10317_OrbisWitch.class.getSimpleName(), "32892-09.html");
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
			case "32892-02.htm":
			case "32892-03.htm":
			case "32892-04.htm":
			{
				htmltext = event;
				break;
			}
			case "32892-05.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32892-07.html":
			{
				if (qs.isCond(2))
				{
					giveAdena(player, 5427900, false);
					addExpAndSp(player, 79260650, 19022);
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
		
		final int npcId = npc.getId();
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npcId == LYDIA)
				{
					htmltext = "32892-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "32892-06.html";
				}
				else if (qs.isCond(2) && (getQuestItemsCount(player, CURSE_RESIDUE) >= 8))
				{
					htmltext = "32892-06a.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = "32892-08.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		final QuestState qs = getQuestState(attacker, false);
		if ((qs != null) && qs.isCond(1))
		{
			if (getRandom(100) < 5)
			{
				switch (npc.getId())
				{
					case ORBIS_VICTIM:
					{
						final L2Npc mob = addSpawn(18978, npc.getX(), npc.getY(), npc.getZ(), 0, false, 60000);
						addAttackPlayerDesire(mob, attacker, 5);
						npc.deleteMe();
						break;
					}
					case ORBIS_CURATOR:
					{
						final L2Npc mob1 = addSpawn(18981, npc.getX(), npc.getY(), npc.getZ(), 0, false, 60000);
						addAttackPlayerDesire(mob1, attacker, 5);
						npc.deleteMe();
						break;
					}
					case ORBIS_THROWER:
					{
						final L2Npc mob2 = addSpawn(18980, npc.getX(), npc.getY(), npc.getZ(), 0, false, 60000);
						addAttackPlayerDesire(mob2, attacker, 5);
						npc.deleteMe();
						break;
					}
					case ORBIS_ANCIENT_HERO:
					{
						final L2Npc mob3 = addSpawn(18982, npc.getX(), npc.getY(), npc.getZ(), 0, false, 60000);
						addAttackPlayerDesire(mob3, attacker, 5);
						npc.deleteMe();
						break;
					}
					case ORBIS_GUARD:
					{
						final L2Npc mob4 = addSpawn(18979, npc.getX(), npc.getY(), npc.getZ(), 0, false, 60000);
						addAttackPlayerDesire(mob4, attacker, 5);
						npc.deleteMe();
						break;
					}
					case ORBIS_CHIEF:
					{
						final L2Npc mob5 = addSpawn(18983, npc.getX(), npc.getY(), npc.getZ(), 0, false, 60000);
						addAttackPlayerDesire(mob5, attacker, 5);
						npc.deleteMe();
						break;
					}
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if ((qs != null) && qs.isCond(1) && giveItemRandomly(killer, CURSE_RESIDUE, 1, 8, 0.7, true))
		{
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}