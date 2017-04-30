package irma;
import relic.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raoul on 29/04/2017.
 */
public class PublicKey {

    private ep2_t A,Z,Q;
    private List<ep2_t> A_list = new ArrayList<ep2_t>();


    public PublicKey(bn_t z, bn_t a, List<bn_t> a_list, ep2_t Q)
    {
        this.A = new ep2_t();
        this.Z = new ep2_t();
        this.Q = new ep2_t();

        Relic.INSTANCE.ep2_copy(this.Q,Q);
        Relic.INSTANCE.ep2_mul_lwnaf(this.A,Q,a);
        Relic.INSTANCE.ep2_mul_lwnaf(this.Z,Q,z);

        for(bn_t a_i: a_list){
            ep2_t temp = new ep2_t();
            Relic.INSTANCE.ep2_mul_lwnaf(temp,Q,a_i);
            A_list.add(temp);
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
        List<ep2_t> copy = new ArrayList<ep2_t>();
        for(ep2_t a_i: this.A_list)
        {
            ep2_t temp = new ep2_t();
            Relic.INSTANCE.ep2_copy(temp,a_i);
            copy.add(temp);
        }
        return copy;
    }



}
