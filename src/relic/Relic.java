package relic;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface Relic extends Library {
	public static final Relic INSTANCE
			= (Relic) Native.loadLibrary("/Users/raoul/Documents/workspace/irma/bin/librelic.dylib",Relic.class);
	public static final RelicSizes sizes = INSTANCE.get_relic_sizes();

	//		void ep2_rand(ep2_st.ByReference a);
	void bn_init(bn_t a, int digits);
	void ep_curve_get_ord(Structure bn_st);
	void ep_rand(Structure a);
	void ep2_rand(Structure a);
	int core_init();
	int ep_param_set(int param);
	void ep_param_print();
	void fp_param_print();
	int ep_param_set_any_pairf();
	int ep_param_embed();
	void core_clean();
	//		void pp_map_oatep_k12(long[] fp_12t, Structure ep_st, Structure ep2_st);
	void pp_map_oatep_k12(Pointer fp_12t, Structure ep_st, Structure ep2_st);
	//		int fp12_cmp_dig(long[] fp_12t,int x);
	int fp12_cmp_dig(Pointer fp_12t,int x);
	void ep_set_infty(Structure ep_st);
	void ep2_set_infty(Structure ep2_st);
	void bn_rand_mod(Structure bn_st,Structure bn_st2);
	void ep2_mul_monty(Structure ep2_st,Structure ep2_st2,Structure bn_st);
	void ep_mul_monty(Structure ep_st,Structure ep_st2,Structure bn_st);
	int fp12_cmp(Pointer fp_12t, Pointer fp_12t2);

	RelicSizes get_relic_sizes();
	void ep_curve_get_ord(bn_t ord);
}