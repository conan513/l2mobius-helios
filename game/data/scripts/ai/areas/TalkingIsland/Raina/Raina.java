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
package ai.areas.TalkingIsland.Raina;

import static com.l2jmobius.gameserver.model.base.ClassLevel.THIRD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.data.xml.impl.CategoryData;
import com.l2jmobius.gameserver.data.xml.impl.ClassListData;
import com.l2jmobius.gameserver.data.xml.impl.SkillTreesData;
import com.l2jmobius.gameserver.enums.CategoryType;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.enums.SubclassInfoType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.base.ClassId;
import com.l2jmobius.gameserver.model.base.ClassLevel;
import com.l2jmobius.gameserver.model.base.PlayerClass;
import com.l2jmobius.gameserver.model.base.SubClass;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.ListenerRegisterType;
import com.l2jmobius.gameserver.model.events.annotations.Id;
import com.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import com.l2jmobius.gameserver.model.events.annotations.RegisterType;
import com.l2jmobius.gameserver.model.events.impl.character.npc.OnNpcMenuSelect;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.AcquireSkillList;
import com.l2jmobius.gameserver.network.serverpackets.ExSubjobInfo;
import com.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmobius.gameserver.network.serverpackets.SocialAction;
import com.l2jmobius.gameserver.network.serverpackets.SystemMessage;

import ai.AbstractNpcAI;
import quests.Q10385_RedThreadOfFate.Q10385_RedThreadOfFate;
import quests.Q10472_WindsOfFateEncroachingShadows.Q10472_WindsOfFateEncroachingShadows;

/**
 * Raina AI.
 * @author St3eT
 */
public final class Raina extends AbstractNpcAI
{
	// NPC
	private static final int RAINA = 33491;
	// Items
	private static final int SUBCLASS_CERTIFICATE = 30433;
	private static final int CHAOS_POMANDER = 37375;
	private static final int ABELIUS_POWER = 32264;
	private static final int SAPYROS_POWER = 32265;
	private static final int ASHAGEN_POWER = 32266;
	private static final int CRANIGG_POWER = 32267;
	private static final int SOLTKREIG_POWER = 32268;
	private static final int NAVIAROPE_POWER = 32269;
	private static final int LEISTER_POWER = 32270;
	private static final int LAKCIS_POWER = 32271;
	// Misc
	private static final Set<PlayerClass> mainSubclassSet;
	private static final Set<PlayerClass> neverSubclassed = EnumSet.of(PlayerClass.Overlord, PlayerClass.Warsmith);
	private static final Set<PlayerClass> subclasseSet1 = EnumSet.of(PlayerClass.DarkAvenger, PlayerClass.Paladin, PlayerClass.TempleKnight, PlayerClass.ShillienKnight);
	private static final Set<PlayerClass> subclasseSet2 = EnumSet.of(PlayerClass.TreasureHunter, PlayerClass.AbyssWalker, PlayerClass.Plainswalker);
	private static final Set<PlayerClass> subclasseSet3 = EnumSet.of(PlayerClass.Hawkeye, PlayerClass.SilverRanger, PlayerClass.PhantomRanger);
	private static final Set<PlayerClass> subclasseSet4 = EnumSet.of(PlayerClass.Warlock, PlayerClass.ElementalSummoner, PlayerClass.PhantomSummoner);
	private static final Set<PlayerClass> subclasseSet5 = EnumSet.of(PlayerClass.Sorceror, PlayerClass.Spellsinger, PlayerClass.Spellhowler);
	private static final EnumMap<PlayerClass, Set<PlayerClass>> subclassSetMap = new EnumMap<>(PlayerClass.class);
	static
	{
		final Set<PlayerClass> subclasses = PlayerClass.getSet(null, THIRD);
		subclasses.removeAll(neverSubclassed);
		mainSubclassSet = subclasses;
		subclassSetMap.put(PlayerClass.DarkAvenger, subclasseSet1);
		subclassSetMap.put(PlayerClass.Paladin, subclasseSet1);
		subclassSetMap.put(PlayerClass.TempleKnight, subclasseSet1);
		subclassSetMap.put(PlayerClass.ShillienKnight, subclasseSet1);
		subclassSetMap.put(PlayerClass.TreasureHunter, subclasseSet2);
		subclassSetMap.put(PlayerClass.AbyssWalker, subclasseSet2);
		subclassSetMap.put(PlayerClass.Plainswalker, subclasseSet2);
		subclassSetMap.put(PlayerClass.Hawkeye, subclasseSet3);
		subclassSetMap.put(PlayerClass.SilverRanger, subclasseSet3);
		subclassSetMap.put(PlayerClass.PhantomRanger, subclasseSet3);
		subclassSetMap.put(PlayerClass.Warlock, subclasseSet4);
		subclassSetMap.put(PlayerClass.ElementalSummoner, subclasseSet4);
		subclassSetMap.put(PlayerClass.PhantomSummoner, subclasseSet4);
		subclassSetMap.put(PlayerClass.Sorceror, subclasseSet5);
		subclassSetMap.put(PlayerClass.Spellsinger, subclasseSet5);
		subclassSetMap.put(PlayerClass.Spellhowler, subclasseSet5);
	}
	
