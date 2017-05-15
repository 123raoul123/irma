package irma;

import Issue.*;
import relic.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.ByteArrayOutputStream;


/**
 * Created by raoul on 06/05/2017.
 */
public class User {

    private UserPrivateKey privkey;
    private bn_t kappa_p;// p is for '
    private ep_t S, S_zero;

    public User(UserPrivateKey privkey)
    {
        this.privkey = privkey;
        this.kappa_p = new bn_t();
        this.S = new ep_t();
        this.S_zero = new ep_t();
    }

    public FirstUserMessage sendFirsUserMessage(FirstIssuerMessage message){
        bn_t alpha = new bn_t();
        ep_t R = new ep_t();
        bn_t ord = new bn_t();

        ep_t W = new ep_t();
        byte[] hash = new byte[32];

        Relic.INSTANCE.ep_curve_get_ord(ord);
        Relic.INSTANCE.bn_rand_mod(alpha,ord);
        Relic.INSTANCE.bn_rand_mod(kappa_p,ord);

        Relic.INSTANCE.ep_mul_monty(S,message.getS_bar(),alpha);
        Relic.INSTANCE.ep_mul_monty(S_zero,message.getS_zero_bar(),alpha);

        ep_t R_left = new ep_t();
        Relic.INSTANCE.ep_mul_monty(R_left,S,kappa_p);

        ep_t R_right = new ep_t();
        Relic.INSTANCE.ep_mul_monty(R_right,S_zero,privkey.getk_zero());
        Relic.INSTANCE.ep_add_basic(R,R_left,R_right);

        //Create W = S^w S_0^w_0
        bn_t w = new bn_t();
        bn_t w_0 = new bn_t();
        Relic.INSTANCE.bn_rand_mod(w,ord);
        Relic.INSTANCE.bn_rand_mod(w_0,ord);

        ep_t W_left = new ep_t();
        Relic.INSTANCE.ep_mul_monty(W_left,S,w);

        ep_t W_right = new ep_t();
        Relic.INSTANCE.ep_mul_monty(W_right,S_zero,w_0);
        Relic.INSTANCE.ep_add_basic(W,W_left,R_right);

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
            bn_t temp_s = new bn_t();
            bn_t s = new bn_t();
            Relic.INSTANCE.bn_mul_karat(temp_s,c,kappa_p);
            Relic.INSTANCE.bn_add(s,temp_s,w);
            Relic.INSTANCE.bn_mod_basic(s, s, ord);

            //Create s_0 = ck0 + w0
            bn_t temp_s_0 = new bn_t();
            bn_t s_0 = new bn_t();
            Relic.INSTANCE.bn_mul_karat(temp_s_0,c,privkey.getk_zero());
            Relic.INSTANCE.bn_add(s_0,temp_s_0,w_0);
            Relic.INSTANCE.bn_mod_basic(s_0, s_0, ord);

//            System.out.printf("R = %s\n", R.toString().substring(0));
//            System.out.printf("W = %s\n", W.toString().substring(0));
//            System.out.printf("S = %s\n", S.toString().substring(0));
//            System.out.printf("s = %s\n", s.toString().substring(0));
//            System.out.printf("S0 = %s\n", S_zero.toString().substring(0));
//            System.out.printf("s0 = %s\n", s_0.toString().substring(0));

            FirstUserMessage m = new FirstUserMessage(S,S_zero,R,W,s,s_0);
            return m;

        }
        catch (IOException | NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }
}
