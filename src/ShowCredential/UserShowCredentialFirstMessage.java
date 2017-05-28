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
        this.disclosed_attribute_list = new HashMap<>(disclosed_attribute_list);
        this.disclosed = new ArrayList<>(disclosed);
    }

    public Map<Integer,bn_t> getDisclosed_attribute_list()
    {
        return disclosed_attribute_list;
    }

    public List<Boolean> getDisclosed(){
        return disclosed;
    }
}
