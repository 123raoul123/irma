package ShowCredential;

import relic.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by raoul on 28/05/2017.
 */
public class ShowCredentialRequestMessage {
    private Map<Integer,bn_t> disclosedAttributes;
    private List<Boolean> disclosed;

    public ShowCredentialRequestMessage(Map<Integer,bn_t> disclosedAttributes, List<Boolean> disclosed)
    {
        this.disclosedAttributes = new HashMap<>();

        for(Map.Entry<Integer,bn_t> entry: disclosedAttributes.entrySet())
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_copy(temp,entry.getValue());
            this.disclosedAttributes.put(entry.getKey(),temp);
        }

        this.disclosed = new ArrayList<>(disclosed);
    }

    public Map<Integer,bn_t> getDisclosedAttributes()
    {
        Map<Integer,bn_t> copy = new HashMap<>();

        for(Map.Entry<Integer,bn_t> entry: disclosedAttributes.entrySet())
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_copy(temp,entry.getValue());
            copy.put(entry.getKey(),temp);
        }
        return copy;
    }

    public List<Boolean> getDisclosed(){
        List<Boolean> copy = new ArrayList<>(disclosed);
        return copy;
    }
}
