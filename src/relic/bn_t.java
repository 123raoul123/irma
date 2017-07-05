package relic;

import com.sun.jna.Memory;
import java.util.Base64;

public class bn_t extends Memory {
	public bn_t() {
		super(Relic.SIZES.bn_st_size);
	}

	@Override public String toString() {
		byte[] bytes = new byte[Relic.SIZES.bn_st_size];
		Relic.INSTANCE.bn_write_bin(bytes, Relic.SIZES.ep_st_size, this);
		return Base64.getEncoder().encodeToString(bytes);
	}
}