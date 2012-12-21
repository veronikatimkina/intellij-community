package org.hanuna.gitalk.graph.graph_elements;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author erokhins
 */
public interface NodeRow {

    @NotNull
    public List<Node> getVisibleNodes();

    public int getRowIndex();
}
