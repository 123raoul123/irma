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
    private bn_t s_beta,s,s0;
    private List<ep_t> blinded_attribute_list;
    private Map<Integer,bn_t> s_list;

    public UserShowCredentialSecondMessage(ep_t K_blind, ep_t S_blind, ep_t S_zero_blind,
                                           List<ep_t> blinded_attribute_list, ep_t C_blind, ep_t T_blind,
                                           ep_t W, bn_t s_beta, bn_t s, bn_t s0, Map<Integer,bn_t> s_list)
    {
        this.K_blind = K_blind;
        this.S_blind = S_blind;
        this.S_zero_blind = S_zero_blind;
        this.blinded_attribute_list = blinded_attribute_list;
        this.C_blind = C_blind;
        this.T_blind = T_blind;
        this.W = W;
        this.s_beta = s_beta;
        this.s = s;
        this.s0 = s0;
        this.s_list = s_list;

    }

    public bn_t gets_beta()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy,s_beta);
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

    public List<ep_t> getBlinded_attribute_list()
    {
        List<ep_t> copy = new ArrayList<>(blinded_attribute_list);
        return copy;
    }

    public Map<Integer, bn_t> gets_list()
    {
        Map<Integer,bn_t> copy = new HashMap<>(s_list);
        return copy;
    }

}
