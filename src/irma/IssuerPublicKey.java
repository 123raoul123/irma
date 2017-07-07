package irma;
import relic.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class stores the public key of the issuer
 */
public class IssuerPublicKey {

    private ep2_t A,Z,Q;
    private List<ep2_t> AList;


    public IssuerPublicKey(ep2_t A, ep2_t Z, ep2_t Q, List<ep2_t> AList)
    {
        this.A = new ep2_t();
        this.Z = new ep2_t();
        this.Q = new ep2_t();
        this.AList = new ArrayList<>();

        Relic.INSTANCE.ep2_copy(this.A,A);
        Relic.INSTANCE.ep2_copy(this.Z,Z);
        Relic.INSTANCE.ep2_copy(this.Q,Q);

        for(ep2_t a_i: AList)
        {
            ep2_t temp = new ep2_t();
            Relic.INSTANCE.ep2_copy(temp,a_i);
            this.AList.add(temp);
        }

    }

    public IssuerPublicKey(IssuerPublicKey pubkey)
    {
      this.A = pubkey.getA();
      this.Z = pubkey.getZ();
      this.Q = pubkey.getQ();
      this.AList = pubkey.getAList();
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

    public List<ep2_t> getAList()
    {
        List<ep2_t> copy = new ArrayList<>(AList);
        return copy;
    }



}
