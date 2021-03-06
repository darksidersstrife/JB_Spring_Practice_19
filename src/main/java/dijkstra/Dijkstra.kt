package dijkstra

import java.util.*
import java.util.concurrent.Phaser
import kotlin.Comparator
import kotlin.concurrent.thread

val NODE_DISTANCE_COMPARATOR = Comparator<Node> { o1, o2 -> Integer.compare(o1!!.distance, o2!!.distance) }

// Returns `Integer.MAX_VALUE` if a path has not been found.
fun shortestPathSequential(start: Node, destination: Node): Int {
    start.distance = 0
    val q = PriorityQueue<Node>(NODE_DISTANCE_COMPARATOR)
    q.add(start)
    while (q.isNotEmpty()) {
        val cur = q.poll()
        for (e in cur.outgoingEdges) {
            if (e.to.distance > cur.distance + e.weight) {
                e.to.distance = cur.distance + e.weight
                q.remove(e.to) // inefficient, but used for tests only
                q.add(e.to)
            }
        }
    }
    return destination.distance
}

// Returns `Integer.MAX_VALUE` if a path has not been found.
fun shortestPathParallel(start: Node, destination: Node): Int {
    val workers = Runtime.getRuntime().availableProcessors()
    // The distance to the start node is `0`
    start.distance = 0
    // Create a priority (by distance) queue and add the start node into it
    val q = PriorityQueue(workers, NODE_DISTANCE_COMPARATOR)
    q.add(start)
    // Run worker threads and wait until the total work is done
    val onFinish = Phaser(workers + 1) // `arrive()` should be invoked at the end by each worker
    repeat(workers) {
        thread {
            while (true) {
                // TODO Write the required algorithm here,
                // TODO break from this loop when there is no more node to process.
                // TODO Be careful, "empty queue" != "all nodes are processed".
//                val cur: Node? = synchronized(q) { q.poll() }
//                if (cur == null) {
//                    if (workIsDone) break else continue
//                }
//                for (e in cur.outgoingEdges) {
//                    if (e.to.distance > cur.distance + e.weight) {
//                        e.to.distance = cur.distance + e.weight
//                        q.addOrDecreaseKey(e.to)
//                    }
//                }
            }
            onFinish.arrive()
        }
    }
    onFinish.arriveAndAwaitAdvance()
    // Return the result
    return destination.distance
}