---
navigation:
    title: "Fabrication"
    icon: fabricator
    parent: machines/index.md
---

# Tech Fabrication

Most of the upgrades and gear are too complex to be made in general processing machines or a crafting table.
For that, you'll need to make a Fabricator. Fabricators take up to 16 ingredients and use energy to assemble the final
product.

## Fabricator
<BlockImage id="fabricator" scale="2" />

Also referred to as the manual Fabricator, this is the first fabrication machine you'll make. You may view the available
recipes in the recipe view grid. To select a recipe, left click once. Once selected, left click again to craft or right
click to encode the recipe into a blank *Fabrication Blueprint*. This Fabricator has no input slots, it will take the
ingredients directly from your inventory for hassle-free crafting.

### Fabrication Blueprint
<ItemGrid><ItemIcon id="fabrication_blueprint" /></ItemGrid>
This item holds a Fabrication recipe for use in the Auto Fabricator. Hold Shift when viewing the tooltip to view the
recipe's information. Shift + Right click to clear the current recipe, if any.

## Auto Fabricator
<BlockImage id="auto_fabricator" scale="2" />

A more specialized version of the Fabricator. It makes up for its lack of versatility in ease of automation. Unlike the
regular fabricator, it has 16 ingredient input slots as it does not pull from your inventory. You must place a
*Fabrication Blueprint* in the blueprint slot and provide the machine with the required ingredients. As long as it has
ingredients, it will continuously craft the provided recipe.

### AE2 Tip

You can automate this machine with just the Pattern Provider. Set the block face where the pattern provider will go
to Input/Output (dark green) and enable Auto-Output. Make sure to **enable blocking mode** on the Pattern Provider to avoid stalling.