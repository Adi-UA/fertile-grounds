package com.fertilegrounds;

import com.fertilegrounds.block.ModBlocks;
import com.fertilegrounds.entity.ModEntities;
import com.fertilegrounds.fairy.FairyGiftCooldowns;
import com.fertilegrounds.fairy.FairyVisitScheduler;
import com.fertilegrounds.fairy.FairyVisits;
import com.fertilegrounds.hotel.FairyHotelLookup;
import com.fertilegrounds.item.ModDataComponents;
import com.fertilegrounds.item.ModItemGroups;
import com.fertilegrounds.item.ModItems;
import com.fertilegrounds.util.ModCriteria;
import com.fertilegrounds.util.ModGameRules;
import com.fertilegrounds.util.ModSounds;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FertileGrounds implements ModInitializer {
  public static final String MOD_ID = "fertilegrounds";

  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    LOGGER.info("Fertile Grounds initializing");
    ModGameRules.register();
    ModSounds.register();
    ModCriteria.register();
    ModDataComponents.register();
    ModBlocks.register();
    FairyHotelLookup.register();
    ModEntities.register();
    ModItems.register();
    ModItemGroups.register();
    FairyVisits.register();
    FairyGiftCooldowns.register();
    FairyVisitScheduler.register();
  }
}
