package timedelayqueue;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeDelayQueue stores items that have a unique identifier
 * and timestamp. Items inside TimeDelayQueue are returned in
 * an order that is determined by individual timestamps and a
 * delay parameter.
 */

public class TimeDelayQueue {

    private final int delay;
    private final ArrayList<PubSubMessage> unSyncedQueue;
    private final List<PubSubMessage> queue;

    /*
    Representation Invariant:
        The 'delay' parameter must always be non-negative.
        The 'count' must always be non-negative and less than
        or equal to the size of queue.
        The 'opTimes' must always contain only non-negative integers.

    Abstraction Function:
        abstraction function: AF(c) = (I, T, D, C, O) where
      I is the set of items in the TimeDelayQueue
      T is the set of timestamps associated with each item in I
      D is the delay parameter of the TimeDelayQueue
      C is the total number of messages processed by the TimeDelayQueue
      O is the set of operation times for the TimeDelayQueue

      Thread Safety Argument:
        The TimeDelayQueue class is thread-safe because all of its
        mutable state is accessed and modified only through synchronized
        methods, which ensures that only one thread can modify the state
        of the TimeDelayQueue at a time.
        The AtomicInteger datatype used for the count field ensures that
        the getTotalMsgCount() method is also thread-safe, even if it is
        not explicitly synchronized.
     */

    /**
     * AtomicInteger datatype keeps count variable
     * threadsafe and methods have been synchronized to further
     * prevent thread interference
     */
    private final AtomicInteger count;
    private final ArrayList<Integer> opTimes;

    /**
     * Create a new TimeDelayQueue
     *
     * @param delay the delay, in milliseconds, that the queue can tolerate, >= 0
     */
    public TimeDelayQueue(int delay) {
        this.delay = delay;
        this.unSyncedQueue = new ArrayList<>();
        this.queue = Collections.synchronizedList(unSyncedQueue);
        this.count = new AtomicInteger(0);
        this.opTimes = new ArrayList<>();

    }

    /**
     *Add a message to the TimeDelayQueue
     * @param msg
     * @return false if a message with same Id as msg exists,
     * otherwise return true.
     */
    public synchronized boolean add(PubSubMessage msg) {

        if (msg == null) {
            return false;
        }

        //log time of operation into list
        this.opTimes.add((int) System.currentTimeMillis());

        //check for duplicate
        for (PubSubMessage message : this.queue) {
            if (message.getId() == msg.getId()) {
                return false;
            }
        }

        queue.add(msg);
        this.count.incrementAndGet();
        return true;
    }

    /**
     * Get the count of the total number of messages processed
     * by this TimeDelayQueue
     *
     * @return total number of messages processed
     */
    public synchronized long getTotalMsgCount() {
        return this.count.longValue();
    }

    /**
     * Gets the next message in TimeDelayQueue
     * @return the next message and PubSubMessage.NO_MSG
     * if there is no suitable message
     */
    public synchronized PubSubMessage getNext() {
        int earliestMsgIndex = 0;
        Timestamp callTime = new Timestamp(System.currentTimeMillis());
        Timestamp callTimeMinusDelay = new Timestamp(System.currentTimeMillis() - this.delay);
        PubSubMessageComparator cmp = new PubSubMessageComparator();

        //log time of operation into list
        this.opTimes.add((int) System.currentTimeMillis());

        //return no msg if queue empty
        if (this.queue.size() == 0) {
            return PubSubMessage.NO_MSG;
        }

        synchronized (this.queue) {
            Iterator<PubSubMessage> iterator = this.queue.iterator();
            while (iterator.hasNext()) {
                PubSubMessage message = iterator.next();
                if (message instanceof TransientPubSubMessage) {
                    long msgTime = message.getTimestamp().getTime();
                    if (callTime.getTime() - msgTime > ((TransientPubSubMessage) message).getLifetime()) {
                        iterator.remove();
                    }
                }
            }
        }

        //find earliest timestamped message
        for (int i = 1; i < this.queue.size(); i++) {
            if (cmp.compare(this.queue.get(i), this.queue.get(earliestMsgIndex)) > 0) {
                earliestMsgIndex = i;
            }
        }


        if (this.queue.size() == 0) {
            return PubSubMessage.NO_MSG;
        } else if (callTimeMinusDelay.compareTo(this.queue.get(earliestMsgIndex).getTimestamp()) > 0) {
            //return only if currentTime - message's timestamp >= DELAY
            return this.queue.remove(earliestMsgIndex);
        }

        return PubSubMessage.NO_MSG;
    }

    /**
     *
     * @param timeWindow
     * @return the maximum number of operations performed
     * on this TimeDelayQueue over any window of length
     * timeWindow the operations of interest are add
     * and getNext
     */
    public synchronized int getPeakLoad(int timeWindow) {

        int max = 0;

        //go through each operation in the list
        for (int i = 0; i < this.opTimes.size(); i++) {
            int temp = 0;

            //check how many are in its time range
            for (int j = i; j < this.opTimes.size(); j++) {
                if (this.opTimes.get(j) <= this.opTimes.get(i) + timeWindow) {
                    temp++;
                }
            }

            if (temp > max) {
                max = temp;
            }
        }
        return max;
    }

    /**
     * a comparator to sort messages
     */
    private class PubSubMessageComparator implements Comparator<PubSubMessage> {
        public int compare(PubSubMessage msg1, PubSubMessage msg2) {
            return msg1.getTimestamp().compareTo(msg2.getTimestamp());
        }
    }
}