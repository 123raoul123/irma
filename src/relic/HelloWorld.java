package relic;

public class HelloWorld {
	public static void main(String[] args) {
		bn_st.ByReference ref_k = new bn_st.ByReference();
		bn_st.ByReference ref_n = new bn_st.ByReference();
		bn_st[] k = (bn_st[]) ref_k.toArray(1);
		bn_st[] n = (bn_st[]) ref_n.toArray(1);

		ep_st.ByReference ref_p = new ep_st.ByReference();
		ep_st[] p = (ep_st[]) ref_p.toArray(2);

		ep2_st.ByReference ref_q = new ep2_st.ByReference();
		ep2_st.ByReference ref_r = new ep2_st.ByReference();
		ep2_st[] q = (ep2_st[]) ref_q.toArray(2);
		ep2_st[] r = (ep2_st[]) ref_r.toArray(1);

		fp12_t e1 = new fp12_t();
		fp12_t e2 = new fp12_t();
//		long[] e1 = new long[fp_12t];
//		long[] e2 = new long[fp_12t];

		if(Relic.INSTANCE.core_init() == 1){
			Relic.INSTANCE.core_clean();
			System.exit(1);
		}

		if(Relic.INSTANCE.ep_param_set_any_pairf() == 1)
		{
			Relic.INSTANCE.core_clean();
			System.exit(0);
		}
		Relic.INSTANCE.ep_param_print();
//		Relic.INSTANCE.fp_param_print();
		System.out.println(Relic.INSTANCE.ep_param_embed());

//		Relic.INSTANCE.bn_init(k,BN_SIZE);
//		Relic.INSTANCE.bn_init(n,BN_SIZE);
//		Relic.INSTANCE.bn_init(l,BN_SIZE);
		Relic.INSTANCE.ep_curve_get_ord(n[0]);

		System.out.println("optimal ate pairing non-degeneracy is correct");
		Relic.INSTANCE.ep_rand(p[0]);
		Relic.INSTANCE.ep2_rand(q[0]);

		Relic.INSTANCE.pp_map_oatep_k12(e1,p[0],q[0]);
		System.out.print("Next result should NOT equal 0 : ");
		System.out.println(Relic.INSTANCE.fp12_cmp_dig(e1,1));

		Relic.INSTANCE.ep_set_infty(p[0]);
		Relic.INSTANCE.pp_map_oatep_k12(e1,p[0],q[0]);
		System.out.print("Next result should equal 0 : ");
		System.out.println(Relic.INSTANCE.fp12_cmp_dig(e1,1));

		Relic.INSTANCE.ep_rand(p[0]);
		Relic.INSTANCE.ep2_set_infty(q[0]);
		Relic.INSTANCE.pp_map_oatep_k12(e1,p[0],q[0]);
		System.out.print("Next result should equal 0 : ");
		System.out.println(Relic.INSTANCE.fp12_cmp_dig(e1,1));

		System.out.println("optimal ate pairing is bilinear");
		Relic.INSTANCE.ep_rand(p[0]);
		Relic.INSTANCE.ep2_rand(q[0]);
		Relic.INSTANCE.bn_rand_mod(k[0], n[0]);
		Relic.INSTANCE.ep2_mul_monty(r[0], q[0], k[0]);
		Relic.INSTANCE.pp_map_oatep_k12(e1,p[0],r[0]);
		Relic.INSTANCE.ep_mul_monty(p[0],p[0],k[0]);
		Relic.INSTANCE.pp_map_oatep_k12(e2,p[0],q[0]);
		System.out.print("Next result should equal 0 : ");
		System.out.println(Relic.INSTANCE.fp12_cmp(e1,e2));

		Relic.INSTANCE.core_clean();
	}
}


