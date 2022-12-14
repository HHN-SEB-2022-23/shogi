package de.hhn.shogi.basicgamemanager;

public abstract class TypeSerializer {
    protected abstract String serialize(Object object);

    protected abstract Object deserialize(String serialized);

    @SuppressWarnings("rawtypes")
    protected abstract Class getType();
}
