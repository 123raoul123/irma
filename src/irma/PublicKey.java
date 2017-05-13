package irma;
import relic.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raoul on 29/04/2017.
 */
public class PublicKey {

    private ep2_t A,Z,Q;
    private List<ep2_t> A_list;


    public PublicKey(ep2_t A,ep2_t Z,ep2_t Q, List<ep2_t> A_list)
    {
        this.A = new ep2_t();
        this.Z = new ep2_t();
        this.Q = new ep2_t();
        this.A_list = new ArrayList<>();

        Relic.INSTANCE.ep2_copy(this.A,A);
        Relic.INSTANCE.ep2_copy(this.Z,Z);
        Relic.INSTANCE.ep2_copy(this.Q,Q);

        for(ep2_t a_i: A_list)
        {
            ep2_t temp = new ep2_t();
            Relic.INSTANCE.ep2_copy(temp,a_i);
            this.A_list.add(temp);
        }

    }

    public PublicKey(PublicKey pubkey)
    {
      this.A = pubkey.getA();
      this.Z = pubkey.getZ();
      this.Q = pubkey.getQ();
      this.A_list = pubkey.getA_list();
    }

    public ep2_t getA()
    {
        ep2_t copy = new ep2_t();
        Relic.INSTANCE.ep2_copy(copy,this.A);
        return copy;
    }

    public ep2_t getZ()
    {
        ep2_t copy = new ep2_t();
        Relic.INSTANCE.ep2_copy(copy,this.Z);
        return copy;
    }

    public ep2_t getQ()
    {
        ep2_t copy = new ep2_t();
        Relic.INSTANCE.ep2_copy(copy,this.Q);
        return copy;
    }

    public List<ep2_t> getA_list()
    {
        List<ep2_t> copy = new ArrayList<>();
        for(ep2_t a_i: this.A_list)
        {
            ep2_t temp = new ep2_t();
            Relic.INSTANCE.ep2_copy(temp,a_i);
            copy.add(temp);
        }
        return copy;
    }



}
