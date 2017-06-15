package Issue;
import relic.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raoul on 06/05/2017.
 */
public class IssueSignatureMessage {
    private bn_t kappa_pp;
    private ep_t K,T;
    private List<ep_t> basePoints;

    public IssueSignatureMessage(bn_t kappa_pp, ep_t K, List<ep_t> basePoints, ep_t T)
    {
        this.kappa_pp = new bn_t();
        this.K = new ep_t();
        this.T = new ep_t();

        Relic.INSTANCE.bn_copy(this.kappa_pp,kappa_pp);
        Relic.INSTANCE.ep_copy(this.K,K);
        Relic.INSTANCE.ep_copy(this.T,T);

        this.basePoints = new ArrayList<>();

        for(int i = 0; i< basePoints.size(); ++i)
        {
            ep_t temp = new ep_t();
            Relic.INSTANCE.ep_copy(temp, basePoints.get(i));
            this.basePoints.add(temp);
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

    public List<ep_t> getBasePoints()
    {
        List<ep_t> copy = new ArrayList<>();

        for(int i = 0; i< basePoints.size(); ++i)
        {
            ep_t temp = new ep_t();
            Relic.INSTANCE.ep_copy(temp, basePoints.get(i));
            copy.add(temp);
        }

        return copy;
    }

}
