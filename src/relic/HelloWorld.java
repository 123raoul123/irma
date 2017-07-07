package relic;

import Issue.*;
import ShowCredential.*;
import irma.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HelloWorld {
	public static void main(String[] args) {
		/*******************************************

		 Initialize relic library

		 *******************************************/
		initRelic();

		/*******************************************

		 Generate random Q and set n

		 *******************************************/
		ep2_t Q = new ep2_t();
		Relic.INSTANCE.ep2_rand(Q);
		int n = 5;

		/*******************************************

		 Initialize issuer

		 *******************************************/
		IssuerPrivateKey pk = new IssuerPrivateKey(n,Q);
		Issuer issuer = new Issuer(pk);
		/*******************************************

		 Initialize User with attributes n-1

		 *******************************************/
		Attributes at = new Attributes(n-1);
		UserPrivateKey privk = new UserPrivateKey();
		User user = new User(privk,at);

		/*******************************************

		 TEST ISSUE PROTOCOL

		 *******************************************/
		IssueRequestMessage fum_mes = user.createIssueRequestMessage();
		IssueResponseMessage fim_mes = issuer.createIssueResponseMessage();
		IssueCommitmentMessage sum_mes = user.createIssueCommitmentMessage(fim_mes);
		IssueSignatureMessage sim_mes = issuer.createIssueSignatureMessage(fum_mes,sum_mes);
		user.setSignature(sim_mes);

		/*******************************************

		 Initialize verifier

		 *******************************************/
		Verifier verifier = new Verifier(pk.getPublicKey());

		/*******************************************

		 Set list of disclosed attributes
		 TRUE = Disclosed
		 FALSE = Not Disclosed

		 *******************************************/
		List<Boolean> bools = new ArrayList<>();
		bools.add(true);
		bools.add(false);
		bools.add(false);
		bools.add(true);

		/*******************************************

		 TEST SHOW CREDENTIAL PROTOCOL

		 *******************************************/
		ShowCredentialRequestMessage fium_mes = user.createShowCredentialRequestMessage(bools);
		ShowCredentialResponseMessage fvm_mes = verifier.createShowCredentialResponseMessage();
		ShowCredentialCommitmentMessage seum_mes = user.createShowCredentialCommitmentMessage(fium_mes, fvm_mes,bools);
		verifier.verifyCredentials(fium_mes,seum_mes);

		/*******************************************

		 Clean relic

		 *******************************************/
		System.out.println("Cleaning up Relic");
		Relic.INSTANCE.core_clean();

	}

	/**
	 * Initialises relic by loading the library and setting the values for the objects used
	 */
	private static void initRelic() {
		// The .getResource() method only returns the correct path when
		// it is asked to locate an existing file - asking for "", "." or "/"
		// does not work.
		//CURENTLY THIS LINE OF CODE LOADS MAC VERSION (DYLIB) CHANGE THIS TO LOAD FOR DIFFERENT ARCHITECTURE
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


