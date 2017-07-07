package irma;

import Issue.*;
import ShowCredential.*;
import relic.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * User class
 * The user class is quite big. It is used to perform the Issuance and the ShowCredential protocol
 */
public class User {

    private UserPrivateKey privkey;
    private bn_t kappa_p,kappa;// p is for '
    private ep_t S, S0,K,C,T,D;
    private Attributes attributes;

    public User(UserPrivateKey privkey, Attributes at)
    {
        this.privkey = privkey;
        kappa_p = new bn_t();
        kappa = new bn_t();
        S = new ep_t();
        S0 = new ep_t();
        K = new ep_t();
        C = new ep_t();
        T = new ep_t();
        D = new ep_t();
        attributes = at;
    }

    /**
     * This method is the first part of the issuance protocol
     *
     * @return the user provides the issuer with his attributes
     */
    public IssueRequestMessage createIssueRequestMessage()
    {
        IssueRequestMessage mes = new IssueRequestMessage(attributes.getAttributes());
        return mes;
    }

    /**
     * This method is the second part of the issuance protocol
     * @param message Message received by issuer containing S_bar, S0_bar and nonce
     * @return S,S0 and R. Plus W and s,s0 required for the proof
     */
    public IssueCommitmentMessage createIssueCommitmentMessage(IssueResponseMessage message){
        bn_t alpha = new bn_t(),ord = new bn_t();
        ep_t R = new ep_t(),W = new ep_t();

        /*******************************************

         Choose random alpha and kappa' which is called kappa_p

         *******************************************/
        Relic.INSTANCE.ep_curve_get_ord(ord);
        Relic.INSTANCE.bn_rand_mod(alpha,ord);
        Relic.INSTANCE.bn_rand_mod(kappa_p,ord);

        /*******************************************

         Set S = S_bar^alpha and S0 = S0_bar^alpha

         *******************************************/
        Relic.INSTANCE.ep_mul_monty(S,message.getS_bar(),alpha);
        Relic.INSTANCE.ep_mul_monty(S0,message.getS0_bar(),alpha);

        /*******************************************

         R = S^k' S0^k_0

         *******************************************/
        ep_t left = new ep_t(),right = new ep_t();
        Relic.INSTANCE.ep_mul_monty(left,S,kappa_p);
        Relic.INSTANCE.ep_mul_monty(right, S0,privkey.getk0());

        //add left and right to obtain R
        Relic.INSTANCE.ep_add_basic(R,left,right);

        /*******************************************

         Create W = S^w S0^w0

         *******************************************/
        //random w and w0
        bn_t w = new bn_t(),w0 = new bn_t();
        Relic.INSTANCE.bn_rand_mod(w,ord);
        Relic.INSTANCE.bn_rand_mod(w0,ord);


        Relic.INSTANCE.ep_mul_monty(left,S,w);
        Relic.INSTANCE.ep_mul_monty(right, S0,w0);

        //add left and right to obtain W
        Relic.INSTANCE.ep_add_basic(W,left,right);

        /*******************************************

         Create c for proof of knowledge and create s and s0

         *******************************************/
        bn_t c = Attributes.hashAndConvert(message.getNonce(),W,R);
        bn_t temp = new bn_t();

        //Create s = ck'+ w
        bn_t s = new bn_t();
        Relic.INSTANCE.bn_mul_karat(temp,c,kappa_p);
        Relic.INSTANCE.bn_add(s,temp,w);

        //Create s0 = ck0 + w0
        bn_t s0 = new bn_t();
        Relic.INSTANCE.bn_mul_karat(temp,c,privkey.getk0());
        Relic.INSTANCE.bn_add(s0,temp,w0);

        IssueCommitmentMessage m = new IssueCommitmentMessage(S, S0,R,W,s,s0);
        return m;
    }

    /**
     * This method is called after
     * the signature is received to store and compute the signature
     * @param message message containing the signature send by Issuer
     */
    public void setSignature(IssueSignatureMessage message)
    {
        // k = k' + k''
        Relic.INSTANCE.bn_add(kappa,kappa_p,message.getKappa_pp());
        // set K
        Relic.INSTANCE.ep_copy(K,message.getK());
        // set T
        Relic.INSTANCE.ep_copy(T,message.getT());
        // store signed attributes
        attributes.setBasePoints(message.getBasePoints());
        //compute C
        computeC();
    }

    /**
     * This method is called after
     * the signature is received to compute C
     */
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
        Relic.INSTANCE.ep_mul_monty(temp, S0,privkey.getk0());
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


    /**
     * This method is called to start the ShowCredential protocol
     * @param disclosed boolean list indicating if the attribute is disclosed or not
     * @return the disclosed attributes that will be send to the verifier
     */
    public ShowCredentialRequestMessage createShowCredentialRequestMessage(List<Boolean> disclosed)
    {
        Map<Integer,bn_t> disclosed_attribute_list = new HashMap<>();
        List<bn_t> attribute_list = attributes.getAttributes();

        for(int i=0; i<disclosed.size();++i)
            if(disclosed.get(i))
                disclosed_attribute_list.put(i,attribute_list.get(i));

        ShowCredentialRequestMessage m = new ShowCredentialRequestMessage(disclosed_attribute_list,disclosed);
        return m;
    }

