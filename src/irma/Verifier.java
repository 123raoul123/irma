package irma;

import ShowCredential.*;

import java.security.SecureRandom;

import relic.*;

/**
 * Verifier class
 * The verifier class verifies user attributes. This class is used for the ShowCredential protocol.
 */
public class Verifier {

    private IssuerPublicKey pubkey;
    private byte[] nonce;

    public Verifier(IssuerPublicKey pubkey)
    {
        this.pubkey = pubkey;
        nonce = new byte[16];
    }

    /**
     * This method is the first part of the ShowCredential protocol
     * It returns a nonce needed for the proof
     */
    public ShowCredentialResponseMessage createShowCredentialResponseMessage()
    {
        //Generate nonce for schnorr
        SecureRandom rand = new SecureRandom();
        rand.nextBytes(nonce);

        ShowCredentialResponseMessage m = new ShowCredentialResponseMessage(nonce);
        return m;
    }

    /**
     * This method is the second part of the issuance protocol
     * @param first Message received by user contains the disclosed attributes
     * @param second Message received by user. contains blinded signature and proof
     * @return True if all check are passed
     */
    public boolean verifyCredentials(ShowCredentialRequestMessage first, ShowCredentialCommitmentMessage second)
    {
        /*******************************************

         compute D,c and DLRepresentation

         *******************************************/
        ep_t D = Attributes.compute_D(second.getKBlind(), second.getBasePoints(), first.getDisclosedAttributes());
        bn_t c = Attributes.hashAndConvert(nonce,second.getW(),D);
        ep_t res = Attributes.computeDLRepresentation(
                second.getCBlind(), second.getSBlind(), second.getS0Blind(), second.getBasePoints(),
                second.gets_beta(), second.gets(), second.gets0(), second.getsList());

        /*******************************************

         Compute D^c * W

         *******************************************/
        ep_t res_1 = new ep_t();
        Relic.INSTANCE.ep_mul_monty(res_1,D,c);
        Relic.INSTANCE.ep_add_basic(res_1,res_1,second.getW());

        /*******************************************

         Verify if D^c * W = DLRepresentation

         *******************************************/
        if(Relic.INSTANCE.ep_cmp(res,res_1) == 0)
            System.out.print("Yay D^c * W = DLRepresentation\n");
        else
            throw new RuntimeException("No D^c * W != DLRepresentation :(\n");

        /*******************************************

         Verify if E(KBlind,A) = E(SBlind,Q)

         *******************************************/
        fp12_t cmp_1 = new fp12_t(),cmp_2 = new fp12_t();
        Relic.INSTANCE.pp_map_oatep_k12(cmp_1,second.getKBlind(),pubkey.getA());
        Relic.INSTANCE.pp_map_oatep_k12(cmp_2,second.getSBlind(),pubkey.getQ());

        if(Relic.INSTANCE.fp12_cmp(cmp_1,cmp_2) == 0)
            System.out.print("First verification step succeeded\n");
        else
            throw new RuntimeException("first verification step failed\n");

        /*******************************************

         Verify if E(KBlind,A0) = E(S0Blind,Q)

         *******************************************/
        Relic.INSTANCE.pp_map_oatep_k12(cmp_1,second.getKBlind(),pubkey.getAList().get(0));
        Relic.INSTANCE.pp_map_oatep_k12(cmp_2,second.getS0Blind(),pubkey.getQ());

        if(Relic.INSTANCE.fp12_cmp(cmp_1,cmp_2) == 0)
            System.out.print("Second verification step succeeded\n");
        else
            throw new RuntimeException("Second verification step failed\n");

        /*******************************************

         Verify if E(KBlind,Ai) = E(SiBlind,Q)

         *******************************************/
        for(int i = 1; i<pubkey.getAList().size(); ++i)
        {
            Relic.INSTANCE.pp_map_oatep_k12(cmp_1,second.getKBlind(),pubkey.getAList().get(i));
            Relic.INSTANCE.pp_map_oatep_k12(cmp_2,second.getBasePoints().get(i-1),pubkey.getQ());

            if(Relic.INSTANCE.fp12_cmp(cmp_1,cmp_2) == 0)
                System.out.println("verification step "+ i +" succeeded");
            else
                throw new RuntimeException(" verification step "+i+" failed\n");
        }

        /*******************************************

         Verify if E(CBlind,Z) = E(TBlind,Q)

         *******************************************/
        Relic.INSTANCE.pp_map_oatep_k12(cmp_1,second.getCBlind(),pubkey.getZ());
        Relic.INSTANCE.pp_map_oatep_k12(cmp_2,second.getTBlind(),pubkey.getQ());
        if(Relic.INSTANCE.fp12_cmp(cmp_1,cmp_2) == 0){
            System.out.print("Last verification step succeeded\n");
            return true;
        }
        else
            throw new RuntimeException("Last verification step failed\n");
    }

}
