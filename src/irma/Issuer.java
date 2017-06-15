package irma;
import Issue.*;
import relic.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the Issuer it has a secret key
 * and is able to perform the issuance protocol
 */
public class Issuer {

    private IssuerPrivateKey privkey;
    private byte[] nonce;
    private ep_t S_bar, S0_bar;

    public Issuer(IssuerPrivateKey privkey)
    {
        this.privkey = privkey;
        nonce = new byte[16];
        S_bar = new ep_t();
        S0_bar = new ep_t();
    }

    /**
     * This method is the first part of the issuance protocol
     *
     * @return the issuer provides the user with S_bar, S0_bar and nonce
     */
    public IssueResponseMessage createFirstIssuerMessage()
    {
        //Generate nonce for schnorr
        SecureRandom rand = new SecureRandom();
        rand.nextBytes(this.nonce);

        //Choose random K_bar from G_1
        ep_t K_bar = new ep_t();
        Relic.INSTANCE.ep_rand(K_bar);

        //S_bar = K_bar^a
        Relic.INSTANCE.ep_mul_monty(S_bar,K_bar,privkey.geta());

        //S0_bar = K_bar^a_0
        Relic.INSTANCE.ep_mul_monty(S0_bar,K_bar,privkey.getaList().get(0));

        IssueResponseMessage message = new IssueResponseMessage(S_bar, S0_bar,this.nonce);

        return message;
    }

    /**
     * This method is the last part of the issuance protocol
     *              if all the checks are valid the Issuer will create a signature
     * @param first first user message
     * @param second second user message
     * @return message containing the signature of the user
     */
    public IssueSignatureMessage createSecondIssuerMessage(IssueRequestMessage first, IssueCommitmentMessage second)
    {

        /*******************************************

         Validate Proof of knowledge

         *******************************************/
        if(validateR(second))
            System.out.print("Yay Proof verification succeeded\n");
        else
            throw new RuntimeException("Proof verification failed :(\n");

        /*******************************************

         Verify that S != S_bar

         *******************************************/
        if(Relic.INSTANCE.ep_cmp(S_bar,second.getS()) != 0)
            System.out.print("Yay S != S_bar\n");
        else
            throw new RuntimeException("Proof verification failed :(\n");

        /*******************************************

         Set K = S^(1/a)

         *******************************************/
        bn_t  inverse = new bn_t(), ord = new bn_t(), tmp = new bn_t();
        ep_t K = new ep_t(), res = new ep_t();
        Relic.INSTANCE.ep_curve_get_ord(ord); // Get the group order
        Relic.INSTANCE.bn_gcd_ext_basic(tmp, inverse, null, privkey.geta(), ord);
        Relic.INSTANCE.ep_mul_monty(K,second.getS(),inverse);

        /*******************************************

         Verify that K = S0^(1/a0)

         *******************************************/
        Relic.INSTANCE.bn_gcd_ext_basic(tmp, inverse, null, privkey.getaList().get(0), ord);
        Relic.INSTANCE.ep_mul_monty(res,second.getS0(),inverse);

        if(Relic.INSTANCE.ep_cmp(K,res) == 0)
            System.out.print("Yay K = S0^(1/a0)\n");
        else
            throw new RuntimeException("Proof verification failed :(\n");

        /*******************************************

         Choose kappa'' from Z_p
                                kappa'' = kappa_pp
         *******************************************/
        bn_t kappa_pp = new bn_t();
        Relic.INSTANCE.bn_rand_mod(kappa_pp,ord);

        /*******************************************

         Set Si = K^(ai) where i [1..n]

         *******************************************/
        List<ep_t> basePoints = new ArrayList<>();
        for(int i = 1; i<privkey.getaList().size(); ++i)
        {
            ep_t bla = new ep_t();
            Relic.INSTANCE.ep_mul_monty(bla,K,privkey.getaList().get(i));
            basePoints.add(bla);
        }

        /*******************************************

         Set T

         *******************************************/
        ep_t T = new ep_t();
        Relic.INSTANCE.ep_mul_monty(res,second.getS(),kappa_pp);
        Relic.INSTANCE.ep_add_basic(T,K,res);
        Relic.INSTANCE.ep_add_basic(T,T,second.getR());

        for(int i=0; i<basePoints.size();++i)
        {
            Relic.INSTANCE.ep_mul_monty(res,basePoints.get(i),first.getAttributes().get(i));
            Relic.INSTANCE.ep_add_basic(T,T,res);
        }

        Relic.INSTANCE.ep_mul_monty(T,T,privkey.getz());

        IssueSignatureMessage mes = new IssueSignatureMessage(kappa_pp,K, basePoints,T);

        return mes;
    }


    /**
     * This method is used to verify kappa' and k_0
     *             this is checked by verifying
     *                      R^c W = (S^s) (S_0^s_0)
     * @param second message that needs to be verified
     * @return boolean returns true if the statement R^c W = (S^s) (S_0^s_0) is true
     */
    private boolean validateR(IssueCommitmentMessage second)
    {
        //Obtain c from H(R,W,Nonce)
        bn_t c = Attributes.hashAndConvert(nonce,second.getW(), second.getR());

        // res = R^c W
        ep_t res = new ep_t();
        Relic.INSTANCE.ep_mul_monty(res, second.getR(),c);
        Relic.INSTANCE.ep_add_basic(res,res, second.getW());

        // Obtain (S^s) (S_0^s_0)
        ep_t ep_temp = new ep_t(),res_1 = new ep_t();
        Relic.INSTANCE.ep_mul_monty(res_1, second.getS(), second.gets());
        Relic.INSTANCE.ep_mul_monty(ep_temp,second.getS0(), second.gets0());
        Relic.INSTANCE.ep_add_basic(res_1,res_1,ep_temp);


        if(Relic.INSTANCE.ep_cmp(res,res_1) == 0)
            return true;
        else
            return false;

    }

}
