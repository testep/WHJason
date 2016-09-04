
// This class has been created by the following authors
// I take no credit for any code in this page
// Rezart Tabaku

package mvc;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  The IndexMinPQ class represents an indexed priority queue of generic keys.
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *
 */
public class MinPQ<Key extends Comparable<Key>> implements Iterable<Integer> {
    private int maxN;        // maximum number of elements on PQ
    private int N;           // number of elements on PQ
    private Node[] pq;        // binary heap using 1-based indexing
    
    /**
     * Initializes an empty indexed priority queue with indices between <tt>0</tt>
     * and <tt>maxN - 1</tt>.
     * @param  maxN the keys on this priority queue are index from <tt>0</tt>
     *         <tt>maxN - 1</tt>
     * @throws IllegalArgumentException if <tt>maxN</tt> &lt; <tt>0</tt>
     */
    public MinPQ(int maxN) {
        if (maxN < 0) throw new IllegalArgumentException();
        this.maxN = maxN;
        N = 0;
        pq = new Node[maxN+1];
    }

    /**
     * Returns true if this priority queue is empty.
     *
     * @return <tt>true</tt> if this priority queue is empty;
     *         <tt>false</tt> otherwise
     */
    public boolean isEmpty() {
        return N == 0;
    }

    /**
     * Returns the number of keys on this priority queue.
     *
     * @return the number of keys on this priority queue
     */
    public int size() {
        return N;
    }

    /**
     * Associates key with index <tt>i</tt>.
     *
     */
    public void insert(Node i) {
        N++;
        pq[N] = i;
        swim(N);
    }

    /**
     * Returns an index associated with a minimum key.
     *
     */
    public Node minIndex() {
        if (N == 0) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
    }


    /**
     * Removes a minimum key and returns its associated index.
     * @return an index associated with a minimum key
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Node getMin() {
        if (N == 0)return null;
        Node min = pq[1];
        exch(1, N--);
        sink(1);
        assert min == pq[N+1];
        return min;
    }


   /***************************************************************************
    * General helper functions.
    ***************************************************************************/
    private boolean greater(int i, int j) {
        return pq[i].compareTo(pq[j]) > 0;
    }

    private void exch(int i, int j) {
        Node swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
    }

    public void print(){
    	for (int i = 1; i <= N; i++) {
			System.out.print(" -> " + pq[i]);
		}
    }
   /***************************************************************************
    * Heap helper functions.
    ***************************************************************************/
    private void swim(int k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= N) {
            int j = 2*k;
            if (j < N && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }

	@Override
	public Iterator<Integer> iterator() {
		
		return null;
	}

	public void clear() {
		while(!isEmpty()){
			getMin();
		}
	}


    
}
