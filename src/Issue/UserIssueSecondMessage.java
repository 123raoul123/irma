package Issue;

import irma.UserPrivateKey;
import relic.*;

import javax.swing.plaf.SliderUI;

/**
 * Created by raoul on 30/04/2017.
 */
public class UserIssueSecondMessage {

    private ep_t S,S_zero,R,W;
    private bn_t s,s_0;


    public UserIssueSecondMessage(ep_t S, ep_t S_zero, ep_t R, ep_t W, bn_t s, bn_t s_0)
    {
        this.S = S;
        this.S_zero = S_zero;
        this.R = R;
        this.W = W;
        this.s = s;
        this.s_0 = s_0;
    }

    public bn_t get_small_s_zero()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy,this.s_0);
        return copy;
    }

    public bn_t get_Small_s()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy,this.s);
        return copy;
    }

    public ep_t getW()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,this.W);
        return copy;
    }

    public ep_t getS()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,this.S);
        return copy;
    }

    public ep_t getS_zero()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,this.S_zero);
        return copy;
    }

    public ep_t getR()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,this.R);
        return copy;
    }

}
