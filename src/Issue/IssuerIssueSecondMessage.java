package Issue;
import relic.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raoul on 06/05/2017.
 */
public class IssuerIssueSecondMessage {
    private bn_t kappa_pp;
    private ep_t K,T;
    private List<ep_t> signed_attribute_list;

    public IssuerIssueSecondMessage(bn_t kappa_pp,ep_t K,List<ep_t> signed_attribute_list,ep_t T)
    {
        this.kappa_pp = new bn_t();
        this.K = new ep_t();
        this.T = new ep_t();

        Relic.INSTANCE.bn_copy(this.kappa_pp,kappa_pp);
        Relic.INSTANCE.ep_copy(this.K,K);
        Relic.INSTANCE.ep_copy(this.T,T);

        this.signed_attribute_list = new ArrayList<>();

        for(int i=0;i<signed_attribute_list.size();++i)
        {
            ep_t temp = new ep_t();
            Relic.INSTANCE.ep_copy(temp,signed_attribute_list.get(i));
            this.signed_attribute_list.add(temp);
        }
    }

    public bn_t getKappa_pp()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy,kappa_pp);
        return copy;
    }

    public ep_t getT()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,T);
        return copy;
    }

    public ep_t getK()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,K);
        return copy;
    }

    public List<ep_t> getSigned_attribute_list()
    {
        List<ep_t> copy = new ArrayList<>();

        for(int i=0;i<signed_attribute_list.size();++i)
        {
            ep_t temp = new ep_t();
            Relic.INSTANCE.ep_copy(temp,signed_attribute_list.get(i));
            copy.add(temp);
        }

        return copy;
    }

}
