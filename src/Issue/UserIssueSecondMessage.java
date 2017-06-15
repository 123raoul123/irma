package Issue;

import relic.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by raoul on 30/04/2017.
 */
public class UserIssueSecondMessage {

    private ep_t S, S0,R,W;
    private bn_t s, s0;


    public UserIssueSecondMessage(ep_t S, ep_t S0, ep_t R, ep_t W)
    {
        this.S = new ep_t();
        this.S0 = new ep_t();
        this.R = new ep_t();
        this.W = new ep_t();

        Relic.INSTANCE.ep_copy(this.S,S);
        Relic.INSTANCE.ep_copy(this.S0, S0);
        Relic.INSTANCE.ep_copy(this.R,R);
        Relic.INSTANCE.ep_copy(this.W,W);

        this.s = new bn_t();
        this.s0 = new bn_t();
    }

    public bn_t gets0()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy, s0);
        return copy;
    }

    public void sets0(bn_t s0)
    {
        Relic.INSTANCE.bn_copy(this.s0, s0);
    }

    public bn_t gets()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy,s);
        return copy;
    }

    public void sets(bn_t s)
    {
        Relic.INSTANCE.bn_copy(this.s, s);
    }

    public ep_t getW()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,W);
        return copy;
    }

    public ep_t getS()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,S);
        return copy;
    }

    public ep_t getS0()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy, S0);
        return copy;
    }

    public ep_t getR()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,R);
        return copy;
    }

    /**
     * This method is used to hash H(R,W,Nonce) and convert to bn_t which we call c
     * Nonce provided by Issuer
     * @return bn_t c which is H(R,W,Nonce) converted to bn_t
     */
    public bn_t hashAndConvert(byte[] nonce)
    {
        //Convert R and W to bytes
        byte[] R_byte = new byte[1000];
        byte[] W_byte = new byte[1000];
        Relic.INSTANCE.ep_write_bin(R_byte,1000, R,0);
        Relic.INSTANCE.ep_write_bin(W_byte,1000, W,0);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try
        {
            //Concat ETA (Nonce) W and R
            outputStream.write(nonce);
            outputStream.write(W_byte);
            outputStream.write(R_byte);
            byte concat[] = outputStream.toByteArray();


            //HASH RESULT AND CONVERT TO BN_T
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(concat);
            bn_t c = new bn_t();
            Relic.INSTANCE.bn_read_bin(c, hash, hash.length);
            return c;
        }
        catch (IOException | NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong :(\n");
        }
    }

}
