import java.util.List;
import java.util.NoSuchElementException;
import student.TestCase;

/**
 * This class tests the PlayerQueue,including the enqueue, dequeue, peek, isEmpty,
 * size, and toNameList, and bad input cases.
 *
 * @author Tanvi Bhat
 * @version 2026.11.06
 */
public class PlayerQueueTest extends TestCase {

    private PlayerQueue queue;
    private Player playerA;
    private Player playerB;
    private Player playerC;

    /**
     * This method creates a empty queue and three players.
     */
    public void setUp() {
        queue = new PlayerQueue();
        playerA = new Player("Hawkeye");
        playerB = new Player("Black Widow");
        playerC = new Player("Iron Man");
    }


    /**
     * Tests if enqueueing two players puts them in insertion order and sets size to 2.
     */
    public void testEnqueueNormal() {
        queue.enqueue(playerA);
        queue.enqueue(playerB);

        List<String> names = queue.toNameList();
        assertEquals(2, queue.size());
        assertEquals("Hawkeye", names.get(0));
        assertEquals("Black Widow", names.get(1));
    }

    /**
     * Tests if enqueueing into an empty queue sets both the head and tail so that the single
     * player is both the front and the back.
     */
    public void testEnqueueIntoEmptyQueue() {
        queue.enqueue(playerA);

        assertEquals(1, queue.size());
        assertEquals(playerA, queue.peek());
    }

    /**
     * Tests if enqueueing null throws IllegalArgumentException.
     */
    public void testEnqueueNull() {
        Exception thrown = null;
        try {
            queue.enqueue(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            thrown = e;
        }

        assertNotNull(thrown);
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }


    /**
     * Tests if dequeueing returns the player at the front and shrinks the queue.
     */
    public void testDequeueNormal() {
        queue.enqueue(playerA);
        queue.enqueue(playerB);

        assertEquals(playerA, queue.dequeue());
        assertEquals(1, queue.size());
        assertEquals(playerB, queue.peek());
    }

    /**
     * Tests if dequeueing every player empties the queue in front-to-back order.
     */
    public void testDequeueUntilEmpty() {
        queue.enqueue(playerA);
        queue.enqueue(playerB);
        queue.enqueue(playerC);

        assertEquals(playerA, queue.dequeue());
        assertEquals(playerB, queue.dequeue());
        assertEquals(playerC, queue.dequeue());
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    /**
     * Tests if dequeueing an empty queue throws NoSuchElementException.
     */
    public void testDequeueEmpty() {
        Exception thrown = null;
        try {
            queue.dequeue();
            fail("Expected NoSuchElementException");
        }
        catch (NoSuchElementException e) {
            thrown = e;
        }

        assertNotNull(thrown);
    }


    /**
     * Tests if peek returns the front player without removing them.
     */
    public void testPeekNormal() {
        queue.enqueue(playerA);

        assertEquals(playerA, queue.peek());
        assertEquals(1, queue.size());
        assertEquals(playerA, queue.peek());
    }

    /**
     * Tests if peek reports the new front after a dequeue.
     */
    public void testPeekAfterDequeue() {
        queue.enqueue(playerA);
        queue.enqueue(playerB);
        queue.dequeue();

        assertEquals(playerB, queue.peek());
    }

    /**
     * Tests if peek on an empty queue throws NoSuchElementException.
     */
    public void testPeekEmpty() {
        Exception thrown = null;
        try {
            queue.peek();
            fail("Expected NoSuchElementException");
        }
        catch (NoSuchElementException e) {
            thrown = e;
        }

        assertNotNull(thrown);
    }

    /**
     * Tests a new queue is empty with size 0 and if it stops being empty after one enqueue.
     */
    public void testIsEmptyNewQueue() {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());

        queue.enqueue(playerA);
        assertFalse(queue.isEmpty());
    }

    /**
     *  Tests if emptying the queue and refilling it works. 
     */
    public void testEnqueueAfterEmptying() {
        queue.enqueue(playerA);
        queue.dequeue();
        assertTrue(queue.isEmpty());

        queue.enqueue(playerB);
        queue.enqueue(playerC);

        assertEquals(2, queue.size());
        assertEquals(playerB, queue.peek());
        assertEquals("Iron Man", queue.toNameList().get(1));
    }


    /**
     * Tests toNameList and returns the names from front to back.
     */
    public void testToNameListOrder() {
        queue.enqueue(playerA);
        queue.enqueue(playerB);
        queue.enqueue(playerC);

        List<String> names = queue.toNameList();
        assertEquals(3, names.size());
        assertEquals("Hawkeye", names.get(0));
        assertEquals("Black Widow", names.get(1));
        assertEquals("Iron Man", names.get(2));
    }

    /**
     * Tests toNameList on an empty queue and returns an empty list.
     */
    public void testToNameListEmpty() {
        List<String> names = queue.toNameList();

        assertNotNull(names);
        assertTrue(names.isEmpty());
    }

    /**
     * Tests the copied list. 
     */
    public void testToNameListIsACopy() {
        queue.enqueue(playerA);

        List<String> names = queue.toNameList();
        names.clear();

        assertEquals(1, queue.size());
        assertEquals("Hawkeye", queue.toNameList().get(0));
    }
}