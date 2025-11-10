package liedge.ltxindustries.menu.layout;

public final class RecipeLayouts
{
    public static final RecipeLayout COOKING_LAYOUT = RecipeLayout.builder().itemIn(54, 35).itemOut(112, 35).build(79, 40);
    public static final RecipeLayout GRINDING = RecipeLayout.builder()
            .modeSlot(25, 35)
            .itemIn(43, 35).itemOut(101, 35).itemOut(119, 35).itemOut(137, 35)
            .build(68, 41);
    public static final RecipeLayout FUSING = RecipeLayout.builder()
            .modeSlot(25, 36)
            .itemIn(43, 27).itemIn(61, 27).itemIn(79, 27)
            .fluidIn(43, 45)
            .itemOut(137, 36)
            .build(104, 41);
    public static final RecipeLayout ELECTRO_CENTRIFUGING = RecipeLayout.builder()
            .modeSlot(25, 35)
            .itemIn(43, 35).fluidIn(61, 35)
            .itemOut(119, 17).itemOut(137, 17).itemOut(119, 35).itemOut(137, 35)
            .fluidOut(119, 53).fluidOut(137, 53).build(86, 40);
    public static final RecipeLayout MIXING = RecipeLayout.builder()
            .modeSlot(25, 35)
            .itemIn(43, 17).itemIn(61, 17).itemIn(43, 35).itemIn(61, 35)
            .fluidIn(43, 53).fluidIn(61, 53)
            .itemOut(119, 35).fluidOut(137, 35).build(86, 40);
    public static final RecipeLayout ENERGIZING = RecipeLayout.builder()
            .modeSlot(36, 35)
            .itemIn(54, 35).itemOut(112, 35)
            .build(79, 40);
    public static final RecipeLayout CHEMICAL_REACTING = RecipeLayout.builder()
            .modeSlot(16, 35)
            .itemIn(34, 26).itemIn(52, 26).itemIn(70, 26)
            .fluidIn(34, 44).fluidIn(52, 44).fluidIn(70, 44)
            .itemOut(128, 26).itemOut(146, 26)
            .fluidOut(128, 44).fluidOut(146, 44).build(95, 40);
    public static final RecipeLayout ASSEMBLING = RecipeLayout.builder()
            .modeSlot(25, 35)
            .slotGrid(43, 17, 3, 2, LayoutSlot.Type.ITEM_INPUT)
            .fluidIn(43, 53)
            .itemOut(137, 36).build(104, 40);
    public static final RecipeLayout GEO_SYNTHESIS = RecipeLayout.builder()
            .modeSlot(25, 35)
            .fluidIn(43, 35).itemIn(61, 35).fluidIn(79, 35)
            .itemOut(137, 35).build(104, 40);
    public static final RecipeLayout GARDEN_SIMULATING = RecipeLayout.builder()
            .modeSlot(34, 35)
            .itemIn(52, 26).fluidIn(52, 44)
            .itemOut(110, 26).itemOut(128, 26).itemOut(110, 44).itemOut(128, 44)
            .build(77, 40);
}