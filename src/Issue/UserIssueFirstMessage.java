package Issue;
import relic.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raoul on 24/05/2017.
 */
public class UserIssueFirstMessage {

    private List<bn_t> attributes;

    public UserIssueFirstMessage(List<bn_t> attributes)
    {
        this.attributes = new ArrayList<>();

        for(int i = 0; i< attributes.size(); ++i)
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_copy(temp, attributes.get(i));
            this.attributes.add(temp);
        }
    }

    public List<bn_t> getAttributes()
    {
        List<bn_t> copy = new ArrayList<>();

        for(int i = 0; i< attributes.size(); ++i)
        {
            bn_t temp = new bn_t();
            Relic.INSTANCE.bn_copy(temp, attributes.get(i));
            copy.add(temp);
        }
        return copy;
    }
}
