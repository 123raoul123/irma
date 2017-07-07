package relic;

import com.sun.jna.Memory;

/**
 * Element in G_2
 */
public class ep2_t extends Memory {
	public ep2_t() {
		super(Relic.SIZES.ep2_st_size);
	}
}
