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
package quests.Q10472_WindsOfFateEncroachingShadows;

import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.ItemHolder;
import com.l2jmobius.gameserver.model.holders.SkillHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * Winds of Fate: Encroaching Shadows (10472)<br>
 * This quest is also included in these AIs:<br>
 * <ul>
 * <li><b>Hardin</b> (first talk event)</li>
 * <li><b>AwakeningMaster</b> (first talk event)</li>
 * </ul>
 * @author malyelfik
 */
public final class Q10472_WindsOfFateEncroachingShadows extends Quest
{
	// NPCs
	private static final int NAVARI = 33931;
	private static final int ZEPHYRA = 33978;
	private static final int MOMET = 33998;
	private static final int BLACK_MARKETEER_MAMMON = 31092;
	private static final int BLACKSMITH_OF_MAMMON = 31126;
	private static final int HARDIN = 33870;
	private static final int KARLA = 33933;
	private static final int RAINA = 33491;
	// Mobs
	private static final int[] MOBS =
	{
		23174, // Arbitor of Darkness
		23175, // Altar of Evil Spirit Offering Box
		23176, // Mutated Cerberos
		23177, // Dartanion
		23178, // Insane Phion
		23179, // Dimensional Rifter
		23180, // Hellgate Fighting Dog
	};
	// Items
	private static final int DARK_FRAGMENT = 40060;
	private static final int COUNTERFEIT_ATELIA = 40059;
	// Rewards
	private static final ItemHolder RECIPE_TWILIGHT_NECKLACE = new ItemHolder(36791, 1);
	private static final ItemHolder CRYSTAL_R = new ItemHolder(17371, 5);
	private static final ItemHolder RED_SOUL_CRYSTAL_15 = new ItemHolder(10480, 1);
	private static final ItemHolder BLUE_SOUL_CRYSTAL_15 = new ItemHolder(10481, 1);
	private static final ItemHolder GREEN_SOUL_CRYSTAL_15 = new ItemHolder(10482, 1);
	private static final ItemHolder FIRE_STONE = new ItemHolder(9546, 15);
	private static final ItemHolder WATER_STONE = new ItemHolder(9547, 15);
	private static final ItemHolder EARTH_STONE = new ItemHolder(9548, 15);
	private static final ItemHolder WIND_STONE = new ItemHolder(9549, 15);
	private static final ItemHolder DARK_STONE = new ItemHolder(9550, 15);
	private static final ItemHolder HOLY_STONE = new ItemHolder(9551, 15);
	// Skill
	private static final SkillHolder ABSORB_WIND = new SkillHolder(16389, 1);
	private static final SkillHolder ATELIA_ENERGY = new SkillHolder(16398, 1);
	// Misc
	private static final double DROP_CHANCE = 0.6d; // Guessed
	private static final int DARK_FRAGMENT_COUNT = 50;
	
