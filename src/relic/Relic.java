package relic;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface Relic extends Library {
	Relic INSTANCE = (Relic) Native.loadLibrary("relic", Relic.class);
	RelicSizes SIZES = INSTANCE.get_relic_sizes();

	/**
	 * Initializes the library.
	 *
	 * @return STS_OK if no error occurs, STS_ERR otherwise.
	 */
	int  core_init();
	/**
	 * Finalizes the library.
	 *
	 * @return STS_OK if no error occurs, STS_ERR otherwise.
	 */
	void core_clean();
	/**
	 * Returns the result of a comparison between two dodecic extension field
	 * elements.
	 *
	 * @param[in] a				- the first dodecic extension field element.
	 * @param[in] b				- the second dodecic extension field element.
	 * @return CMP_LT if a < b, CMP_EQ if a == b and CMP_GT if a > b.
	 */
	int  fp12_cmp(fp12_t lhs, fp12_t rhs);

	/**
	 * Copies the second argument to the first argument.
	 *
	 * @param[out] c			- the result.
	 * @param[in] a				- the multiple precision integer to copy.
	 */
	void bn_copy(bn_t retval,bn_t element);
	/**
	 * Assigns a non-zero random value to a multiple precision integer with absolute
	 * value smaller than a given modulus.
	 *
	 * @param[out] a			- the multiple precision integer to assign.
	 * @param[in] b				- the modulus.
	 */
	void bn_rand_mod(bn_t num, bn_t mod);
	/**
	 * Reads a positive multiple precision integer from a byte vector in big-endian
	 * format.
	 *
	 * @param[out] a			- the result.
	 * @param[in] bin			- the byte vector.
	 * @param[in] len			- the buffer capacity.
	 */
	void bn_read_bin(bn_t retval,byte[] hash, int len);
	/**
	 * Adds two multiple precision integers. Computes c = a + b.
	 *
	 * @param[out] c			- the result.
	 * @param[in] a				- the first multiple precision integer to add.
	 * @param[in] b				- the second multiple precision integer to add.
	 */
	void bn_add(bn_t retval,bn_t a,bn_t b);
	/**
	 * Multiplies two multiple precision integers using Karatsuba multiplication.
	 *
	 * @param[out] c			- the result.
	 * @param[in] a				- the first multiple precision integer to multiply.
	 * @param[in] b				- the second multiple precision integer to multiply.
	 */
	void bn_mul_karat(bn_t retval, bn_t a,bn_t b);
	/**
	 * Writes a positive multiple precision integer to a byte vector in big-endian
	 * format.
	 *
	 * @param[out] bin			- the byte vector.
	 * @param[in] len			- the buffer capacity.
	 * @param[in] a				- the multiple integer to write.
	 * @throw ERR_NO_BUFFER		- if the buffer capacity is insufficient.
	 */
	void bn_write_bin(byte[] retval, int len, bn_t a);
	/**
	 * Computes the extended greatest common divisor of two multiple precision
	 * integer using the Euclidean algorithm.
	 *
	 * @param[out] c			- the result.
	 * @param[out] d			- the cofactor of the first operand, can be NULL.
	 * @param[out] e			- the cofactor of the second operand, can be NULL.
	 * @param[in] a				- the first multiple precision integer.
	 * @param[in] b				- the second multiple precision integer.
	 */
	void bn_gcd_ext_basic(bn_t C, bn_t D, bn_t E, bn_t A, bn_t B);
	/**
	 * Inverts the sign of a multiple precision integer.
	 *
	 * @param[out] c			- the result.
	 * @param[out] a			- the multiple precision integer to negate.
	 */
	void bn_neg(bn_t res,bn_t a);
	/**
	 * Returns the order of the group of points in the prime elliptic curve.
	 *
	 * @param[out] r			- the returned order.
	 */
	void ep_curve_get_ord(bn_t bn_st);
	/**
	 * Copies the second argument to the first argument.
	 *
	 * @param[out] q			- the result.
	 * @param[in] p				- the prime elliptic curve point to copy.
	 */
	void ep_copy(ep_t retval, ep_t element);
	/**
	 * Assigns a random value to a prime elliptic curve point.
	 *
	 * @param[out] p			- the prime elliptic curve point to assign.
	 */
	void ep_rand(ep_t a);
	/**
	 * Configures some set of pairing-friendly curve parameters for the current
	 * security level.
	 *
	 * @return STS_OK if there is a curve at this security level, STS_ERR otherwise.
	 */
	int  ep_param_set_any_pairf();
	/**
	 * Adds two prime elliptic curve points represented in affine coordinates.
	 *
	 * @param[out] r			- the result.
	 * @param[in] p				- the first point to add.
	 * @param[in] q				- the second point to add.
	 */
	void ep_add_basic(ep_t retval,ep_t first,ep_t second);
	/**
	 * Multiplies a prime elliptic point by an integer using the constant-time
	 * Montgomery laddering point multiplication method.
	 *
	 * @param[out] r			- the result.
	 * @param[in] p				- the point to multiply.
	 * @param[in] k				- the integer.
	 */
	void ep_mul_monty(ep_t retval, ep_t element, bn_t num);
	/**
	 * Writes a prime elliptic curve point to a byte vector in big-endian format
	 * with optional point compression.
	 *
	 * @param[out] bin			- the byte vector.
	 * @param[in] len			- the buffer capacity.
	 * @param[in] a				- the prime elliptic curve point to write.
	 * @param[in] pack			- the flag to indicate point compression.
	 * @throw ERR_NO_BUFFER		- if the buffer capacity is invalid.
	 */
	void ep_write_bin(byte[] retval, int len, ep_t a, int pack);
	/**
	 * Compares two prime elliptic curve points.
	 *
	 * @param[in] p				- the first prime elliptic curve point.
	 * @param[in] q				- the second prime elliptic curve point.
	 * @return CMP_EQ if p == q and CMP_NE if p != q.
	 */
	int ep_cmp(ep_t p,ep_t q);
	/**
	 * Negates a prime elliptic curve point represented by affine coordinates.
	 *
	 * @param[out] r			- the result.
	 * @param[in] p				- the point to negate.
	 */
	void ep_neg_basic(ep_t r, ep_t p);
	/**
	 * Copies the second argument to the first argument.
	 *
	 * @param[out] q			- the result.
	 * @param[in] p				- the elliptic curve point to copy.
	 */
	void ep2_copy(ep2_t retval, ep2_t element);
	/**
	 * Multiplies a prime elliptic point by an integer using the constant-time
	 * Montgomery laddering point multiplication method.
	 *
	 * @param[out] r			- the result.
	 * @param[in] p				- the point to multiply.
	 * @param[in] k				- the integer.
	 */
	void ep2_mul_monty(ep2_t retval, ep2_t element, bn_t num);
	/**
	 * Assigns a random value to an elliptic curve point.
	 *
	 * @param[out] p			- the elliptic curve point to assign.
	 */
	void ep2_rand(ep2_t a);
	/**
	 * Computes the optimal ate pairing of two points in a parameterized elliptic
	 * curve with embedding degree 12.
	 *
	 * @param[out] r			- the result.
	 * @param[in] q				- the first elliptic curve point.
	 * @param[in] p				- the second elliptic curve point.
	 */
	void pp_map_oatep_k12(fp12_t retval, ep_t g1, ep2_t g2);

	RelicSizes get_relic_sizes();
}