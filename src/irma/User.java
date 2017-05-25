package irma;

import Issue.*;
import ShowCredential.UserShowCredentialFirstMessage;
import relic.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.ByteArrayOutputStream;
import java.util.List;


/**
 * Created by raoul on 06/05/2017.
 */
public class User {

    private UserPrivateKey privkey;
    private bn_t kappa_p,kappa;// p is for '
    private ep_t S, S_zero,K,C;
    private Attributes attributes;

    public User(UserPrivateKey privkey, Attributes at)
    {
        this.privkey = privkey;
        kappa_p = new bn_t();
        kappa = new bn_t();
        S = new ep_t();
        S_zero = new ep_t();
        K = new ep_t();
        C = new ep_t();
        attributes = at;
    }

    public UserIssueFirstMessage createUserIssueFirstMessage()
    {
        UserIssueFirstMessage mes = new UserIssueFirstMessage(attributes);
        return mes;
    }

    public UserIssueSecondMessage createUserIssueSecondMessage(IssuerIssueFirstMessage message){
        bn_t alpha = new bn_t();
        ep_t R = new ep_t();
        bn_t ord = new bn_t();

        ep_t W = new ep_t();
        byte[] hash;

        //generate random k' and alpha
        Relic.INSTANCE.ep_curve_get_ord(ord);
        Relic.INSTANCE.bn_rand_mod(alpha,ord);
        Relic.INSTANCE.bn_rand_mod(kappa_p,ord);

        //S = S_bar^alpha
        Relic.INSTANCE.ep_mul_monty(S,message.getS_bar(),alpha);

        //S_zero = S_zero_bar^alpha
        Relic.INSTANCE.ep_mul_monty(S_zero,message.getS_zero_bar(),alpha);

        ep_t left = new ep_t();
        ep_t right = new ep_t();

        //R = S^k' S_zero^k_0
        Relic.INSTANCE.ep_mul_monty(left,S,kappa_p);
        Relic.INSTANCE.ep_mul_monty(right,S_zero,privkey.getk_zero());

        //add left and right to obtain R
        Relic.INSTANCE.ep_add_basic(R,left,right);

        //Create W = S^w S_0^w_0

        //random w and w_0
        bn_t w = new bn_t();
        bn_t w_0 = new bn_t();
        Relic.INSTANCE.bn_rand_mod(w,ord);
        Relic.INSTANCE.bn_rand_mod(w_0,ord);


        Relic.INSTANCE.ep_mul_monty(left,S,w);
        Relic.INSTANCE.ep_mul_monty(right,S_zero,w_0);

        //add left and right to obtain W
        Relic.INSTANCE.ep_add_basic(W,left,right);

        //Convert R and W to bytes
        byte[] R_byte = new byte[1000];
        byte[] W_byte = new byte[1000];
        Relic.INSTANCE.ep_write_bin(R_byte,1000,R,0);
        Relic.INSTANCE.ep_write_bin(W_byte,1000,W,0);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try
        {
            //Concat ETA (Nonce) W and R
            outputStream.write(message.getNonce());
            outputStream.write(W_byte);
            outputStream.write(R_byte);
            byte concat[] = outputStream.toByteArray();


            //HASH RESULT AND CONVERT TO BN_T
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(concat);
            bn_t c = new bn_t();
            Relic.INSTANCE.bn_read_bin(c,hash,hash.length);

            //Create s = ck'+w
            bn_t temp = new bn_t();
            bn_t s = new bn_t();
            Relic.INSTANCE.bn_mul_karat(temp,c,kappa_p);
            Relic.INSTANCE.bn_add(s,temp,w);
            Relic.INSTANCE.bn_mod_basic(s, s, ord);

            //Create s_0 = ck0 + w0
            bn_t s_0 = new bn_t();
            Relic.INSTANCE.bn_mul_karat(temp,c,privkey.getk_zero());
            Relic.INSTANCE.bn_add(s_0,temp,w_0);
            Relic.INSTANCE.bn_mod_basic(s_0, s_0, ord);

//            System.out.printf("R = %s\n", R.toString().substring(0));
//            System.out.printf("W = %s\n", W.toString().substring(0));
//            System.out.printf("S = %s\n", S.toString().substring(0));
//            System.out.printf("s = %s\n", s.toString().substring(0));
//            System.out.printf("S0 = %s\n", S_zero.toString().substring(0));
//            System.out.printf("s0 = %s\n", s_0.toString().substring(0));

            UserIssueSecondMessage m = new UserIssueSecondMessage(S,S_zero,R,W,s,s_0);
            return m;

        }
        catch (IOException | NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    private computeC()
    {
        ep_t temp = new ep_t();
        //COMPUTE C
        //C = K
        Relic.INSTANCE.ep_copy(C,K);
        //temp = S^k
        Relic.INSTANCE.ep_mul_monty(temp,S,kappa);
        //C = K S^k
        Relic.INSTANCE.ep_add_basic(C,C,temp);
        //temp = S0^k0
        Relic.INSTANCE.ep_mul_monty(temp,S_zero,privkey.getk_zero());
        //C = K S^k S0^k0
        Relic.INSTANCE.ep_add_basic(C,C,temp);

        List<ep_t> signed_attribute_list = attributes.getSignedAttributeList();
        List<bn_t> unsigned_attribute_list = attributes.getAttributeList();

        for(int i=0;i<signed_attribute_list.size();++i){
            //temp = Si^ki
            Relic.INSTANCE.ep_mul_monty(temp,signed_attribute_list.get(i),unsigned_attribute_list.get(i));
            //C = K S^k S0^k0 S1^k1 ... Sn^kn
            Relic.INSTANCE.ep_add_basic(C,C,temp);
        }

    }


    public UserShowCredentialFirstMessage createUserShowCredentialFirstMessage()
    {

    }

}
