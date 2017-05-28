package Issue;
import relic.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raoul on 24/05/2017.
 */
public class UserIssueFirstMessage {

    private List<bn_t> unsigned_attributes;

    public UserIssueFirstMessage(List<bn_t> unsigned_attributes)
    {
        this.unsigned_attributes = new ArrayList<>();

        for(int i=0;i<unsigned_attributes.size();++i)
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_copy(temp,unsigned_attributes.get(i));
            this.unsigned_attributes.add(temp);
        }
    }

    public List<bn_t> getUnsigned_attributes()
    {
        List<bn_t> copy = new ArrayList<>();

        for(int i=0;i<unsigned_attributes.size();++i)
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_copy(temp,unsigned_attributes.get(i));
            copy.add(temp);
        }
        return copy;
    }
}
