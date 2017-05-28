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
        this.S = new ep_t();
        this.S_zero = new ep_t();
        this.R = new ep_t();
        this.W = new ep_t();

        Relic.INSTANCE.ep_copy(this.S,S);
        Relic.INSTANCE.ep_copy(this.S_zero,S_zero);
        Relic.INSTANCE.ep_copy(this.R,R);
        Relic.INSTANCE.ep_copy(this.W,W);

        this.s = new bn_t();
        this.s_0 = new bn_t();

        Relic.INSTANCE.bn_copy(this.s,s);
        Relic.INSTANCE.bn_copy(this.s_0,s);
    }

    public bn_t gets_0()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy,s_0);
        return copy;
    }

    public bn_t gets()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy,s);
        return copy;
    }

    public ep_t getW()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,W);
        return copy;
    }

    public ep_t getS()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,S);
        return copy;
    }

    public ep_t getS_zero()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,S_zero);
        return copy;
    }

    public ep_t getR()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,R);
        return copy;
    }

}
