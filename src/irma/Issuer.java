package irma;
import Issue.IssuerIssueFirstMessage;
import Issue.IssuerIssueSecondMessage;
import Issue.UserIssueFirstMessage;
import Issue.UserIssueSecondMessage;
import relic.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


public class Issuer {

    private IssuerPrivateKey privkey;
    private byte[] nonce;
    private ep_t S_bar,S_zero_bar;

    public Issuer(IssuerPrivateKey privkey)
    {
        this.privkey = privkey;
        nonce = new byte[16];
        S_bar = new ep_t();
        S_zero_bar = new ep_t();
    }

    public IssuerIssueFirstMessage createFirstIssuerMessage()
    {
        //Generate nonce for schnor
        SecureRandom rand = new SecureRandom();
        rand.nextBytes(this.nonce);

        //Choose random K_bar from G_1
        ep_t K_bar = new ep_t();
        Relic.INSTANCE.ep_rand(K_bar);

        //S_bar = K_bar^a
        Relic.INSTANCE.ep_mul_monty(S_bar,K_bar,privkey.geta());

        //S_zero_bar = K_bar^a_0
        Relic.INSTANCE.ep_mul_monty(S_zero_bar,K_bar,privkey.geta_list().get(0));

        IssuerIssueFirstMessage message = new IssuerIssueFirstMessage(S_bar,S_zero_bar,this.nonce);

        return message;
    }

    public IssuerIssueSecondMessage createSecondIssuerMessage(UserIssueFirstMessage first, UserIssueSecondMessage second)
    {
        //Convert R and W to bytes
        byte[] R_byte = new byte[1000];
        byte[] W_byte = new byte[1000];
        Relic.INSTANCE.ep_write_bin(R_byte,1000, second.getR(),0);
        Relic.INSTANCE.ep_write_bin(W_byte,1000, second.getW(),0);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try
        {
            //Concat ETA (Nonce) W and R
            outputStream.write(this.nonce);
            outputStream.write(W_byte);
            outputStream.write(R_byte);
            byte concat[] = outputStream.toByteArray();


            //HASH RESULT AND CONVERT TO BN_T
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(concat);
            bn_t c = new bn_t();
            Relic.INSTANCE.bn_read_bin(c,hash,hash.length);

            // res = R^c W
            ep_t res = new ep_t();
            Relic.INSTANCE.ep_mul_monty(res, second.getR(),c);
            Relic.INSTANCE.ep_add_basic(res,res, second.getW());

            // Obtain (S^s) (S_0^s_0)

            //left = (S^s)
            ep_t left = new ep_t();
            Relic.INSTANCE.ep_mul_monty(left, second.getS(), second.gets());

            //right = (S_0^s_0)
            ep_t right = new ep_t();
            Relic.INSTANCE.ep_mul_monty(right, second.getS_zero(), second.gets_0());

            // res_1 = (S^s) (S_0^s_0)
            ep_t res_1 = new ep_t();
            Relic.INSTANCE.ep_add_basic(res_1,left,right);

//            System.out.printf("R = %s\n", second.getR().toString().substring(0));
//            System.out.printf("W = %s\n", second.getW().toString().substring(0));
//            System.out.printf("S = %s\n", second.getS().toString().substring(0));
//            System.out.printf("s = %s\n", second.gets().toString().substring(0));
//            System.out.printf("S0 = %s\n", second.getS_zero().toString().substring(0));
//            System.out.printf("s0 = %s\n", second.gets_0().toString().substring(0));

            if(Relic.INSTANCE.ep_cmp(res,res_1) == 0)
            {
                System.out.print("Yay\n");
            }
            else
            {
                throw new RuntimeException("Proof verification failed :(\n");
            }

            //Verify that S != S_bar
            if(Relic.INSTANCE.ep_cmp(S_bar,second.getS()) != 0)
            {
                System.out.print("Yay S != S_bar\n");
            }
            else
            {
                throw new RuntimeException("Proof verification failed :(\n");
            }

            //Set K = S^(1/a)
            // Calculate a^{-1} mod ord
            bn_t  inverse = new bn_t(), ord = new bn_t(), tmp = new bn_t();
            Relic.INSTANCE.ep_curve_get_ord(ord); // Get the group order
            Relic.INSTANCE.bn_gcd_ext_basic(tmp, inverse, null, privkey.geta(), ord);
            ep_t K = new ep_t();
            Relic.INSTANCE.ep_mul_monty(K,second.getS(),inverse);

            // Set res = S0^(1/a0)
            // Calculate a0^{-1}
            Relic.INSTANCE.bn_gcd_ext_basic(tmp, inverse, null, privkey.geta_list().get(0), ord);
            Relic.INSTANCE.ep_mul_monty(res,second.getS_zero(),inverse);

            //Verify that K = S_zero^(1/a0)
            if(Relic.INSTANCE.ep_cmp(K,res) == 0)
            {
                System.out.print("Yay K = S_zero^(1/a0)\n");
            }
            else
            {
                throw new RuntimeException("Proof verification failed :(\n");
            }

            //generate random k'', kappa_pp = k''
            bn_t kappa_pp = new bn_t();
            Relic.INSTANCE.bn_rand_mod(kappa_pp,ord);

            //Set Si = K^(ai) where i [1..n]
            List<ep_t> signed_attribute_list = new ArrayList<ep_t>();

            for(int i=1; i<privkey.geta_list().size();++i)
            {
                Relic.INSTANCE.ep_mul_monty(res,K,privkey.geta_list().get(i));
                signed_attribute_list.add(res);
            }

            //Calculate T
            ep_t T = new ep_t();
            Relic.INSTANCE.ep_mul_monty(res,second.getS(),kappa_pp);
            Relic.INSTANCE.ep_add_basic(T,K,res);
            Relic.INSTANCE.ep_add_basic(T,T,second.getR());

            // Get unsigned attributes from first User message
            List<bn_t> unsigned_attributes = first.getUnsigned_attributes();

            for(int i=1; i<signed_attribute_list.size();++i)
            {
                Relic.INSTANCE.ep_mul_monty(res,signed_attribute_list.get(i),unsigned_attributes.get(i-1));
                Relic.INSTANCE.ep_add_basic(T,T,res);
            }

            Relic.INSTANCE.ep_mul_monty(T,T,privkey.getz());
            IssuerIssueSecondMessage mes = new IssuerIssueSecondMessage(kappa_pp,K,signed_attribute_list,T);

            return mes;

        }
        catch (IOException | NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong :(\n");
        }
    }



}
