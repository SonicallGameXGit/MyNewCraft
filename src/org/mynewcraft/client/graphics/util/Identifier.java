package org.mynewcraft.client.graphics.util;

import java.util.Objects;

public class Identifier {
    private final String NAMESPACE;
    private final String ID;

    public Identifier(String namespace, String id) {
        NAMESPACE = namespace;
        ID = id;
    }

    public String getNamespace() {
        return NAMESPACE;
    }
    public String getId() {
        return ID;
    }

    @Override
    public String toString() {
        return "namespace: " + NAMESPACE + ", " + "id: " + ID;
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) || (Objects.equals(NAMESPACE, ((Identifier) other).NAMESPACE) && Objects.equals(ID, ((Identifier) other).ID));
    }
}