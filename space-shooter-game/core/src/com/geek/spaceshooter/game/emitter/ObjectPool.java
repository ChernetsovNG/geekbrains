package com.geek.spaceshooter.game.emitter;

import com.geek.spaceshooter.game.objects.Poolable;

import java.util.ArrayList;
import java.util.List;

public abstract class ObjectPool<T extends Poolable> {
    List<T> activeList;
    List<T> freeList;

    public List<T> getActiveList() {
        return activeList;
    }

    public List<T> getFreeList() {
        return freeList;
    }

    protected abstract T newObject();

    private void free(int index) {
        freeList.add(activeList.remove(index));
    }

    ObjectPool() {
        this.activeList = new ArrayList<T>();
        this.freeList = new ArrayList<T>();
    }

    ObjectPool(int size) {
        this.activeList = new ArrayList<T>();
        this.freeList = new ArrayList<T>();
        for (int i = 0; i < size; i++) {
            freeList.add(newObject());
        }
    }

    T getActiveElement() {
        if (freeList.size() == 0) {
            freeList.add(newObject());
        }
        T temp = freeList.remove(freeList.size() - 1);
        activeList.add(temp);
        return temp;
    }

    public void checkPool() {
        for (int i = activeList.size() - 1; i >= 0; i--) {
            if (!activeList.get(i).isActive()) {
                free(i);
            }
        }
    }
}
