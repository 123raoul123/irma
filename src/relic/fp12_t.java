package relic;

import com.sun.jna.Memory;

/**
 * Element in G_T
 */
public class fp12_t extends Memory {
	public fp12_t() {
		super(Relic.SIZES.fp12_size);
	}
}
