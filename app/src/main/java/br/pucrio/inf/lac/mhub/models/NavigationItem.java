package br.pucrio.inf.lac.mhub.models;

/**
 * Created by luis on 25/02/15.
 * Data structure for the navigation menu
 */
public class NavigationItem {
    /** Name of the item */
    private String name;

    /** Id for the icon */
    private int iconId;

    public NavigationItem(String name, int iconId) {
        this.name = name;
        this.iconId = iconId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconId() {
        return iconId;
    }
}
