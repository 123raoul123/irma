package irma;
import Issue.FirstIssuerMessage;
import Issue.FirstUserMessage;
import Issue.SecondIssuerMessage;
import relic.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class Issuer {

    private PrivateKey privkey;
    private byte[] nonce;

    public Issuer(PrivateKey privkey)
    {
        this.privkey = privkey;
        this.nonce = new byte[16];
    }

    public void send_SecondIssuerMessage(FirstUserMessage message)
    {
        //Convert R and W to bytes
        byte[] R_byte = new byte[1000];
        byte[] W_byte = new byte[1000];
        Relic.INSTANCE.ep_write_bin(R_byte,1000,message.getR(),0);
        Relic.INSTANCE.ep_write_bin(W_byte,1000,message.getW(),0);

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
            Relic.INSTANCE.ep_mul_monty(res,message.getR(),c);
            Relic.INSTANCE.ep_add_basic(res,res,message.getW());

            ep_t temp_1 = new ep_t();
            ep_t temp_2 = new ep_t();
            Relic.INSTANCE.ep_mul_monty(temp_1,message.getS(),message.get_Small_s());
            Relic.INSTANCE.ep_mul_monty(temp_2,message.getS_zero(),message.get_small_s_zero());
            ep_t res_1 = new ep_t();
            Relic.INSTANCE.ep_add_basic(res_1,temp_1,temp_2);

//            System.out.printf("R = %s\n", message.getR().toString().substring(0));
//            System.out.printf("W = %s\n", message.getW().toString().substring(0));
//            System.out.printf("S = %s\n", message.getS().toString().substring(0));
//            System.out.printf("s = %s\n", message.get_Small_s().toString().substring(0));
//            System.out.printf("S0 = %s\n", message.getS_zero().toString().substring(0));
//            System.out.printf("s0 = %s\n", message.get_small_s_zero().toString().substring(0));

            if(Relic.INSTANCE.ep_cmp(res,res_1) == 0)
            {
                System.out.print("Yay");
            }
            else
            {
                throw new RuntimeException("Proof verification failed :(");
            }

        }
        catch (IOException | NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    public FirstIssuerMessage send_FirstIssuerMessage()
    {
        ep_t K_bar = new ep_t();
        ep_t S_bar = new ep_t();
        ep_t S_zero_bar = new ep_t();

        SecureRandom rand = new SecureRandom();
        rand.nextBytes(this.nonce);

        Relic.INSTANCE.ep_rand(K_bar);
        Relic.INSTANCE.ep_mul_monty(S_bar,K_bar,privkey.geta());
        Relic.INSTANCE.ep_mul_monty(S_zero_bar,K_bar,privkey.geta_list().get(0));

        FirstIssuerMessage message = new FirstIssuerMessage(S_bar,S_zero_bar,this.nonce);

        return message;
    }

}
