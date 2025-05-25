package liedge.limatech.client.gui.screen;

public enum RecipeScreenType
{
    DIGITAL_FURNACE(75, 39, 24, 6),
    GRINDER(75, 39, 24, 6),
    RECOMPOSER(75, 39, 24, 6);

    private final int recipeAreaX;
    private final int recipeAreaY;
    private final int recipeAreaWidth;
    private final int recipeAreaHeight;

    RecipeScreenType(int recipeAreaX, int recipeAreaY, int recipeAreaWidth, int recipeAreaHeight)
    {
        this.recipeAreaX = recipeAreaX;
        this.recipeAreaY = recipeAreaY;
        this.recipeAreaWidth = recipeAreaWidth;
        this.recipeAreaHeight = recipeAreaHeight;
    }

    public int getRecipeAreaX()
    {
        return recipeAreaX;
    }

    public int getRecipeAreaY()
    {
        return recipeAreaY;
    }

    public int getRecipeAreaWidth()
    {
        return recipeAreaWidth;
    }

    public int getRecipeAreaHeight()
    {
        return recipeAreaHeight;
    }
}