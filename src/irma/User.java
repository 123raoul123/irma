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
    private ep_t S,S_zero,W;
    private byte[] hash;

    public User(UserPrivateKey privkey)
    {
        this.privkey = privkey;
        this.kappa_p = new bn_t();
        this.S = new ep_t();
        this.S_zero = new ep_t();
        this.W = new ep_t();
        this.hash = new byte[32];

    }

    public FirstUserMessage sendFirsUserMessage(FirstIssuerMessage message){
        bn_t alpha = new bn_t();
        ep_t R = new ep_t();
        bn_t ord = new bn_t();

        Relic.INSTANCE.ep_curve_get_ord(ord);
        Relic.INSTANCE.bn_rand_mod(alpha,ord);
        Relic.INSTANCE.bn_rand_mod(this.kappa_p,ord);

        Relic.INSTANCE.ep_mul_monty(this.S,message.getS_bar(),alpha);
        Relic.INSTANCE.ep_mul_monty(this.S_zero,message.getS_zero_bar(),alpha);

        ep_t R_left = new ep_t();
        Relic.INSTANCE.ep_mul_monty(R_left,this.S,this.kappa_p);

        ep_t R_right = new ep_t();
        Relic.INSTANCE.ep_mul_monty(R_right,this.S_zero,privkey.getk_zero());
        Relic.INSTANCE.ep_add_basic(R,R_left,R_right);


        //Create W = S^w S_0^w_0
        bn_t w = new bn_t();
        bn_t w_0 = new bn_t();
        Relic.INSTANCE.bn_rand_mod(w,ord);
        Relic.INSTANCE.bn_rand_mod(w_0,ord);

        ep_t W_left = new ep_t();
        Relic.INSTANCE.ep_mul_monty(W_left,this.S,w);

        ep_t W_right = new ep_t();
        Relic.INSTANCE.ep_mul_monty(W_right,this.S_zero,w_0);
        Relic.INSTANCE.ep_add_basic(this.W,W_left,R_right);

        //Convert R and W to bytes
        byte[] R_byte = new byte[1000];
        byte[] W_byte = new byte[1000];
        Relic.INSTANCE.ep_write_bin(R_byte,1000,R,0);
        Relic.INSTANCE.ep_write_bin(W_byte,1000,this.W,0);

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
            this.hash = digest.digest(concat);
            bn_t c = new bn_t();
            Relic.INSTANCE.bn_read_bin(c,this.hash,hash.length);

            //Create s = ck'+w
            bn_t temp_s = new bn_t();
            bn_t s = new bn_t();
            Relic.INSTANCE.bn_mul_karat(temp_s,c,this.kappa_p);
            Relic.INSTANCE.bn_add(s,temp_s,w);

            //Create s_0 = ck0 + w0
            bn_t temp_s_0 = new bn_t();
            bn_t s_0 = new bn_t();
            Relic.INSTANCE.bn_mul_karat(temp_s_0,c,privkey.getk_zero());
            Relic.INSTANCE.bn_add(s_0,temp_s_0,w_0);



            FirstUserMessage m = new FirstUserMessage(this.S,this.S_zero,R,W,s,s_0);
            return m;

        }
        catch (IOException | NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        FirstUserMessage m = new FirstUserMessage();
        return m;
    }
}
