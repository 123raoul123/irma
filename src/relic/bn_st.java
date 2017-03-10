package relic;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/*
#define RELIC_BN_BITS 	((int)BN_PRECI)
#define BN_DIGIT	((int)DIGIT
#define BN_DIGS		((int)((RELIC_BN_BITS)/(BN_DIGIT) + (RELIC_BN_BITS % BN_DIGIT > 0)))
#define BN_SIZE		((int)(2 * BN_DIGS + 2))
typedef uint64_t dig_t;

typedef struct {
	int alloc;
	int used;
	int sign;
	align dig_t dp[BN_SIZE];
#endif
} bn_st;
typedef bn_st bn_t[1];
*/

/**
 * Represents a multiple precision integer.
 *
 * The field dp points to a vector of digits. These digits are organized
 * in little-endian format, that is, the least significant digits are
 * stored in the first positions of the vector.
 */
public class bn_st extends Structure {
	public static class ByReference extends bn_st implements Structure.ByReference {}
	/** The number of digits allocated to this multiple precision integer. */
	public int alloc;
	/** The number of digits actually used. */
	public int used;
	/** The sign of this multiple precision integer. */
	public int sign;
	/** The sequence of contiguous digits that forms this integer.*/
	public long[] dp = new long[12];

	public bn_st() {
		super();
	}

	protected List<String> getFieldOrder() {
		return Arrays.asList("alloc", "used", "sign", "dp");
	}
	/**
	 * @param alloc The number of digits allocated to this multiple precision integer.
	 * @param used The number of digits actually used.
	 * @param sign The sign of this multiple precision integer.
	 * @param dp The sequence of contiguous digits that forms this integer.*/
	public bn_st(int alloc, int used, int sign, long[] dp) {
		super();
		this.alloc = alloc;
		this.used = used;
		this.sign = sign;
		if ((dp.length != this.dp.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.dp = dp;
	}
}