	private static final Map<CategoryType, Integer> classCloak = new HashMap<>();
	{
		classCloak.put(CategoryType.SIXTH_SIGEL_GROUP, 30310); // Abelius Cloak
		classCloak.put(CategoryType.SIXTH_TIR_GROUP, 30311); // Sapyros Cloak Grade
		classCloak.put(CategoryType.SIXTH_OTHEL_GROUP, 30312); // Ashagen Cloak Grade
		classCloak.put(CategoryType.SIXTH_YR_GROUP, 30313); // Cranigg Cloak Grade
		classCloak.put(CategoryType.SIXTH_FEOH_GROUP, 30314); // Soltkreig Cloak Grade
		classCloak.put(CategoryType.SIXTH_WYNN_GROUP, 30315); // Naviarope Cloak Grade
		classCloak.put(CategoryType.SIXTH_IS_GROUP, 30316); // Leister Cloak Grade
		classCloak.put(CategoryType.SIXTH_EOLH_GROUP, 30317); // Laksis Cloak Grade
	}
	
	private static final List<PlayerClass> dualClassList = new ArrayList<>();
	{
		dualClassList.addAll(Arrays.asList(PlayerClass.sigelPhoenixKnight, PlayerClass.sigelHellKnight, PlayerClass.sigelEvasTemplar, PlayerClass.sigelShilenTemplar));
		dualClassList.addAll(Arrays.asList(PlayerClass.tyrrDuelist, PlayerClass.tyrrDreadnought, PlayerClass.tyrrTitan, PlayerClass.tyrrGrandKhavatari, PlayerClass.tyrrDoombringer));
		dualClassList.addAll(Arrays.asList(PlayerClass.othellAdventurer, PlayerClass.othellWindRider, PlayerClass.othellGhostHunter, PlayerClass.othellFortuneSeeker));
		dualClassList.addAll(Arrays.asList(PlayerClass.yulSagittarius, PlayerClass.yulMoonlightSentinel, PlayerClass.yulGhostSentinel, PlayerClass.yulTrickster));
		dualClassList.addAll(Arrays.asList(PlayerClass.feohArchmage, PlayerClass.feohSoultaker, PlayerClass.feohMysticMuse, PlayerClass.feoStormScreamer, PlayerClass.feohSoulHound));
		dualClassList.addAll(Arrays.asList(PlayerClass.issHierophant, PlayerClass.issSwordMuse, PlayerClass.issSpectralDancer, PlayerClass.issDoomcryer));
		dualClassList.addAll(Arrays.asList(PlayerClass.wynnArcanaLord, PlayerClass.wynnElementalMaster, PlayerClass.wynnSpectralMaster));
		dualClassList.addAll(Arrays.asList(PlayerClass.aeoreCardinal, PlayerClass.aeoreEvaSaint, PlayerClass.aeoreShillienSaint));
	}
	
	// @formatter:off
	private static final int[] REAWAKEN_PRICE =
	{
		100_000_000, 90_000_000, 80_000_000, 70_000_000, 60_000_000, 50_000_000, 40_000_000, 30_000_000, 20_000_000, 10_000_000
	};
	// @formatter:on
	
