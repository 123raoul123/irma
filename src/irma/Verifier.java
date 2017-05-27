package irma;

import ShowCredential.*;
import java.security.SecureRandom;

/**
 * Created by raoul on 25/05/2017.
 */
public class Verifier {

    private IssuerPublicKey pubkey;
    private byte[] nonce;

    public Verifier(IssuerPublicKey pubkey)
    {
        this.pubkey = pubkey;
    }

    public VerifierShowCredentialFirstMessage createVerifierShowCredentialFirstMessage()
    {
        //Generate nonce for schnor
        SecureRandom rand = new SecureRandom();
        rand.nextBytes(nonce);

        VerifierShowCredentialFirstMessage m = new VerifierShowCredentialFirstMessage(nonce);
        return m;
    }

}
