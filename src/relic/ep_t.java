package relic;

import com.sun.jna.Memory;

public class ep_t extends Memory {
	public ep_t() {
		super(Relic.sizes.ep_st_size);
	}
}
