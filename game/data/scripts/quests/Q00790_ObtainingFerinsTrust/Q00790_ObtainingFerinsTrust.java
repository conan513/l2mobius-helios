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
package quests.Q00790_ObtainingFerinsTrust;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * Obtaining Ferin's Trust (00790)
 * @URL https://l2wiki.com/Obtaining_Ferin%27s_Trust
 * @author Gigi
 */
public class Q00790_ObtainingFerinsTrust extends Quest
{
	// NPCs
	private static final int CYPHONA = 34055;
	// Monsters
	private static final int[] MONSTERS =
	{
		23550, // Kerberos Lager
		23551, // Kerberos Fort
		23552, // Kerberos Nero
		23553, // Fury Sylph Barrena
		23555, // Fury Sylph Temptress
		23556, // Fury Sylph Purka
		23557, // Fury Kerberos Leger
		23558 // Fury Kerberos Nero
	};
	// Misc
	private static final int MIN_LEVEL = 100;
	// Item's
	private static final int MARK_OF_TRUST_LOW_GRADE = 45840;
	private static final int MARK_OF_TRUST_MID_GRADE = 45843;
	private static final int MARK_OF_TRUST_HIGH_GRADE = 45848;
	private static final int MUTATAED_SPIRITS_SOUL = 45849;
	private static final int BSOE = 1538;
	private static final int ELEXIR_OF_LIFE_R = 30357;
	private static final int ELEXIR_OF_MIND_R = 30358;
	private static final int ELEXIR_OF_CP_R = 30359;
	private static final int FERINS_REWARD_BOX = 46165;
	private static final int SUPERIOR_GIANTS_CODEX = 46150;
	
