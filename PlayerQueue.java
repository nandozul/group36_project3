import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class implements a singly linked FIFO queue of players waiting to challenge the champion.
 * It keeps both a head and a tail pointer so that enqueue and dequeue are both O(1). 
 * The queue holds only waiting players; the current champion is stored separately by the Tournament class.
 *
 * @author Tanvi Bhat
 * @version 2026.11.06
 */
public class PlayerQueue {

    /**
     * This nested class holds a waiting player and a reference to the player behind them in line.
     */
    private static class Node {
        private Player player;
        private Node next;

        /**
         * A node is created holding the given player with no node behind it.
         *
         * @param player the player waiting to go next.
         */
        Node(Player player) {
            this.player = player;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    /**
     * This method creates an empty queue with no players waiting.
     */
    public PlayerQueue() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * This method adds a player at the back of the line in O(1).
     *
     * @param p the player to add
     * @throws IllegalArgumentException if p is null.
     */
    public void enqueue(Player p) {
        if (p == null) {
            throw new IllegalArgumentException("Cannot enqueue a null player");
        }

        Node newNode = new Node(p);
        if (head == null) {
            head = newNode;
        }
        else {
            tail.next = newNode;
        }
        tail = newNode;
        size++;
    }

    /**
     * This method removes and returns the player at the front of the line in O(1).
     *
     * @return the player who was at the front.
     * @throws NoSuchElementException if the queue is empty.
     */
    public Player dequeue() {
        if (head == null) {
            throw new NoSuchElementException("the queue is empty");
        }

        Player front = head.player;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return front;
    }

    /**
     * This method returns the player at the front of the line without removing them.
     *
     * @return the player at the front
     * @throws NoSuchElementException if the queue is empty
     */
    public Player peek() {
        if (head == null) {
            throw new NoSuchElementException("Queue is empty");
        }
        return head.player;
    }

    /**
     * This method returns whether the queue is empty.
     *
     * @return true if no players are waiting
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * This method returns the number of waiting players to go next.
     *
     * @return the size of the queue
     */
    public int size() {
        return size;
    }

    /**
     * This method returns the waiting players' names from front to back in a new list. 
     * The new list is a copy, so changing it won't change the queue.
     *
     * @return the names in queue order
     */
    public List<String> toNameList() {
        List<String> names = new ArrayList<String>();
        Node current = head;
        while (current != null) {
            names.add(current.player.getName());
            current = current.next;
        }
        return names;
    }
}
