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
	void bn_copy(bn_t retval,bn_t element);
	void bn_rand_mod(bn_t num, bn_t mod);
	void bn_read_bin(bn_t retval,byte[] hash, int len);
	void bn_add(bn_t retval,bn_t a,bn_t b);
	void bn_mul_karat(bn_t retval, bn_t a,bn_t b);
	void bn_write_bin(byte[] retval, int len, bn_t a);
	void bn_mod_basic(bn_t result, bn_t num, bn_t modulus);

	void ep_curve_get_ord(bn_t bn_st);
	int  ep_param_set(int param);
	void ep_copy(ep_t retval, ep_t element);
	void ep_rand(ep_t a);
	void ep_param_print();
	int  ep_param_set_any_pairf();
	int  ep_param_embed();
	void ep_set_infty(ep_t ep_st);
	void ep_add_basic(ep_t retval,ep_t first,ep_t second);
	void ep_mul_monty(ep_t retval, ep_t element, bn_t num);
	void ep_write_bin(byte[] retval, int len, ep_t a, int pack);
	int ep_cmp(ep_t p,ep_t q);

	void ep2_copy(ep2_t retval, ep2_t element);
	void ep2_mul_monty(ep2_t retval, ep2_t element, bn_t num);
	void ep2_mul_lwnaf(ep2_t retval, ep2_t element, bn_t num);
	void ep2_add_basic(ep2_t retval, ep2_t first, ep2_t second);
	void ep2_set_infty(ep2_t ep2_st);
	void ep2_rand(ep2_t a);


	void pp_map_oatep_k12(fp12_t retval, ep_t g1, ep2_t g2);

	RelicSizes get_relic_sizes();
}