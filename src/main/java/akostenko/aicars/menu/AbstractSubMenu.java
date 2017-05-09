package akostenko.aicars.menu;

import java.util.Collections;
import java.util.List;

public abstract class AbstractSubMenu<ITEM extends MenuItem> implements SubMenu<ITEM> {

    protected abstract List<ITEM> items();

    private int current = 0;

    @Override
    public void change(int delta) {
        if (items().size() > 0) {
            current += delta + items().size();
            current %= items().size();
        }
    }

    @Override
    public List<ITEM> getItems() {
        return Collections.unmodifiableList(items());
    }

    @Override
    public ITEM getCurrent() {
        return items().get(current);
    }

    @Override
    public void setCurrent(ITEM item) {
        current = items().indexOf(item);
        if (current < 0) current = 0;
    }

    @Override
    public boolean isCurrent(MenuItem item) {
        return items().indexOf(item) == current;
    }
}
