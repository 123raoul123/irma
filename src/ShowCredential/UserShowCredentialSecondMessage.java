package ShowCredential;

import relic.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by raoul on 25/05/2017.
 */
public class UserShowCredentialSecondMessage {

    private ep_t K_blind,S_blind,S_zero_blind,C_blind,T_blind,W;
    private bn_t sBeta,s,s0;
    private List<ep_t> basePoints;
    private Map<Integer,bn_t> sList;

    public UserShowCredentialSecondMessage(ep_t K_blind, ep_t S_blind, ep_t S_zero_blind,
                                           List<ep_t> basePoints, ep_t C_blind, ep_t T_blind,
                                           ep_t W,bn_t s, bn_t s0, bn_t sBeta, Map<Integer,bn_t> sList)
    {
        this.K_blind = new ep_t();
        this.S_blind = new ep_t();
        this.S_zero_blind = new ep_t();
        this.C_blind = new ep_t();
        this.T_blind = new ep_t();
        this.W = new ep_t();

        Relic.INSTANCE.ep_copy(this.K_blind,K_blind);
        Relic.INSTANCE.ep_copy(this.S_blind,S_blind);
        Relic.INSTANCE.ep_copy(this.S_zero_blind,S_zero_blind);
        Relic.INSTANCE.ep_copy(this.C_blind,C_blind);
        Relic.INSTANCE.ep_copy(this.T_blind,T_blind);
        Relic.INSTANCE.ep_copy(this.W,W);

        this.sBeta = new bn_t();
        this.s = new bn_t();
        this.s0 = new bn_t();
        this.basePoints = new ArrayList<>();

        Relic.INSTANCE.bn_copy(this.s0, s0);
        Relic.INSTANCE.bn_copy(this.s, s);
        Relic.INSTANCE.bn_copy(this.sBeta, sBeta);

        for(int i = 0; i< basePoints.size(); ++i)
        {
            ep_t temp = new ep_t();
            Relic.INSTANCE.ep_copy(temp, basePoints.get(i));
            this.basePoints.add(temp);
        }

        this.sList = new HashMap<>();

        for(Map.Entry<Integer,bn_t> entry: sList.entrySet())
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_copy(temp,entry.getValue());
            this.sList.put(entry.getKey(),temp);
        }

    }

    public bn_t gets_beta()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy, sBeta);
        return copy;
    }

    public bn_t gets()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy,s);
        return copy;
    }

    public bn_t gets0()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy,s0);
        return copy;
    }

    public ep_t getW()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,W);
        return copy;
    }

    public ep_t getS_zero_blind()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,S_zero_blind);
        return copy;
    }

    public ep_t getS_blind()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,S_blind);
        return copy;
    }

    public ep_t getK_blind()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,K_blind);
        return copy;
    }

    public ep_t getC_blind()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,C_blind);
        return copy;
    }

    public ep_t getT_blind()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,T_blind);
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

    public Map<Integer, bn_t> getsList()
    {
        Map<Integer,bn_t> copy = new HashMap<>();

        for(Map.Entry<Integer,bn_t> entry: sList.entrySet())
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_copy(temp,entry.getValue());
            copy.put(entry.getKey(),temp);
        }
        return copy;
    }

}
