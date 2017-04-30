package irma;
import relic.*;
import java.util.ArrayList;
import java.util.List;

public class PrivateKey {
    private List<bn_t> a_list = new ArrayList<bn_t>();
    private bn_t a,z;
    private PublicKey pubkey;


    public PrivateKey(int n, ep2_t Q)
    {
        this.a = new bn_t();
        this.z = new bn_t();

        bn_t ord = new bn_t();
        Relic.INSTANCE.ep_curve_get_ord(ord);
        Relic.INSTANCE.bn_rand_mod(this.a,ord);
        Relic.INSTANCE.bn_rand_mod(this.z,ord);

        for(int i =0;i<n;++i)
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_rand_mod(temp,ord);
            this.a_list.add(temp);
        }

        this.pubkey = new PublicKey(this.z,this.a,this.a_list,Q);
    }

    public PrivateKey(PrivateKey privkey)
    {
        this.a = privkey.geta();
        this.z = privkey.getz();
        this.a_list = privkey.geta_list();
        this.pubkey = privkey.getPublicKey();
    }

    public PublicKey getPublicKey()
    {
        return new PublicKey(this.pubkey);
    }

    public bn_t geta()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy,this.a);
        return copy;
    }

    public bn_t getz()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy,this.z);
        return copy;
    }

    public List<bn_t> geta_list()
    {
        List<bn_t> copy = new ArrayList<bn_t>();
        for(bn_t a_i: this.a_list)
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_copy(temp,a_i);
            copy.add(temp);
        }
        return copy;
    }

}
