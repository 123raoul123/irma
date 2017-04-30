package irma;

import relic.*;

/**
 * Created by raoul on 30/04/2017.
 */
public class UserPrivateKey {

    private bn_t k_zero;

    public UserPrivateKey()
    {
        this.k_zero = new bn_t();
        bn_t ord = new bn_t();
        Relic.INSTANCE.ep_curve_get_ord(ord);
        Relic.INSTANCE.bn_rand_mod(this.k_zero,ord);
    }

    public bn_t getk_zero()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy,this.k_zero);
        return copy;
    }

}
