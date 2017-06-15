package Issue;

import relic.*;

/**
 * Created by raoul on 30/04/2017.
 */
public class UserIssueSecondMessage {

    private ep_t S, S0,R,W;
    private bn_t s, s0;


    public UserIssueSecondMessage(ep_t S, ep_t S0, ep_t R, ep_t W,bn_t s,bn_t s0)
    {
        this.S = new ep_t();
        this.S0 = new ep_t();
        this.R = new ep_t();
        this.W = new ep_t();

        Relic.INSTANCE.ep_copy(this.S,S);
        Relic.INSTANCE.ep_copy(this.S0, S0);
        Relic.INSTANCE.ep_copy(this.R,R);
        Relic.INSTANCE.ep_copy(this.W,W);

        this.s = new bn_t();
        this.s0 = new bn_t();
        Relic.INSTANCE.bn_copy(this.s0, s0);
        Relic.INSTANCE.bn_copy(this.s, s);
    }

    public bn_t gets0()
    {
        bn_t copy = new bn_t();
        Relic.INSTANCE.bn_copy(copy, s0);
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

    public ep_t getS0()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy, S0);
        return copy;
    }

    public ep_t getR()
    {
        ep_t copy = new ep_t();
        Relic.INSTANCE.ep_copy(copy,R);
        return copy;
    }

}
