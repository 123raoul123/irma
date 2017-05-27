package irma;
import relic.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raoul on 24/05/2017.
 */
public class Attributes {

    private List<bn_t> unsigned_attribute_list;
    private List<ep_t> signed_attribute_list;

    public Attributes(int n)
    {
        unsigned_attribute_list = new ArrayList<bn_t>();
        signed_attribute_list = new ArrayList<ep_t>();

        bn_t ord = new bn_t();
        Relic.INSTANCE.ep_curve_get_ord(ord);

        for(int i =0;i<n;++i)
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_rand_mod(temp,ord);
            unsigned_attribute_list.add(temp);
        }

    }

    public void setSignedAttributeList(List<ep_t> list)
    {
        for(ep_t a_i: list)
        {
            ep_t temp = new ep_t();
            Relic.INSTANCE.ep_copy(temp,a_i);
            signed_attribute_list.add(temp);
        }
    }

    public List<ep_t> getSignedAttributeList()
    {
        List<ep_t> copy = new ArrayList<ep_t>();
        for(ep_t a_i: signed_attribute_list)
        {
            ep_t temp = new ep_t();
            Relic.INSTANCE.ep_copy(temp,a_i);
            copy.add(temp);
        }
        return copy;
    }

    public List<bn_t> getUnsigned_attribute_list()
    {
        List<bn_t> copy = new ArrayList<bn_t>();
        for(bn_t a_i: unsigned_attribute_list)
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_copy(temp,a_i);
            copy.add(temp);
        }
        return copy;
    }
}
