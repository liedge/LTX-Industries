package liedge.ltxindustries.menu.layout;

public final class RecipeLayouts
{
    public static final RecipeMenuLayout COOKING_LAYOUT = RecipeMenuLayout.builder().itemIn(54, 35).itemOut(112, 35).build(79, 40);
    public static final RecipeMenuLayout GRINDER = RecipeMenuLayout.builder().itemIn(43, 35).itemOut(101, 35).itemOut(119, 35).itemOut(137, 35).build(68, 41);

    public static final RecipeMenuLayout MATERIAL_FUSING_CHAMBER = RecipeMenuLayout.builder()
            .itemIn(43, 27).itemIn(61, 27).itemIn(79, 27)
            .fluidIn(43, 45)
            .itemOut(137, 36)
            .build(104, 41);
}