package relic;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class RelicSizes extends Structure implements Structure.ByValue {
	public int bn_size;
	public int bn_digs;
	public int fp_prime;
	public int fp_digit;
	public int fp_digs;
	public int digit;
	public int integer;
	public int bn_st_size;
	public int fp12_size;
	public int ep_st_size;
	public int ep2_st_size;

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("bn_size", "bn_digs", "fp_prime", "fp_digit", "fp_digs", "digit", "integer", "bn_st_size", "fp12_size", "ep_st_size", "ep2_st_size");
	}
}
