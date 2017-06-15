package irma;

import ShowCredential.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import relic.*;

/**
 * Created by raoul on 25/05/2017.
 */
public class Verifier {

    private IssuerPublicKey pubkey;
    private byte[] nonce;

    public Verifier(IssuerPublicKey pubkey)
    {
        this.pubkey = pubkey;
        nonce = new byte[16];
    }

    public VerifierShowCredentialFirstMessage createVerifierShowCredentialFirstMessage()
    {
        //Generate nonce for schnorr
        SecureRandom rand = new SecureRandom();
        rand.nextBytes(nonce);

        VerifierShowCredentialFirstMessage m = new VerifierShowCredentialFirstMessage(nonce);
        return m;
    }

    public void verifyCredentials(UserShowCredentialFirstMessage first,UserShowCredentialSecondMessage second)
    {

        ep_t D = Attributes.compute_D(second.getK_blind(), second.getBasePoints(), first.getDisclosedAttributes());
        bn_t c = Attributes.hashAndConvert(nonce,second.getW(),D);

        /*

            Compute C_blind^s_beta * S^s * S0^(s0) * (Si^(si)) * ... * (Si^(si))

         */

        ep_t res = Attributes.computeDLRepresentation(
                second.getC_blind(), second.getS_blind(), second.getS_zero_blind(), second.getBasePoints(),
                second.gets_beta(), second.gets(), second.gets0(), second.getsList());

        /*

            Compute D^c * W

         */
        ep_t res_1 = new ep_t();
        Relic.INSTANCE.ep_mul_monty(res_1,D,c);
        Relic.INSTANCE.ep_add_basic(res_1,res_1,second.getW());

        if(Relic.INSTANCE.ep_cmp(res,res_1) == 0)
            System.out.print("Yay Proof succeeded\n");
        else
            throw new RuntimeException("Proof verification failed :(\n");

        /*
            Start verification process
         */

        fp12_t cmp_1 = new fp12_t(),cmp_2 = new fp12_t();
        Relic.INSTANCE.pp_map_oatep_k12(cmp_1,second.getK_blind(),pubkey.getA());
        Relic.INSTANCE.pp_map_oatep_k12(cmp_2,second.getS_blind(),pubkey.getQ());

        if(Relic.INSTANCE.fp12_cmp(cmp_1,cmp_2) == 0)
            System.out.print("First verification step succeeded\n");
        else
            throw new RuntimeException("first verification step failed\n");

        Relic.INSTANCE.pp_map_oatep_k12(cmp_1,second.getK_blind(),pubkey.getAList().get(0));
        Relic.INSTANCE.pp_map_oatep_k12(cmp_2,second.getS_zero_blind(),pubkey.getQ());

        if(Relic.INSTANCE.fp12_cmp(cmp_1,cmp_2) == 0)
            System.out.print("Second verification step succeeded\n");
        else
            throw new RuntimeException("Second verification step failed\n");

        for(int i = 1; i<pubkey.getAList().size(); ++i)
        {
            Relic.INSTANCE.pp_map_oatep_k12(cmp_1,second.getK_blind(),pubkey.getAList().get(i));
            Relic.INSTANCE.pp_map_oatep_k12(cmp_2,second.getBasePoints().get(i-1),pubkey.getQ());

            if(Relic.INSTANCE.fp12_cmp(cmp_1,cmp_2) == 0)
                System.out.println("verification step "+ i +" succeeded");
            else
                throw new RuntimeException(" verification step "+i+" failed\n");
        }

        Relic.INSTANCE.pp_map_oatep_k12(cmp_1,second.getC_blind(),pubkey.getZ());
        Relic.INSTANCE.pp_map_oatep_k12(cmp_2,second.getT_blind(),pubkey.getQ());

        if(Relic.INSTANCE.fp12_cmp(cmp_1,cmp_2) == 0)
            System.out.print("Last verification step succeeded\n");
        else
            throw new RuntimeException("Last verification step failed\n");
    }

}
