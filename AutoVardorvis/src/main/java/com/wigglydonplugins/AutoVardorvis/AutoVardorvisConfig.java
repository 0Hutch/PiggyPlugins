package com.wigglydonplugins.AutoVardorvis;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("AutoVardorvis")
public interface AutoVardorvisConfig extends Config {

  @ConfigItem(
      keyName = "eatat",
      name = "Eat at",
      description = "Eat at what health?",
      position = 0
  )
  default int EATAT() {
    return 75;
  }

  @ConfigItem(
      keyName = "drinkprayerat",
      name = "Drink prayer potion at",
      description = "Drink prayer potion when?",
      position = 1
  )
  default int DRINKPRAYERAT() {
    return 15;
  }

  @ConfigItem(
      keyName = "minFood",
      name = "minimum food",
      description = "min food for another kill",
      position = 2
  )
  default int MIN_FOOD() {
    return 7;
  }

  @ConfigItem(
      keyName = "minPrayer",
      name = "minimum prayer",
      description = "min ppots for another kill",
      position = 2
  )
  default int MIN_PRAYER_POTIONS() {
    return 1;
  }

  @ConfigItem(
      keyName = "ppotsToBring",
      name = "ppots to bring",
      description = "how many ppots to bring?",
      position = 3
  )
  default int PPOTS_TO_BRING() {
    return 3;
  }


}