	private Raina()
	{
		addStartNpc(RAINA);
		addFirstTalkId(RAINA);
		addTalkId(RAINA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "33491-01.html":
			case "33491-02.html":
			case "33491-03.html":
			case "33491-04.html":
			case "33491-05.html":
			case "reawakenCancel.html":
			{
				htmltext = event;
				break;
			}
			case "addSubclass":
			{
				if (player.isTransformed())
				{
					htmltext = "noTransform.html";
				}
				else if (player.hasSummon())
				{
					htmltext = "noSummon.html";
				}
				else if (player.getRace() == Race.ERTHEIA)
				{
					htmltext = "noErtheia.html";
				}
				else if (!haveDoneQuest(player, false))
				{
					htmltext = "noQuest.html";
				}
				else if (!hasAllSubclassLeveled(player) || (player.getTotalSubClasses() >= Config.MAX_SUBCLASS))
				{
					htmltext = "addFailed.html";
				}
				else if (!player.isInventoryUnder90(true) || (player.getWeightPenalty() >= 2))
				{
					htmltext = "inventoryLimit.html";
				}
				else
				{
					final Set<PlayerClass> availSubs = getAvailableSubClasses(player);
					final StringBuilder sb = new StringBuilder();
					final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "subclassList.html");
					
					if ((availSubs == null) || availSubs.isEmpty())
					{
						break;
					}
					
					for (PlayerClass subClass : availSubs)
					{
						if (subClass != null)
						{
							final int classId = subClass.ordinal();
							final int npcStringId = 11170000 + classId;
							sb.append("<fstring p1=\"0\" p2=\"" + classId + "\">" + npcStringId + "</fstring>");
						}
					}
					html.replace("%subclassList%", sb.toString());
					player.sendPacket(html);
				}
				break;
			}
			case "removeSubclass":
			{
				if (player.isTransformed())
				{
					htmltext = "noTransform.html";
				}
				else if (player.hasSummon())
				{
					htmltext = "noSummon.html";
				}
				else if (player.getRace() == Race.ERTHEIA)
				{
					htmltext = "noErtheia.html";
				}
				else if (!player.isInventoryUnder90(true) || (player.getWeightPenalty() >= 2))
				{
					htmltext = "inventoryLimit.html";
				}
				else if (player.getSubClasses().isEmpty())
				{
					htmltext = "noSubChange.html";
				}
				else
				{
					final StringBuilder sb = new StringBuilder();
					final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "subclassRemoveList.html");
					
					for (SubClass subClass : player.getSubClasses().values())
					{
						if (subClass != null)
						{
							final int classId = subClass.getClassId();
							final int npcStringId = 11170000 + classId;
							sb.append("<fstring p1=\"2\" p2=\"" + subClass.getClassIndex() + "\">" + npcStringId + "</fstring>");
						}
					}
					html.replace("%removeList%", sb.toString());
					player.sendPacket(html);
				}
				break;
			}
			case "changeSubclass": // TODO: Finish me
			{
				if (player.isTransformed())
				{
					htmltext = "noTransform.html";
				}
				else if (player.hasSummon())
				{
					htmltext = "noSummon.html";
				}
				else if (player.getRace() == Race.ERTHEIA)
				{
					htmltext = "noErtheia.html";
				}
				else if (!player.isInventoryUnder80(true) || (player.getWeightPenalty() >= 2))
				{
					htmltext = "inventoryLimit.html";
				}
				else if (player.getSubClasses().isEmpty())
				{
					htmltext = "noSubChange.html";
				}
				else if (!hasQuestItems(player, SUBCLASS_CERTIFICATE))
				{
					htmltext = "noCertificate.html";
				}
				else
				{
					player.sendMessage("Not done yet.");
				}
				break;
			}
			case "ertheiaDualClass":
			{
				// TODO: Maybe html is different when you have 85lvl but you haven't completed quest
				if ((player.getRace() != Race.ERTHEIA) || (player.getLevel() < 85) || !player.isInCategory(CategoryType.SIXTH_CLASS_GROUP) || player.hasDualClass() || !haveDoneQuest(player, true))
				{
					htmltext = "addDualClassErtheiaFailed.html";
				}
				else
				{
					htmltext = "addDualClassErtheia.html";
				}
				break;
			}
			case "addDualClass_SIXTH_SIGEL_GROUP":
			case "addDualClass_SIXTH_TIR_GROUP":
			case "addDualClass_SIXTH_OTHEL_GROUP":
			case "addDualClass_SIXTH_YR_GROUP":
			case "addDualClass_SIXTH_FEOH_GROUP":
			case "addDualClass_SIXTH_IS_GROUP":
			case "addDualClass_SIXTH_WYNN_GROUP":
			case "addDualClass_SIXTH_EOLH_GROUP":
			{
				if ((player.getRace() != Race.ERTHEIA) || (player.getLevel() < 85) || !player.isInCategory(CategoryType.SIXTH_CLASS_GROUP) || player.hasDualClass() || !haveDoneQuest(player, true))
				{
					htmltext = "addDualClassErtheiaFailed.html";
					break;
				}
				
				final CategoryType cType = CategoryType.valueOf(event.replace("addDualClass_", ""));
				
				if (cType == null)
				{
					LOGGER.warning(getClass().getSimpleName() + ": Cannot parse CategoryType, event: " + event);
				}
				
				final StringBuilder sb = new StringBuilder();
				final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "addDualClassErtheiaList.html");
				
