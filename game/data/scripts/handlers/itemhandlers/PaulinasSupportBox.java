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
package handlers.itemhandlers;

import com.l2jmobius.gameserver.data.xml.impl.CategoryData;
import com.l2jmobius.gameserver.enums.CategoryType;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.handler.IItemHandler;
import com.l2jmobius.gameserver.model.actor.L2Playable;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.base.ClassId;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;

/**
 * @author Mobius
 */
public class PaulinasSupportBox implements IItemHandler
{
	// Items
	private static final int BOX_D_GRADE = 46849;
	private static final int BOX_C_GRADE = 46850;
	private static final int BOX_A_GRADE = 46851;
	private static final int BOX_S_GRADE = 46852;
	private static final int BOX_R_GRADE = 46919;
	// Rewards
	private static final int BOX_D_HEAVY = 46837;
	private static final int BOX_D_LIGHT = 46838;
	private static final int BOX_D_ROBE = 46839;
	private static final int BOX_C_HEAVY = 46840;
	private static final int BOX_C_LIGHT = 46841;
	private static final int BOX_C_ROBE = 46842;
	private static final int BOX_A_HEAVY = 46843;
	private static final int BOX_A_LIGHT = 46844;
	private static final int BOX_A_ROBE = 46845;
	private static final int BOX_S_HEAVY = 46846;
	private static final int BOX_S_LIGHT = 46847;
	private static final int BOX_S_ROBE = 46848;
	private static final int BOX_R_HEAVY = 46924;
	private static final int BOX_R_LIGHT = 46925;
	private static final int BOX_R_ROBE = 46926;
	
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final L2PcInstance player = playable.getActingPlayer();
		final Race race = player.getRace();
		final ClassId classId = player.getClassId();
		
		if (!player.isInventoryUnder80(false))
		{
			player.sendPacket(SystemMessageId.YOU_VE_EXCEEDED_THE_LIMIT_AND_CANNOT_RETRIEVE_THE_ITEM_PLEASE_CHECK_YOUR_LIMIT_IN_THE_INVENTORY);
			return false;
		}
		
		player.getInventory().destroyItem(getClass().getSimpleName(), item, 1, player, null);
		player.sendPacket(new InventoryUpdate(item));
		
		switch (item.getId())
		{
			case BOX_D_GRADE:
			{
				switch (race)
				{
					case HUMAN:
					case ELF:
					case DARK_ELF:
					case DWARF:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_D_ROBE, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.RECOM_ROGUE_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_D_LIGHT, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_D_HEAVY, 1, player, true);
						}
						break;
					}
					case ORC:
					case KAMAEL:
					{
						player.addItem(getClass().getSimpleName(), BOX_D_LIGHT, 1, player, true);
						break;
					}
					case ERTHEIA:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_D_ROBE, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_D_LIGHT, 1, player, true);
						}
						break;
					}
				}
				break;
			}
			case BOX_C_GRADE:
			{
				switch (race)
				{
					case HUMAN:
					case ELF:
					case DARK_ELF:
					case DWARF:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_C_ROBE, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.RECOM_ROGUE_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_C_LIGHT, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_C_HEAVY, 1, player, true);
						}
						break;
					}
					case ORC:
					{
						if (player.isMageClass() || CategoryData.getInstance().isInCategory(CategoryType.WARRIOR_CATEGORY, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_C_LIGHT, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_C_HEAVY, 1, player, true);
						}
						break;
					}
					case KAMAEL:
					{
						player.addItem(getClass().getSimpleName(), BOX_C_LIGHT, 1, player, true);
						break;
					}
					case ERTHEIA:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_C_ROBE, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_C_LIGHT, 1, player, true);
						}
						break;
					}
				}
				break;
			}
			case BOX_A_GRADE:
			{
				switch (race)
				{
					case HUMAN:
					case ELF:
					case DARK_ELF:
					case DWARF:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_A_ROBE, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.RECOM_ROGUE_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_A_LIGHT, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_A_HEAVY, 1, player, true);
						}
						break;
					}
					case ORC:
					{
						if (player.isMageClass() || CategoryData.getInstance().isInCategory(CategoryType.WARRIOR_CATEGORY, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_A_LIGHT, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_A_HEAVY, 1, player, true);
						}
						break;
					}
					case KAMAEL:
					{
						player.addItem(getClass().getSimpleName(), BOX_A_LIGHT, 1, player, true);
						break;
					}
					case ERTHEIA:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_A_ROBE, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_A_LIGHT, 1, player, true);
						}
						break;
					}
				}
				break;
			}
			case BOX_S_GRADE:
			{
				switch (race)
				{
					case HUMAN:
					case ELF:
					case DARK_ELF:
					case DWARF:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_S_ROBE, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.RECOM_ROGUE_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_S_LIGHT, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_S_HEAVY, 1, player, true);
						}
						break;
					}
					case ORC:
					{
						if (player.isMageClass() || CategoryData.getInstance().isInCategory(CategoryType.WARRIOR_CATEGORY, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_S_LIGHT, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_S_HEAVY, 1, player, true);
						}
						break;
					}
					case KAMAEL:
					{
						player.addItem(getClass().getSimpleName(), BOX_S_LIGHT, 1, player, true);
						break;
					}
					case ERTHEIA:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_S_ROBE, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_S_LIGHT, 1, player, true);
						}
						break;
					}
				}
				break;
			}
			case BOX_R_GRADE:
			{
				switch (race)
				{
					case HUMAN:
					case ELF:
					case DARK_ELF:
					case DWARF:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_R_ROBE, 1, player, true);
						}
						else if (CategoryData.getInstance().isInCategory(CategoryType.SIXTH_OTHEL_GROUP, classId.getId()) || CategoryData.getInstance().isInCategory(CategoryType.SIXTH_YR_GROUP, classId.getId()))
						{
							player.addItem(getClass().getSimpleName(), BOX_R_LIGHT, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_R_HEAVY, 1, player, true);
						}
						break;
					}
					case ORC:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_R_LIGHT, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_R_HEAVY, 1, player, true);
						}
						break;
					}
					case KAMAEL:
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_R_ROBE, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_R_LIGHT, 1, player, true);
							break;
						}
					case ERTHEIA:
					{
						if (player.isMageClass())
						{
							player.addItem(getClass().getSimpleName(), BOX_R_ROBE, 1, player, true);
						}
						else
						{
							player.addItem(getClass().getSimpleName(), BOX_R_LIGHT, 1, player, true);
						}
						break;
					}
				}
				break;
			}
		}
		return true;
	}
}
