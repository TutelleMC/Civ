package tutellemc.civsim.services;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import tutellemc.civsim.models.Mineshaft;

@Getter
public class NodeService {
    private final Set<Mineshaft> nodes = new HashSet<>();

    public void registerNode(final Mineshaft node) {
        nodes.add(node);
    }
}
