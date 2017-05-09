package akostenko.aicars.menu;

import java.util.List;

public interface SubMenu<ITEM extends MenuItem> {
    String getTitle();

    void change(int delta);

    void enter();

    List<ITEM> getItems();

    ITEM getCurrent();

    void setCurrent(ITEM item);

    boolean isCurrent(MenuItem item);
}
