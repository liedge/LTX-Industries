package liedge.ltxindustries.menu.layout;

public final class RecipeLayouts
{
    public static final RecipeLayout COOKING_LAYOUT = RecipeLayout.builder().itemIn(54, 35).itemOut(112, 35).build(79, 40);
    public static final RecipeLayout GRINDER = RecipeLayout.builder().itemIn(43, 35).itemOut(101, 35).itemOut(119, 35).itemOut(137, 35).build(68, 41);
    public static final RecipeLayout MATERIAL_FUSING_CHAMBER = RecipeLayout.builder()
            .itemIn(43, 27).itemIn(61, 27).itemIn(79, 27)
            .fluidIn(43, 45)
            .itemOut(137, 36)
            .build(104, 41);
    public static final RecipeLayout ELECTROCENTRIFUGE = RecipeLayout.builder()
            .itemIn(43, 35).fluidIn(61, 35)
            .itemOut(119, 17).itemOut(137, 17).itemOut(119, 35).itemOut(137, 35)
            .fluidOut(119, 53).fluidOut(137, 53).build(86, 40);
    public static final RecipeLayout MIXER = RecipeLayout.builder()
            .itemIn(43, 17).itemIn(61, 17).itemIn(43, 35).itemIn(61, 35)
            .fluidIn(43, 53).fluidIn(61, 53)
            .itemOut(119, 35).fluidOut(137, 35).build(86, 40);
    public static final RecipeLayout CHEM_LAB = RecipeLayout.builder()
            .itemIn(34, 26).itemIn(52, 26).itemIn(70, 26)
            .fluidIn(34, 44).fluidIn(52, 44).fluidIn(70, 44)
            .itemOut(128, 26).itemOut(146, 26)
            .fluidOut(128, 44).fluidOut(146, 44).build(95, 40);
    public static final RecipeLayout DIGITAL_GARDEN = RecipeLayout.builder()
            .itemIn(52, 26).fluidIn(52, 44)
            .itemOut(110, 26).itemOut(128, 26).itemOut(110, 44).itemOut(128, 44)
            .build(77, 40);
}