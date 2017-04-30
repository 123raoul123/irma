package Issue;

import irma.UserPrivateKey;
import relic.*;

/**
 * Created by raoul on 30/04/2017.
 */
public class FirstUserMessage {

    private bn_t alpha,kappa_p;
    private ep2_t S,S_zero,R;


    public FirstUserMessage(UserPrivateKey privkey, FirstIssuerMessage message)
    {
        this.alpha = new bn_t();
        this.kappa_p = new bn_t();
        this.S = new ep2_t();
        this.S_zero = new ep2_t();
        this.R = new ep2_t();

        bn_t ord = new bn_t();
        Relic.INSTANCE.ep_curve_get_ord(ord);
        Relic.INSTANCE.bn_rand_mod(this.alpha,ord);
        Relic.INSTANCE.bn_rand_mod(this.kappa_p,ord);

        Relic.INSTANCE.ep2_mul_lwnaf(this.S,message.getS_bar(),this.alpha);
        Relic.INSTANCE.ep2_mul_lwnaf(this.S_zero,message.getS_zero_bar(),this.alpha);

        ep2_t R_left = new ep2_t();
        Relic.INSTANCE.ep2_mul_lwnaf(R_left,this.S,this.kappa_p);

        ep2_t R_right = new ep2_t();
        Relic.INSTANCE.ep2_mul_lwnaf(R_right,this.S_zero,privkey.getk_zero());

        Relic.INSTANCE.ep2_add_basic(this.R,R_left,R_right);
    }

    public ep2_t getS()
    {
        ep2_t copy = new ep2_t();
        Relic.INSTANCE.ep2_copy(copy,this.S);
        return copy;
    }

    public ep2_t getS_zero()
    {
        ep2_t copy = new ep2_t();
        Relic.INSTANCE.ep2_copy(copy,this.S_zero);
        return copy;
    }

    public ep2_t getR()
    {
        ep2_t copy = new ep2_t();
        Relic.INSTANCE.ep2_copy(copy,this.R);
        return copy;
    }

}
