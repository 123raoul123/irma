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
        this.signed_attribute_list = new ArrayList<>(signed_attribute_list);
    }

    public bn_t getKappa_pp()
    {
        return kappa_pp;
    }

    public ep_t getT()
    {
        return T;
    }

    public ep_t getK()
    {
        return K;
    }

    public List<ep_t> getSigned_attribute_list()
    {
        return signed_attribute_list;
    }

}
