package Issue;
import relic.*;
import irma.*;

/**
 * Created by raoul on 30/04/2017.
 */
public class FirstIssuerMessage {

    private ep2_t K_bar, S_bar, S_zero_bar;

    public FirstIssuerMessage(PrivateKey privkey)
    {
        this.K_bar = new ep2_t();
        this.S_bar = new ep2_t();
        this.S_zero_bar = new ep2_t();

        Relic.INSTANCE.ep2_rand(this.K_bar);
        Relic.INSTANCE.ep2_mul_lwnaf(this.S_bar,this.K_bar,privkey.geta());
        Relic.INSTANCE.ep2_mul_lwnaf(this.S_zero_bar,this.K_bar,privkey.geta_list().get(0));
    }

    public ep2_t getS_bar()
    {
        ep2_t copy = new ep2_t();
        Relic.INSTANCE.ep2_copy(copy,this.S_bar);
        return copy;
    }

    public ep2_t getS_zero_bar()
    {
        ep2_t copy = new ep2_t();
        Relic.INSTANCE.ep2_copy(copy,this.S_zero_bar);
        return copy;
    }
}
