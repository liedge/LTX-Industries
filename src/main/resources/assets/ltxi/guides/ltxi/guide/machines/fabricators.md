---
navigation:
    title: "Fabrication"
    icon: fabricator
    parent: machines/index.md
    position: 30
item_ids:
    - fabricator
    - auto_fabricator
    - empty_fabrication_blueprint
    - fabrication_blueprint
---

# Tech Fabrication

Most of the upgrades and gear are too complex to be made in general processing machines or a crafting table.
For that, you'll need to make a Fabricator. Fabricating recipes are unique amongst all the LTXI recipe types, consult
the recipe data spec below.

## Recipe Spec
- Inputs: 1-16 items
- Output: 1 item
- Energy cost: varies per recipe, **used in place of crafting time**

Due to the lack of crafting time, the Fabricator speed is a much simpler function of the recipe energy cost and the
**Energy Usage** machine stat. You may consider it a positive-sentiment stat for this machine.

## Fabricator
<GameScene zoom={3} interactive={false}>
<Block id="fabricator" y="-1" x="0" />
<Block id="mesh_block" y="-1" x="-1" />
<Block id="mesh_block" y="0" x="0" />
<Block id="mesh_block" y="0" x="-1" />
<BlockAnnotation x="0" y="-1" z="0" color="#8bd1f0">
Items and Energy can only be connected to the front, left, rear, and bottom of this block!
</BlockAnnotation>
</GameScene>

Also referred to as the manual Fabricator, this is the first fabrication machine you'll make. You may view the available
recipes in the recipe view grid. To select a recipe, left click once. Once selected, left click again to craft or right
click to encode the recipe into an **Empty Fabrication Blueprint**. This Fabricator has no input slots, it will take the
ingredients directly from your inventory for hassle-free crafting.

## Fabrication Blueprint
<ItemGrid>
<ItemIcon id="empty_fabrication_blueprint" />
<ItemIcon id="fabrication_blueprint" />
</ItemGrid>

Encoded **Fabrication Blueprints** hold a Fabrication recipe for use in the Auto Fabricator or AE2 Pattern Providers.
Hold Shift when viewing the tooltip to display the recipe's information. Shift + Right click to clear the encoded recipe.

## Auto Fabricator
<BlockImage id="auto_fabricator" scale="2" />

A more specialized version of the Fabricator. It makes up for its lack of versatility in ease of automation. Unlike the
regular fabricator, it has 16 ingredient input slots as it does not pull from your inventory.

### Single blueprint-based operation
You must place an encoded Fabrication Blueprint in the blueprint slot and supply the machine with the required ingredients
via your desired item transport method. As long as it has enough ingredients and energy, it will continuously craft the
provided recipe.

### AE2 Pattern Provider integration
AE2's **Pattern Providers** have native compatibility with the Auto Fabricator and encoded Fabrication Blueprints. **Note!
The blueprint slot must be empty, or else the Pattern Provider will not recognize the Auto Fabricator as a valid crafting
machine.**

- Blueprints are patterns. Insert these directly into the Pattern Provider's pattern slots, no need for Processing patterns.
- Compound ingredient/tag/substitution support by default.
- Blocking mode is not necessary, item and blueprint feeding is managed automatically.
- Item IO Configuration is re-configured on every pattern push. It will auto-output on the pattern provider side and disable the rest.