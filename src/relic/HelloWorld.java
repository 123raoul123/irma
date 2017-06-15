package relic;

import Issue.IssuerIssueFirstMessage;
import Issue.IssuerIssueSecondMessage;
import Issue.UserIssueFirstMessage;
import Issue.UserIssueSecondMessage;
import ShowCredential.UserShowCredentialFirstMessage;
import ShowCredential.UserShowCredentialSecondMessage;
import ShowCredential.VerifierShowCredentialFirstMessage;
import irma.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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


		// Calculate a^{-1} mod ord
		bn_t a = new bn_t(), a_inverse = new bn_t(), ord = new bn_t(), tmp = new bn_t();
		Relic.INSTANCE.ep_curve_get_ord(ord); // Get the group order
		Relic.INSTANCE.bn_rand_mod(a, ord);   // Generate a random a
		Relic.INSTANCE.bn_gcd_ext_basic(tmp, a_inverse, null, a, ord);

		// Check that a * a^{-1} = 1 mod ord
		bn_t one = new bn_t();
		Relic.INSTANCE.bn_mul_karat(one, a, a_inverse); // a * a^{-1}
		Relic.INSTANCE.bn_mod_basic(one, one, ord);     // reduce mod ord
		System.out.printf("Modular inverse works: %s\n", Relic.INSTANCE.bn_cmp_dig(one, 1) == 0);
		*/

		ep2_t Q = new ep2_t();
		Relic.INSTANCE.ep2_rand(Q);
		int n = 5;

		IssuerPrivateKey pk = new IssuerPrivateKey(n,Q);
		Issuer issuer = new Issuer(pk);
		Attributes at = new Attributes(n-1);
		UserPrivateKey privk = new UserPrivateKey();
		User user = new User(privk,at);

		// ISSUE PROTOCOL
		UserIssueFirstMessage fum_mes = user.createUserIssueFirstMessage();
		IssuerIssueFirstMessage fim_mes = issuer.createFirstIssuerMessage();
		UserIssueSecondMessage sum_mes = user.createUserIssueSecondMessage(fim_mes);
		IssuerIssueSecondMessage sim_mes = issuer.createSecondIssuerMessage(fum_mes,sum_mes);
		user.setSignature(sim_mes);

		//ShowCredential protocol
		Verifier verifier = new Verifier(pk.getPublicKey());

		List<Boolean> bools = new ArrayList<>();
		bools.add(true);
		bools.add(false);
		bools.add(false);
		bools.add(true);

		UserShowCredentialFirstMessage fium_mes = user.createUserShowCredentialFirstMessage(bools);
		VerifierShowCredentialFirstMessage fvm_mes = verifier.createVerifierShowCredentialFirstMessage();
		UserShowCredentialSecondMessage seum_mes = user.createUserShowCredentialSecondMessage(fium_mes, fvm_mes,bools);
		verifier.verifyCredentials(fium_mes,seum_mes);

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


