package irma;
import relic.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raoul on 24/05/2017.
 */
public class Attributes {

    private List<bn_t> attributes;
    private List<ep_t> basepoints;

    public Attributes(int n)
    {
        attributes = new ArrayList<>();
        basepoints = new ArrayList<>();

        bn_t ord = new bn_t();
        Relic.INSTANCE.ep_curve_get_ord(ord);

        for(int i =0;i<n;++i)
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_rand_mod(temp,ord);
            attributes.add(temp);
        }

    }

    public void setBasePoints(List<ep_t> list)
    {
        for(ep_t S_i: list)
        {
            ep_t temp = new ep_t();
            Relic.INSTANCE.ep_copy(temp,S_i);
            basepoints.add(temp);
        }
    }

    public List<ep_t> getBasePoints()
    {
        List<ep_t> copy = new ArrayList<>();
        for(ep_t S_i: basepoints)
        {
            ep_t temp = new ep_t();
            Relic.INSTANCE.ep_copy(temp,S_i);
            copy.add(temp);
        }
        return copy;
    }

    public List<bn_t> getAttributes()
    {
        List<bn_t> copy = new ArrayList<bn_t>();
        for(bn_t k_i: attributes)
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_copy(temp,k_i);
            copy.add(temp);
        }
        return copy;
    }
}
