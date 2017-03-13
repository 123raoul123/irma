package relic;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface Relic extends Library {
	public static final Relic INSTANCE = (Relic) Native.loadLibrary("relic", Relic.class);
	public static final RelicSizes SIZES = INSTANCE.get_relic_sizes();

	int  core_init();
	void core_clean();

	void fp_param_print();
	int  fp12_cmp_dig(fp12_t gt, int x);
	int  fp12_cmp(fp12_t lhs, fp12_t rhs);

	void bn_init(bn_t a,int digits);
	void bn_rand_mod(bn_t num, bn_t mod);

	void ep_curve_get_ord(bn_t bn_st);
	int  ep_param_set(int param);
	void ep_rand(ep_t a);
	void ep_param_print();
	int  ep_param_set_any_pairf();
	int  ep_param_embed();
	void ep_set_infty(ep_t ep_st);
	void ep_mul_monty(ep_t retval, ep_t element, bn_t num);

	void ep2_mul_monty(ep2_t retval, ep2_t element, bn_t num);
	void ep2_set_infty(ep2_t ep2_st);
	void ep2_rand(ep2_t a);

	void pp_map_oatep_k12(fp12_t retval, ep_t g1, ep2_t g2);

	RelicSizes get_relic_sizes();
}