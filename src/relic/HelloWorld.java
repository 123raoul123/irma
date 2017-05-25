package relic;

import Issue.IssuerIssueFirstMessage;
import Issue.UserIssueFirstMessage;
import Issue.UserIssueSecondMessage;
import irma.*;

import java.net.URL;

public class HelloWorld {
	public static void main(String[] args) {
		initRelic();

		/*
		Relic.INSTANCE.ep_param_print();
		System.out.println(Relic.INSTANCE.ep_param_embed());

		bn_t n = new bn_t();
		Relic.INSTANCE.ep_curve_get_ord(n);

		fp12_t e1 = new fp12_t();
		fp12_t e2 = new fp12_t();
		ep_t p = new ep_t();
		ep2_t q = new ep2_t();
		Relic.INSTANCE.ep_rand(p);
		Relic.INSTANCE.ep2_rand(q);
		Relic.INSTANCE.pp_map_oatep_k12(e1,p,q);

		System.out.println("optimal ate pairing non-degeneracy is correct");
		System.out.print("Next result should NOT equal 0 : ");
		System.out.println(Relic.INSTANCE.fp12_cmp_dig(e1, 1));

		Relic.INSTANCE.ep_set_infty(p);
		Relic.INSTANCE.pp_map_oatep_k12(e1,p,q);
		System.out.print("Next result should equal 0 : ");
		System.out.println(Relic.INSTANCE.fp12_cmp_dig(e1, 1));

		Relic.INSTANCE.ep_rand(p);
		Relic.INSTANCE.ep2_set_infty(q);
		Relic.INSTANCE.pp_map_oatep_k12(e1,p,q);
		System.out.print("Next result should equal 0 : ");
		System.out.println(Relic.INSTANCE.fp12_cmp_dig(e1,1));

		bn_t k = new bn_t();
		ep2_t r = new ep2_t();
		System.out.println("optimal ate pairing is bilinear");
		Relic.INSTANCE.ep_rand(p);
		Relic.INSTANCE.ep2_rand(q);
		Relic.INSTANCE.bn_rand_mod(k, n);
		Relic.INSTANCE.ep2_mul_monty(r, q, k);
		Relic.INSTANCE.pp_map_oatep_k12(e1,p,r);
		Relic.INSTANCE.ep_mul_monty(p,p,k);
		Relic.INSTANCE.pp_map_oatep_k12(e2,p,q);
		System.out.print("Next result should equal 0 : ");
		System.out.println(Relic.INSTANCE.fp12_cmp(e1,e2));
		*/

		ep2_t Q = new ep2_t();
		Relic.INSTANCE.ep2_rand(Q);
		int n = 5;

		PrivateKey pk = new PrivateKey(n,Q);
		Issuer issue = new Issuer(pk);
		Attributes at = new Attributes(n);
		UserPrivateKey privk = new UserPrivateKey();
		User use = new User(privk,at);

		UserIssueFirstMessage fum_mes = use.createUserIssueFirstMessage();
		IssuerIssueFirstMessage fim_mes = issue.createFirstIssuerMessage();
		UserIssueSecondMessage sum_mes = use.createUserIssueSecondMessage(fim_mes);
		issue.createSecondIssuerMessage(fum_mes,sum_mes);

		System.out.println("Cleaning up Relic");
		Relic.INSTANCE.core_clean();

	}

	private static void initRelic() {
		// The .getResource() method only returns the correct path when
		// it is asked to locate an existing file - asking for "", "." or "/"
		// does not work.
		URL url = HelloWorld.class.getClassLoader().getResource("librelic.dylib");
		if (url == null)
			throw new RuntimeException("Native relic library not found");

		// Strip the file from the path
		String resources = url.getPath();
		resources = resources.substring(0, resources.lastIndexOf('/'));
		System.setProperty("jna.library.path", resources);

		if(Relic.INSTANCE.core_init() == 1 || Relic.INSTANCE.ep_param_set_any_pairf() == 1) {
			Relic.INSTANCE.core_clean();
			System.exit(1);
		}
	}
}