    /**
     * This method is called to create the proof to the verifier
     * @param first user message containing the disclosed attributes
     * @param message contains the nonce of the verifier
     * @param disclosed boolean list indicating if the attribute is disclosed or not
     * @return the disclosed attributes that will be send to the verifier
     */
    public ShowCredentialCommitmentMessage createShowCredentialCommitmentMessage(ShowCredentialRequestMessage first, ShowCredentialResponseMessage message, List<Boolean> disclosed)
    {
        /*******************************************

         MASK K,S,S0,...,Sn with alpha

         *******************************************/
        //Generate rand alpha
        bn_t alpha = new bn_t(),ord = new bn_t();
        Relic.INSTANCE.ep_curve_get_ord(ord);
        Relic.INSTANCE.bn_rand_mod(alpha,ord);

        //Multiply everything with alpha
        ep_t K_blind = new ep_t(),S_blind = new ep_t(),S_zero_blind = new ep_t(),ep_temp = new ep_t();
        Relic.INSTANCE.ep_mul_monty(K_blind,K,alpha);
        Relic.INSTANCE.ep_mul_monty(S_blind,S,alpha);
        Relic.INSTANCE.ep_mul_monty(S_zero_blind, S0,alpha);

        List<ep_t> basepoints = attributes.getBasePoints();
        List<ep_t> blindedBasepoints = new ArrayList<>();

        for(ep_t S_i: basepoints){
            ep_t blinded = new ep_t();
            Relic.INSTANCE.ep_mul_monty(blinded,S_i,alpha);
            blindedBasepoints.add(blinded);
        }

        /*******************************************

         MASK C and T with alpha and beta
         C_blind = C^(-alpha/beta), T_blind = T^(-alpha/beta)

         *******************************************/
        bn_t tmp = new bn_t(),beta = new bn_t(),inverse = new bn_t();
        ep_t C_blind = new ep_t(), T_blind = new ep_t();
        Relic.INSTANCE.bn_rand_mod(beta,ord);

        Relic.INSTANCE.bn_gcd_ext_basic(tmp, inverse, null, beta, ord);
        Relic.INSTANCE.bn_neg(tmp,alpha);
        Relic.INSTANCE.bn_mul_karat(tmp,tmp,inverse);

        Relic.INSTANCE.ep_mul_monty(C_blind,C,tmp);
        Relic.INSTANCE.ep_mul_monty(T_blind,T,tmp);

        /*******************************************

         Compute D

         *******************************************/
        D = Attributes.compute_D(K_blind, blindedBasepoints, first.getDisclosedAttributes());

        /*******************************************

         Compute W

         *******************************************/
        bn_t w_beta = new bn_t(), w = new bn_t(), w0 = new bn_t();
        Map<Integer,bn_t> w_list = new HashMap<>();
        Relic.INSTANCE.bn_rand_mod(w_beta,ord);
        Relic.INSTANCE.bn_rand_mod(w,ord);
        Relic.INSTANCE.bn_rand_mod(w0,ord);

        for(int i=0; i<disclosed.size(); ++i) {
            if (!disclosed.get(i)) {
                bn_t temp = new bn_t();
                Relic.INSTANCE.bn_rand_mod(temp, ord);
                w_list.put(i, temp);
            }
        }

        ep_t W = Attributes.computeDLRepresentation(C_blind, S_blind, S_zero_blind,
                blindedBasepoints, w_beta, w, w0, w_list);

        /*******************************************

         Compute c,s,s0,sBeta and sList

         *******************************************/
        bn_t c = Attributes.hashAndConvert(message.getNonce(),W,D);

        //Create sBeta = cbeta+w
        bn_t sBeta = new bn_t();
        Relic.INSTANCE.bn_mul_karat(tmp,c,beta);
        Relic.INSTANCE.bn_add(sBeta,tmp,w_beta);

        //Create s = ck + w
        bn_t s = new bn_t();
        Relic.INSTANCE.bn_mul_karat(tmp,c,kappa);
        Relic.INSTANCE.bn_add(s,tmp,w);

        //Create s0 = ck0 + w0
        bn_t s0 = new bn_t();
        Relic.INSTANCE.bn_mul_karat(tmp,c,privkey.getk0());
        Relic.INSTANCE.bn_add(s0,tmp,w0);

        List<bn_t> attributes = this.attributes.getAttributes();
        Map<Integer,bn_t> sList = new HashMap<>();
        for(int i=0;i<disclosed.size();++i)
        {
            if(!disclosed.get(i))
            {
                bn_t temp = new bn_t();
                Relic.INSTANCE.bn_mul_karat(tmp,c,attributes.get(i));
                Relic.INSTANCE.bn_add(temp,tmp,w_list.get(i));
                sList.put(i,temp);
            }
        }

        /*******************************************

         return message

         *******************************************/
        ShowCredentialCommitmentMessage m = new ShowCredentialCommitmentMessage(K_blind,S_blind,S_zero_blind,
                blindedBasepoints,C_blind,T_blind,W,s,s0,sBeta,sList);

        return m;

    }

}
