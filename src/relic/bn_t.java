package relic;

import com.sun.jna.Memory;

public class bn_t extends Memory {
	public bn_t() {
		super(Relic.SIZES.bn_st_size);
	}
}