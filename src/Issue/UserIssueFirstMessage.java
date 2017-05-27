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
        this.unsigned_attributes = unsigned_attributes;
    }

    public List<bn_t> getUnsigned_attributes()
    {
        List<bn_t> copy = new ArrayList<>(unsigned_attributes);
        return copy;
    }
}
