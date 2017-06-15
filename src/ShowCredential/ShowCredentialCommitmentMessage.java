package ShowCredential;

import relic.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by raoul on 25/05/2017.
 */
public class ShowCredentialCommitmentMessage {

    private ep_t KBlind, SBlind, S0Blind, CBlind, TBlind,W;
    private bn_t sBeta,s,s0;
    private List<ep_t> basePoints;
    private Map<Integer,bn_t> sList;

    public ShowCredentialCommitmentMessage(ep_t KBlind, ep_t SBlind, ep_t S0Blind,
                                           List<ep_t> basePoints, ep_t CBlind, ep_t TBlind,
                                           ep_t W, bn_t s, bn_t s0, bn_t sBeta, Map<Integer,bn_t> sList)
    {
        this.KBlind = new ep_t();
        this.SBlind = new ep_t();
        this.S0Blind = new ep_t();
        this.CBlind = new ep_t();
        this.TBlind = new ep_t();
        this.W = new ep_t();

        Relic.INSTANCE.ep_copy(this.KBlind, KBlind);
        Relic.INSTANCE.ep_copy(this.SBlind, SBlind);
        Relic.INSTANCE.ep_copy(this.S0Blind, S0Blind);
        Relic.INSTANCE.ep_copy(this.CBlind, CBlind);
        Relic.INSTANCE.ep_copy(this.TBlind, TBlind);
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

    public ep_t getS0Blind()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy, S0Blind);
        return copy;
    }

    public ep_t getSBlind()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy, SBlind);
        return copy;
    }

    public ep_t getKBlind()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy, KBlind);
        return copy;
    }

    public ep_t getCBlind()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy, CBlind);
        return copy;
    }

    public ep_t getTBlind()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy, TBlind);
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
