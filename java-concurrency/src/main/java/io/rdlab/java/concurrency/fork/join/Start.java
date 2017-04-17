package io.rdlab.java.concurrency.fork.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public final class Start {
    private Start() {
    }

    static class NodeLong {
        Collection<NodeLong> children;
        Long value;

        NodeLong(Long value, Collection<NodeLong> children) {
            this.value = value;
            this.children = children;
        }

        Collection<NodeLong> getChildren() {
            return children;
        }

        Long getValue() {
            return value;
        }
    }

    static class NodeLongSumTask extends RecursiveTask<Long> {
        NodeLong node;

        NodeLongSumTask(NodeLong node) {
            this.node = node;
        }

        @Override
        protected Long compute() {
            final Long[] result = {node.getValue()};
            List<NodeLongSumTask> tasks = new ArrayList<>();
            node.getChildren().stream().forEach((n) -> {
                final NodeLongSumTask task = new NodeLongSumTask(n);
                task.fork();
                tasks.add(task);
            });
            tasks.stream().forEach(t -> result[0] += t.join());
            return result[0];
        }
    }

    public static void start() {
        final NodeLong nodeLong1 = new NodeLong(1L, Collections.emptyList());
        final NodeLong nodeLong2 = new NodeLong(2L, Collections.emptyList());
        final NodeLong nodeLong3 = new NodeLong(3L, Collections.emptyList());
        final NodeLong nodeLong4 = new NodeLong(4L, Collections.emptyList());
        final Collection nodeLongs = new ArrayList<>();
        nodeLongs.add(nodeLong1);
        nodeLongs.add(nodeLong2);
        nodeLongs.add(nodeLong3);
        nodeLongs.add(nodeLong4);
        final NodeLong nodeLong = new NodeLong(0L, nodeLongs);
        System.out.println(new ForkJoinPool().invoke(new NodeLongSumTask(nodeLong)));
    }
}