	public Q10472_WindsOfFateEncroachingShadows()
	{
		super(10472);
		addStartNpc(NAVARI);
		addTalkId(NAVARI, ZEPHYRA, MOMET, BLACK_MARKETEER_MAMMON, BLACKSMITH_OF_MAMMON, HARDIN, KARLA, RAINA);
		addKillId(MOBS);
		
		addCondRace(Race.ERTHEIA, "");
		addCondCompletedQuest("Q10753_WindsOfFateChoices", "33931-00.htm"); // TODO: Replace quest name
		
		registerQuestItems(DARK_FRAGMENT, COUNTERFEIT_ATELIA);
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
			case "33931-02.htm":
			case "33931-03.htm":
			case "33931-04.htm":
			case "33978-02.html":
			case "33998-02.html":
			case "33998-03.html":
			case "31092-07.html":
			case "31126-02.html":
			case "31126-03.html":
			case "31126-04.html":
			case "31126-05.html":
			case "31126-06.html":
			case "31126-07.html":
			case "33870-02.html":
			case "33870-03.html":
			case "33870-04.html":
			case "33870-08.html":
			case "33978-06.html":
			case "33933-02.html":
			case "33491-02.html":
			case "33491-03.html":
			case "33491-04.html":
			{
				break;
			}
			case "33931-05.htm": // Navari
			{
				qs.startQuest();
				break;
			}
			case "33978-03.html": // Zephyra
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
				}
				break;
			}
			case "33978-07.html":
			{
				if (qs.isCond(17))
				{
					// TODO: Here Zephyra should cast some skill to player which recovers CP/HP/MP
					qs.setCond(18, true);
				}
				break;
			}
			case "33998-04.html": // Momet
			{
				if (qs.isCond(2))
				{
					qs.setCond(3, true);
				}
				break;
			}
			case "31092-02.html": // Black Marketeer Mammon
			{
				htmltext = getHtm(player, event).replace("%playerName%", player.getName());
				break;
			}
			case "31092-03.html":
			{
				if (qs.isCond(3))
				{
					qs.setCond(4, true);
				}
				break;
			}
			case "31092-06.html":
			{
				npc.setTarget(player);
				npc.doCast(ABSORB_WIND.getSkill());
				qs.setMemoState(1);
				break;
			}
			case "31092-08.html":
			{
				if (qs.isCond(5))
				{
					qs.setCond(6, true);
					qs.setMemoState(0);
					takeItems(player, DARK_FRAGMENT, DARK_FRAGMENT_COUNT);
				}
				break;
			}
			case "31126-08.html": // Blacksmith Mammon
			{
				if (qs.isCond(6))
				{
					qs.setCond(7, true);
					giveItems(player, COUNTERFEIT_ATELIA, 1);
				}
				break;
			}
			case "33870-05.html": // Hardin
			{
				if (qs.isCond(7))
				{
					qs.setCond(8, true);
				}
				break;
			}
			case "33870-09.html":
			{
				if (qs.isCond(16))
				{
					takeItems(player, COUNTERFEIT_ATELIA, 1);
					npc.setTarget(player);
					npc.doCast(ATELIA_ENERGY.getSkill()); // TODO: Implement this skill
					qs.setCond(17, true);
				}
				break;
			}
			case "33933-03.html": // Karla
			{
				if (qs.isCond(18))
				{
					qs.setCond(19, true);
				}
				break;
			}
			case "33491-red": // Raina
			case "33491-blue":
			case "33491-green":
			{
				if (qs.isCond(19))
				{
					qs.set("SoulCrystal", event.split("-")[1]);
					htmltext = "33491-05.html";
				}
				break;
			}
			case "33491-fire":
			case "33491-water":
			case "33491-earth":
			case "33491-wind":
			case "33491-dark":
			case "33491-holy":
			{
				if (qs.isCond(19) && qs.isSet("SoulCrystal"))
				{
					// Give attribute stones
					switch (event.split("-")[1])
					{
						case "fire":
						{
							giveItems(player, FIRE_STONE);
							break;
						}
						case "water":
						{
							giveItems(player, WATER_STONE);
							break;
						}
						case "earth":
						{
							giveItems(player, EARTH_STONE);
							break;
						}
						case "wind":
						{
							giveItems(player, WIND_STONE);
							break;
						}
						case "dark":
						{
							giveItems(player, DARK_STONE);
							break;
						}
						case "holy":
						{
							giveItems(player, HOLY_STONE);
							break;
						}
					}
					// Give soul crystal
					switch (qs.get("SoulCrystal"))
					{
						case "red":
						{
							giveItems(player, RED_SOUL_CRYSTAL_15);
							break;
						}
						case "blue":
						{
							giveItems(player, BLUE_SOUL_CRYSTAL_15);
							break;
						}
						case "green":
						{
							giveItems(player, GREEN_SOUL_CRYSTAL_15);
							break;
						}
					}
					giveItems(player, CRYSTAL_R);
					giveItems(player, RECIPE_TWILIGHT_NECKLACE);
					addExpAndSp(player, 175739575, 42177);
					qs.exitQuest(false, true);
					htmltext = "33491-06.html";
				}
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
		
		if (npc.getId() == NAVARI)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					htmltext = "33931-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "33931-06.html";
					}
					break;
				}
				case State.COMPLETED:
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
			}
		}
		else if ((npc.getId() == ZEPHYRA) && qs.isStarted())
		{
			switch (qs.getCond())
			{
				case 1:
				{
					htmltext = "33978-01.html";
					break;
				}
				case 2:
				{
					htmltext = "33978-04.html";
					break;
				}
				case 17:
				{
					htmltext = "33978-05.html";
					break;
				}
				case 18:
				{
					htmltext = "33978-08.html";
					break;
				}
			}
		}
		else if ((npc.getId() == MOMET) && qs.isStarted())
		{
			if (qs.isCond(2))
			{
				htmltext = "33998-01.html";
			}
			else if (qs.isCond(3))
			{
				htmltext = "33998-05.html";
			}
		}
		else if ((npc.getId() == BLACK_MARKETEER_MAMMON) && qs.isStarted())
		{
			switch (qs.getCond())
			{
				case 3:
				{
					htmltext = "31092-01.html";
					break;
				}
				case 4:
				{
					htmltext = "31092-04.html";
					break;
				}
				case 5:
				{
					htmltext = (qs.isMemoState(1)) ? "31092-06.html" : "31092-05.html";
					break;
				}
				case 6:
				{
					htmltext = "31092-08.html";
					break;
				}
			}
		}
		else if ((npc.getId() == BLACKSMITH_OF_MAMMON) && qs.isStarted())
		{
			if (qs.isCond(6))
			{
				htmltext = "31126-01.html";
			}
			else if (qs.isCond(7))
			{
				htmltext = "31126-09.html";
			}
		}
		else if ((npc.getId() == HARDIN) && qs.isStarted())
		{
			switch (qs.getCond())
			{
				case 7:
				{
					htmltext = "33870-01.html";
					break;
				}
				case 8:
				{
					htmltext = "33870-06.html";
					break;
				}
				// TODO: Unknown html for cond 9 - 15
				case 16:
				{
					htmltext = "33870-07.html";
					break;
				}
				case 17:
				{
					htmltext = "33870-10.html";
					break;
				}
			}
		}
		else if ((npc.getId() == KARLA) && qs.isStarted())
		{
			if (qs.isCond(18))
			{
				htmltext = getHtm(player, "33933-01.html");
				htmltext = htmltext.replace("%playerName%", player.getName());
			}
			else if (qs.isCond(19))
			{
				htmltext = "33933-04.html";
			}
		}
		else if ((npc.getId() == RAINA))
		{
			if (qs.isStarted() && qs.isCond(19))
			{
				htmltext = getHtm(player, "33491-01.html");
				htmltext = htmltext.replace("%playerName%", player.getName());
			}
			else if (qs.isCompleted())
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(4) && giveItemRandomly(killer, npc, DARK_FRAGMENT, 1, DARK_FRAGMENT_COUNT, DROP_CHANCE, true))
		{
			qs.setCond(5);
		}
		return super.onKill(npc, killer, isSummon);
	}
}