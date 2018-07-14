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
package ai.others.MonumentOfHeroes;

import java.util.List;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.entity.Hero;
import com.l2jmobius.gameserver.model.olympiad.Olympiad;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.ExHeroList;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmobius.gameserver.network.serverpackets.PlaySound;

import ai.AbstractNpcAI;

/**
 * Monument of Heroes AI.
 * @author St3eT
 */
public final class MonumentOfHeroes extends AbstractNpcAI
{
	// NPCs
	private static final int[] MONUMENTS =
	{
		31690,
		31769,
		31770,
		31771,
		31772,
	};
	// Items
	private static final int HERO_CLOAK = 30372;
	private static final int GLORIOUS_CLOAK = 30373;
	private static final int WINGS_OF_DESTINY_CIRCLET = 6842;
	private static final int[] WEAPONS =
	{
		30392, // Infinity Shaper (dagger)
		30393, // Infinity Cutter (1-H Sword)
		30394, // Infinity Slasher (2-H Sword)
		30395, // Infinity Avenger (1-H Blunt Weapon)
		30396, // Infinity Fighter (Fist)
		30397, // Infinity Stormer (Polearm)
		30398, // Infinity Thrower (bow)
		30399, // Infinity Shooter (crossbow)
		30400, // Infinity Buster (magic sword)
		30401, // Infinity Caster (magic blunt weapon)
		30402, // Infinity Retributer (two-handed magic blunt weapon)
		30403, // Infinity Dual Sword (Dual Swords)
		30404, // Infinity Dual Dagger (Dual Daggers)
		30405, // Infinity Dual Blunt Weapon (Dual Blunt Weapon)
	};
	
	private MonumentOfHeroes()
	{
		addStartNpc(MONUMENTS);
		addFirstTalkId(MONUMENTS);
		addTalkId(MONUMENTS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "MonumentOfHeroes-reward.html":
			{
				htmltext = event;
				break;
			}
			case "index":
			{
				htmltext = onFirstTalk(npc, player);
				break;
			}
			case "heroList":
			{
				player.sendPacket(new ExHeroList());
				break;
			}
			case "receiveCloak":
			{
				final int olympiadRank = getOlympiadRank(player);
				if (olympiadRank == 1)
				{
					if (!hasAtLeastOneQuestItem(player, HERO_CLOAK, GLORIOUS_CLOAK))
					{
						if (player.isInventoryUnder80(false))
						{
							giveItems(player, HERO_CLOAK, 1);
						}
						else
						{
							player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
						}
					}
					else
					{
						htmltext = "MonumentOfHeroes-cloakHave.html";
					}
				}
				else if ((olympiadRank == 2) || (olympiadRank == 3))
				{
					if (!hasAtLeastOneQuestItem(player, HERO_CLOAK, GLORIOUS_CLOAK))
					{
						if (player.isInventoryUnder80(false))
						{
							giveItems(player, GLORIOUS_CLOAK, 1);
						}
						else
						{
							player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
						}
					}
					else
					{
						htmltext = "MonumentOfHeroes-cloakHave.html";
					}
				}
				else
				{
					htmltext = "MonumentOfHeroes-cloakNo.html";
				}
				break;
			}
			case "heroWeapon":
			{
				if (Hero.getInstance().isHero(player.getObjectId()))
				{
					if (player.isInventoryUnder80(false))
					{
						htmltext = hasAtLeastOneQuestItem(player, WEAPONS) ? "MonumentOfHeroes-weaponHave.html" : "MonumentOfHeroes-weaponList.html";
					}
					else
					{
						player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
					}
				}
				else
				{
					htmltext = "MonumentOfHeroes-weaponNo.html";
				}
				break;
			}
			case "heroCirclet":
			{
				if (Hero.getInstance().isHero(player.getObjectId()))
				{
					if (hasQuestItems(player, WINGS_OF_DESTINY_CIRCLET))
					{
						htmltext = "MonumentOfHeroes-circletHave.html";
					}
					else if (!player.isInventoryUnder80(false))
					{
						player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
					}
					else
					{
						giveItems(player, WINGS_OF_DESTINY_CIRCLET, 1);
					}
				}
				else
				{
					htmltext = "MonumentOfHeroes-circletNo.html";
				}
				break;
			}
			case "heroCertification":
			{
				if (Hero.getInstance().isUnclaimedHero(player.getObjectId()))
				{
					htmltext = "MonumentOfHeroes-heroCertification.html";
				}
				else if (Hero.getInstance().isHero(player.getObjectId()))
				{
					htmltext = "MonumentOfHeroes-heroCertificationAlready.html";
				}
				else
				{
					htmltext = "MonumentOfHeroes-heroCertificationNo.html";
				}
				break;
			}
			case "heroConfirm":
			{
				if (Hero.getInstance().isUnclaimedHero(player.getObjectId()))
				{
					if (!player.isSubClassActive())
					{
						if (player.getLevel() >= 85)
						{
							Hero.getInstance().claimHero(player);
							showOnScreenMsg(player, (NpcStringId.getNpcStringId(13357 + player.getClassId().getId())), ExShowScreenMessage.TOP_CENTER, 5000);
							player.broadcastPacket(new PlaySound(1, "ns01_f", 0, 0, 0, 0, 0));
							htmltext = "MonumentOfHeroes-heroCertificationsDone.html";
						}
						else
						{
							htmltext = "MonumentOfHeroes-heroCertificationLevel.html";
						}
					}
					else
					{
						htmltext = "MonumentOfHeroes-heroCertificationSub.html";
					}
				}
				else
				{
					htmltext = "MonumentOfHeroes-heroCertificationNo.html";
				}
				break;
			}
			case "give_30392": // Infinity Shaper (dagger)
			case "give_30393": // Infinity Cutter (1-H Sword)
			case "give_30394": // Infinity Slasher (2-H Sword)
			case "give_30395": // Infinity Avenger (1-H Blunt Weapon)
			case "give_30396": // Infinity Fighter (Fist)
			case "give_30397": // Infinity Stormer (Polearm)
			case "give_30398": // Infinity Thrower (bow)
			case "give_30399": // Infinity Shooter (crossbow)
			case "give_30400": // Infinity Buster (magic sword)
			case "give_30401": // Infinity Caster (magic blunt weapon)
			case "give_30402": // Infinity Retributer (two-handed magic blunt weapon)
			case "give_30403": // Infinity Dual Sword (Dual Swords)
			case "give_30404": // Infinity Dual Dagger (Dual Daggers)
			case "give_30405": // Infinity Dual Blunt Weapon (Dual Blunt Weapon)
			{
				final int weaponId = Integer.parseInt(event.replace("give_", ""));
				giveItems(player, weaponId, 1);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return player.getNobleLevel() > 0 ? "MonumentOfHeroes-noblesse.html" : "MonumentOfHeroes-noNoblesse.html";
	}
	
	private int getOlympiadRank(L2PcInstance player)
	{
		final List<String> names = Olympiad.getInstance().getClassLeaderBoard(player.getClassId().getId());
		try
		{
			for (int i = 1; i <= 3; i++)
			{
				if (names.get(i - 1).equals(player.getName()))
				{
					return i;
				}
			}
		}
		catch (Exception e)
		{
			return -1;
		}
		return -1;
	}
	
	public static void main(String[] args)
	{
		new MonumentOfHeroes();
	}
}