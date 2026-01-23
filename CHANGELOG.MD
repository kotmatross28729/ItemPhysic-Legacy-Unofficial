# 1.3.1

- Added support for Tinkers construct items to experience client-side physics by @Gamingb3ast

# 1.3.0

- Expose falling sound customization to config by @donzilzenovka

# 1.2.9

- Fixed a bug where fire from ignitable items could be placed on the client side (server desync)

# 1.2.8

#### WARNING:

1) Requires [UniMixins](https://github.com/LegacyModdingMC/UniMixins/releases)

2) Need to delete config - `minecraft\config\itemphysic.cfg`

---

- Full migration from ASM to mixins (**removed dependency on creativecore**)

- Added the ability to change some (general) of the configs without leaving the game (Forge ingame config)
  - However, lists (Burn/Float/Explosion etc.) still need to be modified manually

- Significantly improve the performance of lists (Burn/Float/Explosion etc.)

- Added several configuration options:
  - `throwPowerTickCoefficient`- Adjusts the number of ticks required to increase the throwing power level of an item
  - `throwPowerMax` - Maximum throwing power level
  - `playerPickupExpansionXZ` - Extends the distance in X and Z from which the player automatically (if `customPickup` is enabled, then by pressing shift) picks up items
  - `playerPickupExpansionY` - Extends the distance in Y from which the player automatically (if `customPickup` is enabled, then by pressing shift) picks up items

- Fixed... whatever, I forgot
  - Definitely something has been fixed regarding customThrow

---

# 1.2.7.2

- Fixed incorrect display of metadata (see #12)

---

# 1.2.7

- Fix names of igniting items becoming "Unnamed" if hbm's ntm is not installed

---

# 1.2.6

### WARNING: all configs related to lists were moved to a separate folder and separated (.minecraft\config\itemphysic\someList.cfg)

- Fix items spinning issue in general (Thaumcraft / Mo' Creatures / ChocolateQuest / YeGamolChattels)


- Backport show tooltip option (customPickup)


- Backport igniting items


- Backport item fall sounds
