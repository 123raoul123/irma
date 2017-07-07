package irma;

import relic.*;

/**
 * This class generates and contains the private key of the user
 */
public class UserPrivateKey {

    private bn_t k0;

    public UserPrivateKey()
    {
        this.k0 = new bn_t();
        bn_t ord = new bn_t();
        Relic.INSTANCE.ep_curve_get_ord(ord);
        Relic.INSTANCE.bn_rand_mod(this.k0,ord);
    }

    /**
     * This method returns the private key of the user
     */
    public bn_t getk0()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy,this.k0);
        return copy;
    }

}
