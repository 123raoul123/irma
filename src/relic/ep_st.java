package relic;
import java.util.Arrays;
import java.util.List;
import com.sun.jna.Structure;

/*
 typedef uint64_t dig_t;
 typedef align dig_t fp_st[FP_DIGS + PADDING(FP_BYTES)/(FP_DIGIT / 8)];
 
 typedef struct {
	fp_st x;
	fp_st y;
	fp_st z;
	int norm;
} ep_st;

typedef ep_st ep_t[1];
 
 */

public class ep_st extends Structure{
	public static class ByReference extends ep_st implements Structure.ByReference {}
	
	public long[] x = new long[10];
	public long[] y = new long[10];
	public long[] z = new long[10];
	public int norm;
	public ep_st() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("x", "y", "z", "norm");
	}

	public ep_st(long x[], long y[], long z[], int norm) {
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