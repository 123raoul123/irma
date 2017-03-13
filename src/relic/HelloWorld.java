package relic;
import java.util.Arrays;
import java.util.List;
import com.sun.jna.Function;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import relic.ep2_st.ByReference;

import com.sun.jna.Memory;
import com.sun.jna.NativeLibrary;

public class HelloWorld {
    
    public interface Relic extends Library 
    {	   
//		void ep2_rand(ep2_st.ByReference a);
		void bn_init(Structure[] a,int digits);
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
    }
	
	public static void main(String[] args) 
    {	

		int DIGIT = 64;
		int FP_PRIME = 638;
		int FP_DIGIT = DIGIT;
		int FP_BITS = FP_PRIME;
		int FP_DIGS = (((FP_BITS)/(FP_DIGIT) + 1));
		
		System.out.println(FP_DIGS);
		
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
		
		int fp_12t = 2*3*2*FP_DIGS;
		Pointer e1 = new Memory(fp_12t * Native.getNativeSize(long.class));
		Pointer e2 = new Memory(fp_12t * Native.getNativeSize(long.class));

			
		Relic INSTANCE = (Relic) Native.loadLibrary("/Users/raoul/Documents/workspace/irma/bin/librelic.dylib",Relic.class);
		if(INSTANCE.core_init() == 1){
			INSTANCE.core_clean();
			System.exit(1);
		}
		
		if(INSTANCE.ep_param_set_any_pairf() == 1)
		{
			INSTANCE.core_clean();
			System.exit(0);
		}
		INSTANCE.ep_param_print();

		System.out.println(INSTANCE.ep_param_embed());
		

		INSTANCE.ep_curve_get_ord(n[0]);
		
		System.out.println("optimal ate pairing non-degeneracy is correct");
		INSTANCE.ep_rand(p[0]);
		INSTANCE.ep2_rand(q[0]);
		
		INSTANCE.pp_map_oatep_k12(e1,p[0],q[0]);
		System.out.print("Next result should NOT equal 0 : ");
		System.out.println(INSTANCE.fp12_cmp_dig(e1,1));
		
		INSTANCE.ep_set_infty(p[0]);
		INSTANCE.pp_map_oatep_k12(e1,p[0],q[0]);
		System.out.print("Next result should equal 0 : ");
		System.out.println(INSTANCE.fp12_cmp_dig(e1,1));

		INSTANCE.ep_rand(p[0]);
		INSTANCE.ep2_set_infty(q[0]);
		INSTANCE.pp_map_oatep_k12(e1,p[0],q[0]);
		System.out.print("Next result should equal 0 : ");
		System.out.println(INSTANCE.fp12_cmp_dig(e1,1));
		
		System.out.println("optimal ate pairing is bilinear");
		INSTANCE.ep_rand(p[0]);
		INSTANCE.ep2_rand(q[0]);
		INSTANCE.bn_rand_mod(k[0], n[0]);
		INSTANCE.ep2_mul_monty(r[0], q[0], k[0]);
		INSTANCE.pp_map_oatep_k12(e1,p[0],r[0]);
		INSTANCE.ep_mul_monty(p[0],p[0],k[0]);
		INSTANCE.pp_map_oatep_k12(e2,p[0],q[0]);
		System.out.print("Next result should equal 0 : ");
		System.out.println(INSTANCE.fp12_cmp(e1,e2));
		
		INSTANCE.core_clean();

    }
}


