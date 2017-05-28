package ShowCredential;

import relic.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by raoul on 28/05/2017.
 */
public class UserShowCredentialFirstMessage {
    private Map<Integer,bn_t> disclosed_attribute_list;
    private List<Boolean> disclosed;

    public UserShowCredentialFirstMessage(Map<Integer,bn_t> disclosed_attribute_list, List<Boolean> disclosed)
    {
        this.disclosed_attribute_list = new HashMap<>();

        for(Map.Entry<Integer,bn_t> entry: disclosed_attribute_list.entrySet())
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_copy(temp,entry.getValue());
            this.disclosed_attribute_list.put(entry.getKey(),temp);
        }

        this.disclosed = new ArrayList<>(disclosed);
    }

    public Map<Integer,bn_t> getDisclosed_attribute_list()
    {
        Map<Integer,bn_t> copy = new HashMap<>();

        for(Map.Entry<Integer,bn_t> entry: disclosed_attribute_list.entrySet())
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
