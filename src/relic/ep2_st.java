package relic;
import java.util.Arrays;
import java.util.List;
import com.sun.jna.Structure;

/*
 	typedef uint64_t dig_t;
	typedef align dig_t fp_t[FP_DIGS + PADDING(FP_BYTES)/(FP_DIGIT / 8)];
 	typedef fp_t fp2_t[2];
 
    typedef struct {
		fp2_t x;
		fp2_t y;
		fp2_t z;
		int norm;
	} ep2_st;
 	
 	typedef ep2_st ep2_t[1];
 */

/**
 * Represents an elliptic curve point over a quadratic extension over a prime
 * field.
 */
public class ep2_st extends Structure{
	public static class ByReference extends ep2_st implements Structure.ByReference {}
	
	public long[] x = new long[2*10];
	public long[] y = new long[2*10];
	public long[] z = new long[2*10];
	public int norm;
	public ep2_st() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("x", "y", "z", "norm");
	}
	public ep2_st(long x[], long y[], long z[], int norm) {
		super();
		if ((x.length != this.x.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.x = x;
		if ((y.length != this.y.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.y = y;
		if ((z.length != this.z.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.z = z;
		this.norm = norm;
	}

}