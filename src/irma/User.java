package irma;

import Issue.*;
import ShowCredential.UserShowCredentialFirstMessage;
import ShowCredential.UserShowCredentialSecondMessage;
import ShowCredential.VerifierShowCredentialFirstMessage;
import relic.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by raoul on 06/05/2017.
 */
public class User {

    private UserPrivateKey privkey;
    private bn_t kappa_p,kappa;// p is for '
    private ep_t S, S_zero,K,C,T,D;
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
        T = new ep_t();
        D = new ep_t();
        attributes = at;
    }

    public UserIssueFirstMessage createUserIssueFirstMessage()
    {
        UserIssueFirstMessage mes = new UserIssueFirstMessage(attributes.getAttributes());
        return mes;
    }

    public UserIssueSecondMessage createUserIssueSecondMessage(IssuerIssueFirstMessage message){
        bn_t alpha = new bn_t(),ord = new bn_t();
        ep_t R = new ep_t(),W = new ep_t();

        //generate random k' and alpha
        Relic.INSTANCE.ep_curve_get_ord(ord);
        Relic.INSTANCE.bn_rand_mod(alpha,ord);
        Relic.INSTANCE.bn_rand_mod(kappa_p,ord);

        //S = S_bar^alpha
        Relic.INSTANCE.ep_mul_monty(S,message.getS_bar(),alpha);

        //S_zero = S_zero_bar^alpha
        Relic.INSTANCE.ep_mul_monty(S_zero,message.getS_zero_bar(),alpha);

        ep_t left = new ep_t(),right = new ep_t();

        //R = S^k' S_zero^k_0
        Relic.INSTANCE.ep_mul_monty(left,S,kappa_p);
        Relic.INSTANCE.ep_mul_monty(right,S_zero,privkey.getk_zero());

        //add left and right to obtain R
        Relic.INSTANCE.ep_add_basic(R,left,right);

        //Create W = S^w S_0^w_0

        //random w and w_0
        bn_t w = new bn_t(),w_0 = new bn_t();
        Relic.INSTANCE.bn_rand_mod(w,ord);
        Relic.INSTANCE.bn_rand_mod(w_0,ord);


        Relic.INSTANCE.ep_mul_monty(left,S,w);
        Relic.INSTANCE.ep_mul_monty(right,S_zero,w_0);

        //add left and right to obtain W
        Relic.INSTANCE.ep_add_basic(W,left,right);

        //Convert R and W to bytes
        byte[] R_byte = new byte[1000],W_byte = new byte[1000];
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
            byte[] hash;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(concat);
            bn_t c = new bn_t(),temp = new bn_t();
            Relic.INSTANCE.bn_read_bin(c,hash,hash.length);

            //Create s = ck'+w
            bn_t s = new bn_t();
            Relic.INSTANCE.bn_mul_karat(temp,c,kappa_p);
            Relic.INSTANCE.bn_add(s,temp,w);
            Relic.INSTANCE.bn_mod_basic(s, s, ord);

            //Create s_0 = ck0 + w0
            bn_t s_0 = new bn_t();
            Relic.INSTANCE.bn_mul_karat(temp,c,privkey.getk_zero());
            Relic.INSTANCE.bn_add(s_0,temp,w_0);
            Relic.INSTANCE.bn_mod_basic(s_0, s_0, ord);


//            System.out.printf("c = %s\n", c.toString().substring(0));
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

    private void computeC()
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

        List<ep_t> signed_attribute_list = attributes.getBasePoints();
        List<bn_t> unsigned_attribute_list = attributes.getAttributes();

        for(int i=0;i<signed_attribute_list.size();++i){
            //temp = Si^ki
            Relic.INSTANCE.ep_mul_monty(temp,signed_attribute_list.get(i),unsigned_attribute_list.get(i));
            //C = K S^k S0^k0 S1^k1 ... Sn^kn
            Relic.INSTANCE.ep_add_basic(C,C,temp);
        }

    }

    public void setAttributes(IssuerIssueSecondMessage message)
    {
        // k = k' + k''
        Relic.INSTANCE.bn_add(kappa,kappa_p,message.getKappa_pp());
        // set K
        Relic.INSTANCE.ep_copy(K,message.getK());
        // set T
        Relic.INSTANCE.ep_copy(T,message.getT());
        // store signed attributes
        attributes.setBasePoints(message.getSigned_attribute_list());
        //compute C
        computeC();

    }

    public UserShowCredentialFirstMessage createUserShowCredentialFirstMessage(List<Boolean> disclosed)
    {
        Map<Integer,bn_t> disclosed_attribute_list = new HashMap<>();
        List<bn_t> attribute_list = attributes.getAttributes();

        for(int i=0; i<disclosed.size();++i)
        {
            if(disclosed.get(i))
            {
                disclosed_attribute_list.put(i,attribute_list.get(i));
            }
        }

        UserShowCredentialFirstMessage m = new UserShowCredentialFirstMessage(disclosed_attribute_list,disclosed);
        return m;
    }


    public UserShowCredentialSecondMessage createUserShowCredentialSecondMessage(VerifierShowCredentialFirstMessage message, List<Boolean> disclosed)
    {
        //Generate rand alpha and beta
        bn_t alpha = new bn_t(),beta = new bn_t(),ord = new bn_t();
        Relic.INSTANCE.ep_curve_get_ord(ord);
        Relic.INSTANCE.bn_rand_mod(alpha,ord);
        Relic.INSTANCE.bn_rand_mod(beta,ord);

        //Multiply everything with alpha
        ep_t K_blind = new ep_t(),S_blind = new ep_t(),S_zero_blind = new ep_t(),ep_temp = new ep_t();
        Relic.INSTANCE.ep_mul_monty(K_blind,K,alpha);
        Relic.INSTANCE.ep_mul_monty(S_blind,S,alpha);
        Relic.INSTANCE.ep_mul_monty(S_zero_blind,S_zero,alpha);

        List<ep_t> basepoints = attributes.getBasePoints();
        List<ep_t> blindedBasepoints = new ArrayList<>();

        for(ep_t S_i: basepoints){
            ep_t blinded = new ep_t();
            Relic.INSTANCE.ep_mul_monty(blinded,S_i,alpha);
            blindedBasepoints.add(blinded);
        }

        //C_blind = C^(-alpha/beta), T_blind = T^(-alpha/beta)
        bn_t tmp = new bn_t(),inverse = new bn_t();
        ep_t C_blind = new ep_t(), T_blind = new ep_t();

        Relic.INSTANCE.bn_gcd_ext_basic(tmp, inverse, null, beta, ord);
        Relic.INSTANCE.bn_neg(tmp,alpha);
        Relic.INSTANCE.bn_mul_karat(tmp,tmp,inverse);


        Relic.INSTANCE.ep_mul_monty(C_blind,C,tmp);
        Relic.INSTANCE.ep_mul_monty(T_blind,T,tmp);

        /*******************************************

                COMPUTE D

         *******************************************/

        Relic.INSTANCE.ep_neg_basic(D,K_blind);
        List<bn_t> attributes = this.attributes.getAttributes();

        for(int i=0;i<disclosed.size();++i)
        {
            if(disclosed.get(i))
            {
//                System.out.printf("i = %d\n", i);
                // S_i^(-k_i)
                Relic.INSTANCE.bn_neg(tmp,attributes.get(i));
                Relic.INSTANCE.ep_mul_monty(ep_temp,blindedBasepoints.get(i),tmp);
                // Add to D
                Relic.INSTANCE.ep_add_basic(D,D,ep_temp);
            }
        }

//        System.out.printf("K_blind = %s\n", K_blind.toString().substring(0));
//        System.out.printf("D = %s\n", D.toString().substring(0));

        /*******************************************

                SCHNORR PART

         *******************************************/

        // COMPUTE W
        // set random w's
        bn_t w_beta = new bn_t(), w = new bn_t(), w0 = new bn_t();
        Map<Integer,bn_t> w_list = new HashMap<>();
        Relic.INSTANCE.bn_rand_mod(w_beta,ord);
        Relic.INSTANCE.bn_rand_mod(w,ord);
        Relic.INSTANCE.bn_rand_mod(w0,ord);

        ep_t W = new ep_t();
        Relic.INSTANCE.ep_mul_monty(W,C_blind,w_beta);
        Relic.INSTANCE.ep_mul_monty(ep_temp,S_blind,w);
        Relic.INSTANCE.ep_add_basic(W,W,ep_temp);
        Relic.INSTANCE.ep_mul_monty(ep_temp,S_zero_blind,w0);
        Relic.INSTANCE.ep_add_basic(W,W,ep_temp);

        for(int i=0;i<disclosed.size();++i)
        {
            if(!disclosed.get(i))
            {
                bn_t temp = new bn_t();
                Relic.INSTANCE.bn_rand_mod(temp,ord);
                Relic.INSTANCE.ep_mul_monty(ep_temp,blindedBasepoints.get(i),tmp);
                Relic.INSTANCE.ep_add_basic(W,W,ep_temp);

                w_list.put(i,temp);
            }
        }

        //COMPUTE C AND ALL SMALL s

        //Convert D and W to bytes
        byte[] D_byte = new byte[1000],W_byte = new byte[1000];
        Relic.INSTANCE.ep_write_bin(D_byte,1000,D,0);
        Relic.INSTANCE.ep_write_bin(W_byte,1000,W,0);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try
        {
            //Concat ETA (Nonce) W and D
            outputStream.write(message.getNonce());
            outputStream.write(W_byte);
            outputStream.write(D_byte);
            byte concat[] = outputStream.toByteArray();


            //HASH RESULT AND CONVERT TO BN_T
            byte[] hash;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(concat);
            bn_t c = new bn_t();
            Relic.INSTANCE.bn_read_bin(c,hash,hash.length);

            //Create s_beta = cbeta+w
            bn_t s_beta = new bn_t();
            Relic.INSTANCE.bn_mul_karat(tmp,c,beta);
            Relic.INSTANCE.bn_add(s_beta,tmp,w_beta);
//            Relic.INSTANCE.bn_mod_basic(s, s, ord);

            //Create s = ck + w
            bn_t s = new bn_t();
            Relic.INSTANCE.bn_mul_karat(tmp,c,kappa);
            Relic.INSTANCE.bn_add(s,tmp,w);
//            Relic.INSTANCE.bn_mod_basic(s_0, s_0, ord);

            //Create s0 = ck0 + w0
            bn_t s0 = new bn_t();
            Relic.INSTANCE.bn_mul_karat(tmp,c,privkey.getk_zero());
            Relic.INSTANCE.bn_add(s0,tmp,w0);

            Map<Integer,bn_t> s_list = new HashMap<>();
            for(int i=0;i<disclosed.size();++i)
            {
                if(!disclosed.get(i))
                {
                    bn_t temp = new bn_t();
                    Relic.INSTANCE.bn_mul_karat(tmp,c,attributes.get(i));
                    Relic.INSTANCE.bn_add(temp,tmp,w_list.get(i));
                    s_list.put(i,temp);
                }
            }

//            System.out.printf("c = %s\n", c.toString().substring(0));
//            System.out.printf("D = %s\n", D.toString().substring(0));
//            System.out.printf("K_blind = %s\n", K_blind.toString().substring(0));
//            System.out.printf("S_blind = %s\n", S_blind.toString().substring(0));
//            System.out.printf("S_zero_blind = %s\n", S_zero_blind.toString().substring(0));
//            for(int i=0;i<blindedBasepoints.size();++i)
//            {
//                System.out.printf("element = %s\n", blindedBasepoints.get(i).toString().substring(0));
//            }
//            System.out.printf("C_blind = %s\n", C_blind.toString().substring(0));
//            System.out.printf("T_blind = %s\n", T_blind.toString().substring(0));
//            System.out.printf("W = %s\n", W.toString().substring(0));
//            System.out.printf("s_beta = %s\n", s_beta.toString().substring(0));
//            System.out.printf("s = %s\n", s.toString().substring(0));
//            System.out.printf("s0 = %s\n", s0.toString().substring(0));
//            for(int i=0;i<disclosed.size();++i)
//            {
//                if(!disclosed.get(i)) {
//                    System.out.printf("element = %s\n", s_list.get(i).toString().substring(0));
//                }
//            }


            UserShowCredentialSecondMessage m = new UserShowCredentialSecondMessage(K_blind,S_blind,S_zero_blind,
                    blindedBasepoints,C_blind,T_blind,W,s_beta,s,s0,s_list);
            return m;

        }
        catch (IOException | NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }

    }

}
