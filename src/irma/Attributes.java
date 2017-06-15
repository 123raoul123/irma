package irma;
import relic.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by raoul on 24/05/2017.
 */
public class Attributes {

    private List<bn_t> attributes;
    private List<ep_t> basePoints;

    public Attributes(int n)
    {
        attributes = new ArrayList<>();
        basePoints = new ArrayList<>();

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
            basePoints.add(temp);
        }
    }

    public List<ep_t> getBasePoints()
    {
        List<ep_t> copy = new ArrayList<>();
        for(ep_t S_i: basePoints)
        {
            ep_t temp = new ep_t();
            Relic.INSTANCE.ep_copy(temp,S_i);
            copy.add(temp);
        }
        return copy;
    }

    public List<bn_t> getAttributes()
    {
        List<bn_t> copy = new ArrayList<>();
        for(bn_t k_i: attributes)
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_copy(temp,k_i);
            copy.add(temp);
        }
        return copy;
    }

    static ep_t computeDLRepresentation(ep_t C, ep_t S, ep_t S0, List<ep_t> Si,
                                        bn_t b, bn_t k, bn_t k0, Map<Integer, bn_t> ki) {
        ep_t ret = new ep_t(), ep_temp = new ep_t();

        Relic.INSTANCE.ep_mul_monty(ret, C, b);
        Relic.INSTANCE.ep_mul_monty(ep_temp, S, k);
        Relic.INSTANCE.ep_add_basic(ret, ret, ep_temp);
        Relic.INSTANCE.ep_mul_monty(ep_temp, S0, k0);
        Relic.INSTANCE.ep_add_basic(ret, ret, ep_temp);

        for (int i : ki.keySet()) {
            Relic.INSTANCE.ep_mul_monty(ep_temp, Si.get(i), ki.get(i));
            Relic.INSTANCE.ep_add_basic(ret, ret, ep_temp);
        }

        return ret;
    }

    static ep_t compute_D(ep_t K, List<ep_t> basepoints, Map<Integer,bn_t> disclosedAttributes) {
        ep_t ep_temp = new ep_t(), D = new ep_t();

        // Compute (K S S_0 \prod_{disclosed i} S_i)^{-1}
        Relic.INSTANCE.ep_copy(D, K);
        for (int i : disclosedAttributes.keySet()) {
            Relic.INSTANCE.ep_mul_monty(ep_temp, basepoints.get(i), disclosedAttributes.get(i));
            Relic.INSTANCE.ep_add_basic(D,D,ep_temp);
        }
        Relic.INSTANCE.ep_neg_basic(D, D);

        return D;
    }
}