	public Q00790_ObtainingFerinsTrust()
	{
		super(790);
		addStartNpc(CYPHONA);
		addTalkId(CYPHONA);
		addKillId(MONSTERS);
		registerQuestItems(MUTATAED_SPIRITS_SOUL);
		addCondMinLevel(MIN_LEVEL, "34055-00.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "34055-01.htm":
			case "34055-02.htm":
			case "34055-03.htm":
			case "34055-04.html":
			case "34055-08.html":
			case "34055-09a.html":
			case "34055-09b.html":
			case "34055-09c.html":
			{
				htmltext = event;
				break;
			}
			case "34055-05.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34055-09.html":
			{
				giveItems(player, MARK_OF_TRUST_LOW_GRADE, 1);
				qs.exitQuest(QuestType.REPEATABLE, true);
				htmltext = event;
				break;
			}
			case "34055-10a.html":
			{
				if ((getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 200) && (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) < 400))
				{
					giveAdena(player, 119773, true);
					addExpAndSp(player, 5932440000L, 14237820);
					if (getRandom(100) < 20)
					{
						giveItems(player, FERINS_REWARD_BOX, 1);
					}
				}
				else if ((getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 400) && (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) < 600))
				{
					giveAdena(player, 239546, true);
					addExpAndSp(player, 11864880000L, 28475640);
					if (getRandom(100) < 40)
					{
						giveItems(player, FERINS_REWARD_BOX, 1);
					}
				}
				else if ((getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 600) && (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) < 800))
				{
					giveAdena(player, 359319, true);
					addExpAndSp(player, 17797320000L, 42713460);
					if (getRandom(100) < 60)
					{
						giveItems(player, FERINS_REWARD_BOX, 1);
					}
				}
				else if ((getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 800) && (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) < 1000))
				{
					giveAdena(player, 479091, true);
					addExpAndSp(player, 23729760000L, 56951280);
					if (getRandom(100) < 80)
					{
						giveItems(player, FERINS_REWARD_BOX, 1);
					}
				}
				else if (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 1000)
				{
					giveAdena(player, 598864, true);
					addExpAndSp(player, 29662200000L, 71189100);
					giveItems(player, FERINS_REWARD_BOX, 1);
				}
				giveItems(player, MARK_OF_TRUST_MID_GRADE, 1);
				giveItems(player, BSOE, 1);
				giveItems(player, ELEXIR_OF_LIFE_R, 5);
				giveItems(player, ELEXIR_OF_MIND_R, 5);
				giveItems(player, ELEXIR_OF_CP_R, 5);
				qs.exitQuest(QuestType.REPEATABLE, true);
				htmltext = event;
				break;
			}
			case "34055-10b.html":
			{
				if ((getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 200) && (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) < 400))
				{
					giveAdena(player, 119773, true);
					addExpAndSp(player, 5932440000L, 14237820);
					if (getRandom(100) < 20)
					{
						giveItems(player, FERINS_REWARD_BOX, 1);
					}
				}
				else if ((getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 400) && (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) < 600))
				{
					giveAdena(player, 239546, true);
					addExpAndSp(player, 11864880000L, 28475640);
					if (getRandom(100) < 40)
					{
						giveItems(player, FERINS_REWARD_BOX, 1);
					}
				}
				else if ((getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 600) && (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) < 800))
				{
					giveAdena(player, 359319, true);
					addExpAndSp(player, 17797320000L, 42713460);
					if (getRandom(100) < 60)
					{
						giveItems(player, FERINS_REWARD_BOX, 1);
					}
				}
				else if ((getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 800) && (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) < 1000))
				{
					giveAdena(player, 479091, true);
					addExpAndSp(player, 23729760000L, 56951280);
					if (getRandom(100) < 80)
					{
						giveItems(player, FERINS_REWARD_BOX, 1);
					}
				}
				else if (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 1000)
				{
					giveAdena(player, 598864, true);
					addExpAndSp(player, 29662200000L, 71189100);
					giveItems(player, FERINS_REWARD_BOX, 1);
				}
				giveItems(player, MARK_OF_TRUST_HIGH_GRADE, 1);
				giveItems(player, BSOE, 1);
				giveItems(player, ELEXIR_OF_LIFE_R, 5);
				giveItems(player, ELEXIR_OF_MIND_R, 5);
				giveItems(player, ELEXIR_OF_CP_R, 5);
				qs.exitQuest(QuestType.REPEATABLE, true);
				htmltext = event;
				break;
			}
			case "34055-10c.html":
			{
				if ((getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 200) && (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) < 400))
				{
					addExpAndSp(player, 5932440000L, 14237820);
					giveItems(player, FERINS_REWARD_BOX, 1);
					if (getRandom(100) < 1)
					{
						giveItems(player, SUPERIOR_GIANTS_CODEX, 1);
					}
				}
				else if ((getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 400) && (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) < 600))
				{
					addExpAndSp(player, 11864880000L, 28475640);
					giveItems(player, FERINS_REWARD_BOX, getRandom(1, 2));
					if (getRandom(100) < 9)
					{
						giveItems(player, SUPERIOR_GIANTS_CODEX, 1);
					}
				}
				else if ((getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 600) && (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) < 800))
				{
					addExpAndSp(player, 17797320000L, 42713460);
					giveItems(player, FERINS_REWARD_BOX, 2);
					if (getRandom(100) < 20)
					{
						giveItems(player, SUPERIOR_GIANTS_CODEX, 1);
					}
				}
				else if ((getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 800) && (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) < 1000))
				{
					addExpAndSp(player, 23729760000L, 56951280);
					giveItems(player, FERINS_REWARD_BOX, getRandom(2, 3));
					if (getRandom(100) < 25)
					{
						giveItems(player, SUPERIOR_GIANTS_CODEX, 1);
					}
				}
				else if (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) >= 1000)
				{
					addExpAndSp(player, 29662200000L, 71189100);
					giveItems(player, FERINS_REWARD_BOX, 3);
					if (getRandom(100) < 33)
					{
						giveItems(player, SUPERIOR_GIANTS_CODEX, 1);
					}
				}
				qs.exitQuest(QuestType.REPEATABLE, true);
				htmltext = event;
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
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (!hasQuestItems(player, MARK_OF_TRUST_LOW_GRADE) && !hasQuestItems(player, MARK_OF_TRUST_MID_GRADE) && !hasQuestItems(player, MARK_OF_TRUST_HIGH_GRADE))
				{
					htmltext = "34055-01.htm";
					break;
				}
				else if (hasQuestItems(player, MARK_OF_TRUST_LOW_GRADE) && !hasQuestItems(player, MARK_OF_TRUST_MID_GRADE) && !hasQuestItems(player, MARK_OF_TRUST_HIGH_GRADE))
				{
					htmltext = "34055-01a.htm";
					break;
				}
				else if (hasQuestItems(player, MARK_OF_TRUST_MID_GRADE) && hasQuestItems(player, MARK_OF_TRUST_MID_GRADE) && !hasQuestItems(player, MARK_OF_TRUST_HIGH_GRADE))
				{
					htmltext = "34055-01b.htm";
					break;
				}
				else if (hasQuestItems(player, MARK_OF_TRUST_HIGH_GRADE) && hasQuestItems(player, MARK_OF_TRUST_MID_GRADE) && hasQuestItems(player, MARK_OF_TRUST_HIGH_GRADE))
				{
					htmltext = "34055-01c.htm";
					break;
				}
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "34055-06.html";
				}
				else if (qs.isCond(2))
				{
					if (!hasQuestItems(player, MARK_OF_TRUST_LOW_GRADE) && !hasQuestItems(player, MARK_OF_TRUST_MID_GRADE) && !hasQuestItems(player, MARK_OF_TRUST_HIGH_GRADE))
					{
						htmltext = "34055-07.html";
						break;
					}
					else if (hasQuestItems(player, MARK_OF_TRUST_LOW_GRADE) && !hasQuestItems(player, MARK_OF_TRUST_MID_GRADE) && !hasQuestItems(player, MARK_OF_TRUST_HIGH_GRADE))
					{
						htmltext = "34055-07a.html";
						break;
					}
					else if (hasQuestItems(player, MARK_OF_TRUST_MID_GRADE) && hasQuestItems(player, MARK_OF_TRUST_MID_GRADE) && !hasQuestItems(player, MARK_OF_TRUST_HIGH_GRADE))
					{
						htmltext = "34055-07b.html";
						break;
					}
					else if (hasQuestItems(player, MARK_OF_TRUST_HIGH_GRADE) && hasQuestItems(player, MARK_OF_TRUST_MID_GRADE) && hasQuestItems(player, MARK_OF_TRUST_HIGH_GRADE))
					{
						htmltext = "34055-07c.html";
						break;
					}
				}
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
		if ((qs != null) && player.isInsideRadius(npc, Config.ALT_PARTY_RANGE, true, true) && (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) < 1000))
		{
			giveItems(player, MUTATAED_SPIRITS_SOUL, 1);
			if (getQuestItemsCount(player, MUTATAED_SPIRITS_SOUL) == 200)
			{
				qs.setCond(2, true);
			}
			else
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
	}
}