				for (PlayerClass dualClasses : getDualClasses(player, cType))
				{
					if (dualClasses != null)
					{
						sb.append("<button value=\"" + ClassListData.getInstance().getClass(dualClasses.ordinal()).getClassName() + "\" action=\"bypass -h menu_select?ask=6&reply=" + dualClasses.ordinal() + "\" width=\"200\" height=\"31\" back=\"L2UI_CT1.HtmlWnd_DF_Awake_Down\" fore=\"L2UI_CT1.HtmlWnd_DF_Awake\"><br>");
					}
				}
				html.replace("%dualclassList%", sb.toString());
				player.sendPacket(html);
				break;
			}
			case "reawakenDualclass":
			{
				if (player.isTransformed())
				{
					htmltext = "noTransform.html";
				}
				else if (player.hasSummon())
				{
					htmltext = "noSummon.html";
				}
				else if (!player.hasDualClass() || (player.getLevel() < 85) || !player.isDualClassActive() || (player.getClassId().level() != ClassLevel.AWAKEN.ordinal()))
				{
					htmltext = "reawakenNoDual.html";
				}
				else if (!player.isInventoryUnder80(true) || (player.getWeightPenalty() >= 2))
				{
					htmltext = "inventoryLimit.html";
				}
				else
				{
					final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "reawaken.html");
					final int index = player.getLevel() > 94 ? REAWAKEN_PRICE.length - 1 : player.getLevel() - 85;
					html.replace("%price%", REAWAKEN_PRICE[index]);
					player.sendPacket(html);
				}
				break;
			}
			case "reawakenDualclassConfirm":
			{
				final int index = player.getLevel() > 94 ? REAWAKEN_PRICE.length - 1 : player.getLevel() - 85;
				if (player.isTransformed())
				{
					htmltext = "noTransform.html";
				}
				else if (player.hasSummon())
				{
					htmltext = "noSummon.html";
				}
				else if (!player.hasDualClass() || !player.isDualClassActive() || (player.getClassId().level() != ClassLevel.AWAKEN.ordinal()))
				{
					htmltext = "reawakenNoDual.html";
				}
				else if ((player.getAdena() < REAWAKEN_PRICE[index]) || !hasQuestItems(player, getCloakId(player)))
				{
					final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "reawakenNoFee.html");
					html.replace("%price%", REAWAKEN_PRICE[index]);
					player.sendPacket(html);
				}
				else
				{
					final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "reawakenList.html");
					player.sendPacket(html);
				}
				break;
			}
			case "reawaken_SIXTH_SIGEL_GROUP":
			case "reawaken_SIXTH_TIR_GROUP":
			case "reawaken_SIXTH_OTHEL_GROUP":
			case "reawaken_SIXTH_YR_GROUP":
			case "reawaken_SIXTH_FEOH_GROUP":
			case "reawaken_SIXTH_IS_GROUP":
			case "reawaken_SIXTH_WYNN_GROUP":
			case "reawaken_SIXTH_EOLH_GROUP":
			{
				final CategoryType cType = CategoryType.valueOf(event.replace("reawaken_", ""));
				
				if (cType == null)
				{
					LOGGER.warning(getClass().getSimpleName() + ": Cannot parse CategoryType, event: " + event);
				}
				
				final StringBuilder sb = new StringBuilder();
				final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "reawakenClassList.html");
				
				for (PlayerClass dualClasses : getDualClasses(player, cType))
				{
					if (dualClasses != null)
					{
						sb.append("<button value=\"" + ClassListData.getInstance().getClass(dualClasses.ordinal()).getClassName() + "\" action=\"bypass -h menu_select?ask=5&reply=" + dualClasses.ordinal() + "\" width=\"200\" height=\"31\" back=\"L2UI_CT1.HtmlWnd_DF_Awake_Down\" fore=\"L2UI_CT1.HtmlWnd_DF_Awake\"><br>");
					}
				}
				html.replace("%dualclassList%", sb.toString());
				player.sendPacket(html);
				break;
			}
			case "upgradeSubClassToDualClass":
			{
				if (Config.ALT_GAME_DUALCLASS_WITHOUT_QUEST)
				{
					if (player.isTransformed())
					{
						htmltext = "noTransform.html";
					}
					else if (player.hasSummon())
					{
						htmltext = "noSummon.html";
					}
					else if (player.getRace() == Race.ERTHEIA)
					{
						htmltext = "noErtheia.html";
					}
					else if (!player.isInventoryUnder90(true) || (player.getWeightPenalty() >= 2))
					{
						htmltext = "inventoryLimit.html";
					}
					else if (player.hasDualClass() || !player.isSubClassActive() || (player.getLevel() < 80))
					{
						htmltext = "addDualClassWithoutQuestFailed.html";
					}
					else
					{
						player.getSubClasses().get(player.getClassIndex()).setIsDualClass(true);
						
						final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.SUBCLASS_S1_HAS_BEEN_UPGRADED_TO_DUEL_CLASS_S2_CONGRATULATIONS);
						msg.addClassId(player.getClassId().getId());
						msg.addClassId(player.getClassId().getId());
						player.sendPacket(msg);
						
						player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.CLASS_CHANGED));
						player.broadcastSocialAction(SocialAction.LEVEL_UP);
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_NPC_MENU_SELECT)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(RAINA)
	public final void OnNpcMenuSelect(OnNpcMenuSelect event)
	{
		final L2PcInstance player = event.getTalker();
		final L2Npc npc = event.getNpc();
		final int ask = event.getAsk();
		
		switch (ask)
		{
			case 0: // Add subclass confirm menu
			{
				final int classId = event.getReply();
				final StringBuilder sb = new StringBuilder();
				final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "addConfirm.html");
				
				if (!isValidNewSubClass(player, classId))
				{
					return;
				}
				
				final int npcStringId = 11170000 + classId;
				sb.append("<fstring p1=\"1\" p2=\"" + classId + "\">" + npcStringId + "</fstring>");
				html.replace("%confirmButton%", sb.toString());
				player.sendPacket(html);
				break;
			}
			case 1: // Add subclass
			{
				final int classId = event.getReply();
				if (!isValidNewSubClass(player, classId))
				{
					return;
				}
				
				if (!player.addSubClass(classId, player.getTotalSubClasses() + 1, false))
				{
					return;
				}
				
				player.setActiveClass(player.getTotalSubClasses());
				player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.NEW_SLOT_USED));
				player.sendPacket(SystemMessageId.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
				player.sendPacket(getNpcHtmlMessage(player, npc, "addSuccess.html"));
				break;
			}
			case 2: // Remove (change) subclass list
			{
				final int subclassIndex = event.getReply();
				final Set<PlayerClass> availSubs = getAvailableSubClasses(player);
				final StringBuilder sb = new StringBuilder();
				final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "removeSubclassList.html");
				
				if ((availSubs == null) || availSubs.isEmpty())
				{
					return;
				}
				
				for (PlayerClass subClass : availSubs)
				{
					if (subClass != null)
					{
						final int classId = subClass.ordinal();
						final int npcStringId = 11170000 + classId;
						sb.append("<fstring p1=\"3\" p2=\"" + classId + "\">" + npcStringId + "</fstring>");
					}
				}
				npc.getVariables().set("SUBCLASS_INDEX_" + player.getObjectId(), subclassIndex);
				html.replace("%subclassList%", sb.toString());
				player.sendPacket(html);
				break;
			}
			case 3: // Remove (change) subclass confirm menu
			{
				final int classId = event.getReply();
				final int classIndex = npc.getVariables().getInt("SUBCLASS_INDEX_" + player.getObjectId(), -1);
				if (classIndex < 0)
				{
					return;
				}
				
				final StringBuilder sb = new StringBuilder();
				final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "addConfirm2.html");
				final int npcStringId = 11170000 + classId;
				sb.append("<fstring p1=\"4\" p2=\"" + classId + "\">" + npcStringId + "</fstring>");
				html.replace("%confirmButton%", sb.toString());
				player.sendPacket(html);
				break;
			}
			case 4: // Remove (change) subclass
			{
				final int classId = event.getReply();
				final int classIndex = npc.getVariables().getInt("SUBCLASS_INDEX_" + player.getObjectId(), -1);
				if (classIndex < 0)
				{
					return;
				}
				
				if (player.modifySubClass(classIndex, classId, false))
				{
					player.abortCast();
					player.stopAllEffectsExceptThoseThatLastThroughDeath();
					player.stopAllEffects();
					player.stopCubics();
					player.setActiveClass(classIndex);
					player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.CLASS_CHANGED));
					player.sendPacket(getNpcHtmlMessage(player, npc, "addSuccess.html"));
					player.sendPacket(SystemMessageId.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
				}
				break;
			}
			case 5: // Reawaken (change dual class)
			{
				final int classId = event.getReply();
				if (player.isTransformed() || player.hasSummon() || (!player.hasDualClass() || !player.isDualClassActive() || (player.getClassId().level() != ClassLevel.AWAKEN.ordinal())))
				{
					break;
				}
				
				// Validating classId
				if (!getDualClasses(player, null).contains(PlayerClass.values()[classId]))
				{
					break;
				}
				
				final int index = player.getLevel() > 94 ? REAWAKEN_PRICE.length - 1 : player.getLevel() - 85;
				if ((player.getAdena() < REAWAKEN_PRICE[index]) || !hasQuestItems(player, getCloakId(player)))
				{
					final NpcHtmlMessage html = getNpcHtmlMessage(player, npc, "reawakenNoFee.html");
					html.replace("%price%", REAWAKEN_PRICE[index]);
					player.sendPacket(html);
					break;
				}
				
				player.reduceAdena((getClass().getSimpleName() + "_Reawaken"), REAWAKEN_PRICE[index], npc, true);
				takeItems(player, getCloakId(player), 1);
				
				final int classIndex = player.getClassIndex();
				if (player.modifySubClass(classIndex, classId, true))
				{
					player.abortCast();
					player.stopAllEffectsExceptThoseThatLastThroughDeath();
					player.stopAllEffects();
					player.stopCubics();
					player.setActiveClass(classIndex);
					player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.CLASS_CHANGED));
					player.sendPacket(getNpcHtmlMessage(player, npc, "reawakenSuccess.html"));
					SkillTreesData.getInstance().cleanSkillUponAwakening(player);
					player.sendPacket(new AcquireSkillList(player));
					player.sendSkillList();
					addPowerItem(player);
				}
				break;
			}
			case 6: // Add dual class for ertheia
			{
				final int classId = event.getReply();
				if (player.isTransformed() || player.hasSummon() || player.hasDualClass())
				{
					break;
				}
				
				final QuestState qs = player.getQuestState(Q10472_WindsOfFateEncroachingShadows.class.getSimpleName());
				if (((qs == null) || !qs.isCompleted()) && !Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS)
				{
					break;
				}
				
				// Validating classId
				if (!getDualClasses(player, null).contains(PlayerClass.values()[classId]))
				{
					break;
				}
				
				if (player.addSubClass(classId, player.getTotalSubClasses() + 1, true))
				{
					player.setActiveClass(player.getTotalSubClasses());
					player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.NEW_SLOT_USED));
					player.sendPacket(SystemMessageId.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
					player.sendPacket(getNpcHtmlMessage(player, npc, "addSuccess.html"));
					SkillTreesData.getInstance().cleanSkillUponAwakening(player);
					player.sendPacket(new AcquireSkillList(player));
					player.sendSkillList();
					addPowerItem(player);
					giveItems(player, CHAOS_POMANDER, 2);
				}
				break;
			}
		}
	}
	
	private void addPowerItem(L2PcInstance player)
	{
		int itemId = ABELIUS_POWER; // Sigel
		if (player.isInCategory(CategoryType.SIXTH_TIR_GROUP))
		{
			itemId = SAPYROS_POWER;
		}
		else if (player.isInCategory(CategoryType.SIXTH_OTHEL_GROUP))
		{
			itemId = ASHAGEN_POWER;
		}
		else if (player.isInCategory(CategoryType.SIXTH_YR_GROUP))
		{
			itemId = CRANIGG_POWER;
		}
		else if (player.isInCategory(CategoryType.SIXTH_FEOH_GROUP))
		{
			itemId = SOLTKREIG_POWER;
		}
		else if (player.isInCategory(CategoryType.SIXTH_WYNN_GROUP))
		{
			itemId = NAVIAROPE_POWER;
		}
		else if (player.isInCategory(CategoryType.SIXTH_IS_GROUP))
		{
			itemId = LEISTER_POWER;
		}
		else if (player.isInCategory(CategoryType.SIXTH_EOLH_GROUP))
		{
			itemId = LAKCIS_POWER;
		}
		giveItems(player, itemId, 1);
	}
	
	/**
	 * Returns list of available subclasses Base class and already used subclasses removed
	 * @param player
	 * @return
	 */
	private Set<PlayerClass> getAvailableSubClasses(L2PcInstance player)
	{
		final int currentBaseId = player.getBaseClass();
		final ClassId baseCID = ClassId.getClassId(currentBaseId);
		final int baseClassId = (baseCID.level() > 2) ? baseCID.getParent().ordinal() : currentBaseId;
		
		final Set<PlayerClass> availSubs = getSubclasses(player, baseClassId);
		
		if ((availSubs != null) && !availSubs.isEmpty())
		{
			for (PlayerClass pclass : availSubs)
			{
				// scan for already used subclasses
				final int availClassId = pclass.ordinal();
				final ClassId cid = ClassId.getClassId(availClassId);
				
				for (SubClass subList : player.getSubClasses().values())
				{
					final ClassId subId = ClassId.getClassId(subList.getClassId());
					
					if (subId.equalsOrChildOf(cid))
					{
						availSubs.remove(pclass);
						break;
					}
				}
			}
		}
		return availSubs;
	}
	
	private boolean haveDoneQuest(L2PcInstance player, boolean isErtheia)
	{
		final QuestState qs = isErtheia ? player.getQuestState(Q10472_WindsOfFateEncroachingShadows.class.getSimpleName()) : player.getQuestState(Q10385_RedThreadOfFate.class.getSimpleName());
		return (((qs != null) && qs.isCompleted()) || Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS);
	}
	
	/**
	 * Check new subclass classId for validity. Base class not added into allowed subclasses.
	 * @param player
	 * @param classId
	 * @return
	 */
	private boolean isValidNewSubClass(L2PcInstance player, int classId)
	{
		final ClassId cid = ClassId.values()[classId];
		ClassId subClassId;
		for (SubClass subList : player.getSubClasses().values())
		{
			subClassId = ClassId.values()[subList.getClassId()];
			
			if (subClassId.equalsOrChildOf(cid))
			{
				return false;
			}
		}
		
		// get player base class
		final int currentBaseId = player.getBaseClass();
		final ClassId baseCID = ClassId.getClassId(currentBaseId);
		
		// we need 2nd occupation ID
		final int baseClassId = baseCID.level() > 2 ? baseCID.getParent().ordinal() : currentBaseId;
		final Set<PlayerClass> availSubs = getSubclasses(player, baseClassId);
		
		if ((availSubs == null) || availSubs.isEmpty())
		{
			return false;
		}
		
		boolean found = false;
		for (PlayerClass pclass : availSubs)
		{
			if (pclass.ordinal() == classId)
			{
				found = true;
				break;
			}
		}
		return found;
	}
	
	private boolean hasAllSubclassLeveled(L2PcInstance player)
	{
		boolean leveled = true;
		
		for (SubClass sub : player.getSubClasses().values())
		{
			if ((sub != null) && (sub.getLevel() < 75))
			{
				leveled = false;
			}
		}
		return leveled;
	}
	
	public final List<PlayerClass> getAvailableDualclasses(L2PcInstance player)
	{
		final List<PlayerClass> dualClasses = new ArrayList<>();
		
		for (PlayerClass playerClass : PlayerClass.values())
		{
			if (!playerClass.isOfRace(Race.ERTHEIA) && playerClass.isOfLevel(ClassLevel.AWAKEN) && (playerClass.ordinal() != player.getClassId().getId()))
			{
				dualClasses.add(playerClass);
			}
		}
		return dualClasses;
	}
	
	private List<PlayerClass> getDualClasses(L2PcInstance player, CategoryType cType)
	{
		final List<PlayerClass> tempList = new ArrayList<>();
		final int baseClassId = player.getBaseClass();
		final int dualClassId = player.getClassId().getId();
		
		for (PlayerClass temp : dualClassList)
		{
			if ((temp.ordinal() != baseClassId) && (temp.ordinal() != dualClassId) && ((cType == null) || CategoryData.getInstance().isInCategory(cType, temp.ordinal())))
			{
				tempList.add(temp);
			}
		}
		return tempList;
	}
	
	public final Set<PlayerClass> getSubclasses(L2PcInstance player, int classId)
	{
		Set<PlayerClass> subclasses = null;
		final PlayerClass pClass = PlayerClass.values()[classId];
		
		if ((pClass.getLevel() == THIRD) || (pClass.getLevel() == ClassLevel.FOURTH))
		{
			subclasses = EnumSet.copyOf(mainSubclassSet);
			subclasses.remove(pClass);
			subclasses.removeAll(PlayerClass.getSet(Race.ERTHEIA, THIRD));
			
			if (player.getRace() == Race.KAMAEL)
			{
				if (player.getAppearance().getSex())
				{
					subclasses.remove(PlayerClass.femaleSoulbreaker);
				}
				else
				{
					subclasses.remove(PlayerClass.maleSoulbreaker);
				}
				
				if (!player.getSubClasses().containsKey(2) || (player.getSubClasses().get(2).getLevel() < 75))
				{
					subclasses.remove(PlayerClass.inspector);
				}
			}
			else
			{
				// Only Kamael can take Kamael classes as subclasses.
				subclasses.removeAll(PlayerClass.getSet(Race.KAMAEL, THIRD));
			}
			
			final Set<PlayerClass> unavailableClasses = subclassSetMap.get(pClass);
			
			if (unavailableClasses != null)
			{
				subclasses.removeAll(unavailableClasses);
			}
		}
		
		if (subclasses != null)
		{
			final ClassId currClassId = ClassId.getClassId(player.getClassId().getId());
			for (PlayerClass tempClass : subclasses)
			{
				final ClassId tempClassId = ClassId.getClassId(tempClass.ordinal());
				
				if (currClassId.equalsOrChildOf(tempClassId))
				{
					subclasses.remove(tempClass);
				}
			}
		}
		return subclasses;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (Config.ALT_GAME_DUALCLASS_WITHOUT_QUEST)
		{
			return "addDualClassWithoutQuest.html";
		}
		return "33491.html";
	}
	
	private NpcHtmlMessage getNpcHtmlMessage(L2PcInstance player, L2Npc npc, String fileName)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		final String text = getHtm(player, fileName);
		if (text == null)
		{
			LOGGER.info("Cannot find HTML file for " + Raina.class.getSimpleName() + " AI: " + fileName);
			return null;
		}
		html.setHtml(text);
		return html;
	}
	
	private int getCloakId(L2PcInstance player)
	{
		CategoryType catType = null;
		
		if (player.isInCategory(CategoryType.SIXTH_SIGEL_GROUP))
		{
			catType = CategoryType.SIXTH_SIGEL_GROUP;
		}
		else if (player.isInCategory(CategoryType.SIXTH_TIR_GROUP))
		{
			catType = CategoryType.SIXTH_TIR_GROUP;
		}
		else if (player.isInCategory(CategoryType.SIXTH_OTHEL_GROUP))
		{
			catType = CategoryType.SIXTH_OTHEL_GROUP;
		}
		else if (player.isInCategory(CategoryType.SIXTH_YR_GROUP))
		{
			catType = CategoryType.SIXTH_YR_GROUP;
		}
		else if (player.isInCategory(CategoryType.SIXTH_FEOH_GROUP))
		{
			catType = CategoryType.SIXTH_FEOH_GROUP;
		}
		else if (player.isInCategory(CategoryType.SIXTH_IS_GROUP))
		{
			catType = CategoryType.SIXTH_IS_GROUP;
		}
		else if (player.isInCategory(CategoryType.SIXTH_WYNN_GROUP))
		{
			catType = CategoryType.SIXTH_WYNN_GROUP;
		}
		else if (player.isInCategory(CategoryType.SIXTH_EOLH_GROUP))
		{
			catType = CategoryType.SIXTH_EOLH_GROUP;
		}
		return classCloak.get(catType);
	}
	
	public static void main(String[] args)
	{
		new Raina();
	}
}