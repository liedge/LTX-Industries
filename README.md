<p align="center"><img src="LOGO.webp" alt="project logo" /></p>

-----

# LimaTech - Official Repository

## Official Releases

You can download official releases from these sources:
- <a href="https://www.curseforge.com/minecraft/mc-mods/limatech"><img alt="CurseForge Downloads" src="https://img.shields.io/curseforge/dt/1075456?logo=curseforge&logoColor=%23F16436&label=CurseForge&color=%23F16436" /></a>
- <a href="https://modrinth.com/mod/limatech"><img alt="Modrinth Downloads" src="https://img.shields.io/modrinth/dt/LQRvkGba?logo=modrinth&logoColor=%2300AF5C&label=Modrinth&color=%2300AF5C" /></a>

## License
- **Source Code**
  - Copyright © 2024-2025 Eduardo Jimenez
  - ![Static Badge](https://img.shields.io/badge/License-LGPL--3.0--or--later-forestgreen)
  - Licensed under the [GNU Lesser General Public License version 3.0](LICENSE.md) or (at your option) any later version.
- **Textures, Models, and Written Content**
  - Copyright © 2024-2025 Eduardo Jimenez
  - ![Static Badge](https://img.shields.io/badge/License-CC--BY--NC--SA_4.0-purple)
  - Licensed under [Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International](https://creativecommons.org/licenses/by-nc-sa/4.0/deed.en)

## FAQ

### Development
LimaTech is developed only on NeoForge for the current long term support Minecraft version (1.21.1) until further notice.
Porting to other versions or loaders will not be considered.

### Compatibility

**Note on Sodium**: If you are using Sodium you must disable the 'Animate Only Visible Textures' option or
some textures will not render properly.

The following have not been tested and are not officially supported. Please **do not** report any issues
related to these, as they will not be fixed or investigated.

- **OptiFine or any mod that modifies the rendering engine to use non-standard methods.**
- **Shaders**
  - Some shader packs have slight visual glitches.
  - The Bubble Shield causes issues with translucent blocks. Not game-breaking, but might affect immersion.
- **Recipe Advancement Tweak Mods**
  - The Fabricator relies on the recipe advancement system for its gating mechanic.
  - Disabling or modifying the recipe advancements will break progression.
- **Base Game Mechanic Modifiers**
  - Item mechanic modifiers (Epic Fight, etc.)
  - Non-standard animation packs/mods
  - Player model/movement modifications