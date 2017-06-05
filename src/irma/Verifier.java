package irma;

import ShowCredential.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

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
        /*******************************************

         COMPUTE D

         *******************************************/
        bn_t tmp = new bn_t();
        ep_t ep_temp = new ep_t(), D = new ep_t();

        Relic.INSTANCE.ep_neg_basic(D,second.getK_blind());
        List<Boolean> disclosed = first.getDisclosed();
        Map<Integer,bn_t> disclosedAttributes = first.getDisclosedAttributes();
        List<ep_t> basepoints = second.getBasepoints();

        for(int i=0;i<disclosed.size();++i)
        {
            if(disclosed.get(i))
            {
                // S_i^(-k_i)
                Relic.INSTANCE.bn_neg(tmp,disclosedAttributes.get(i));
                Relic.INSTANCE.ep_mul_monty(ep_temp,basepoints.get(i),tmp);

                // Add to D
                Relic.INSTANCE.ep_add_basic(D,D,ep_temp);
            }
        }

        /*******************************************

         SCHNORR PART

         *******************************************/

        //Convert D and W to bytes
        byte[] D_byte = new byte[1000];
        byte[] W_byte = new byte[1000];
        Relic.INSTANCE.ep_write_bin(D_byte,1000, D,0);
        Relic.INSTANCE.ep_write_bin(W_byte,1000, second.getW(),0);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try
        {
            //Concat ETA (Nonce) W and D
            outputStream.write(this.nonce);
            outputStream.write(W_byte);
            outputStream.write(D_byte);
            byte concat[] = outputStream.toByteArray();


            //HASH RESULT AND CONVERT TO BN_T
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(concat);
            bn_t c = new bn_t();
            Relic.INSTANCE.bn_read_bin(c, hash, hash.length);
//            System.out.printf("c = %s\n", c.toString().substring(0));

            /*

                Compute C_blind^s_beta * S^s * S0^(s0) * (Si^(si)) * ... * (Si^(si))

             */

            ep_t res = new ep_t();
            Relic.INSTANCE.ep_mul_monty(res,second.getC_blind(),second.gets_beta());
            Relic.INSTANCE.ep_mul_monty(ep_temp,second.getS_blind(),second.gets());
            Relic.INSTANCE.ep_add_basic(res,res,ep_temp);
            Relic.INSTANCE.ep_mul_monty(ep_temp,second.getS_zero_blind(),second.gets0());
            Relic.INSTANCE.ep_add_basic(res,res,ep_temp);
            Map<Integer,bn_t> s_list = second.gets_list();

            for(int i=0;i<disclosed.size();++i)
            {
                if(!disclosed.get(i))
                {
                    Relic.INSTANCE.ep_mul_monty(ep_temp,basepoints.get(i),s_list.get(i));
                    Relic.INSTANCE.ep_add_basic(res,res,ep_temp);
                }
            }

            /*

                Compute D^c * W

             */
            ep_t res_1 = new ep_t();
            Relic.INSTANCE.ep_mul_monty(res_1,D,c);
            Relic.INSTANCE.ep_add_basic(res_1,res_1,second.getW());

//            System.out.printf("c = %s\n", c.toString().substring(0));
//            System.out.printf("D = %s\n", D.toString().substring(0));
//            System.out.printf("W = %s\n", second.getW().toString().substring(0));
//            System.out.printf("K_blind = %s\n", second.getK_blind().toString().substring(0));
//            System.out.printf("S_blind = %s\n", second.getS_blind().toString().substring(0));
//            System.out.printf("S_zero_blind = %s\n", second.getS_zero_blind().toString().substring(0));
//            for(int i=0;i<basepoints.size();++i)
//            {
//                System.out.printf("element = %s\n", basepoints.get(i).toString().substring(0));
//            }
//            System.out.printf("C_blind = %s\n", second.getC_blind().toString().substring(0));
//            System.out.printf("T_blind = %s\n", second.getT_blind().toString().substring(0));
//            System.out.printf("s_beta = %s\n", second.gets_beta().toString().substring(0));
//            System.out.printf("s = %s\n", second.gets().toString().substring(0));
//            System.out.printf("S0 = %s\n", S_zero.toString().substring(0));
//            System.out.printf("s0 = %s\n", second.gets0().toString().substring(0));
//            for(int i=0;i<disclosed.size();++i)
//            {
//                if(!disclosed.get(i)) {
//                    System.out.printf("element = %s\n", s_list.get(i).toString().substring(0));
//                }
//            }

//            System.out.printf("re1 = %s\n", res.toString().substring(0));
//            System.out.printf("re2 = %s\n", res_1.toString().substring(0));
            if(Relic.INSTANCE.ep_cmp(res,res_1) == 0)
            {
                System.out.print("Yay Proof succeeded\n");
            }
            else
            {
                throw new RuntimeException("Proof verification failed :(\n");
            }

        }
        catch (IOException | NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong :(\n");
        }

    }

}
