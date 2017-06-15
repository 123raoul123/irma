package ShowCredential;


/**
 * Created by raoul on 27/05/2017.
 */
public class ShowCredentialResponseMessage {

    private byte[] nonce;

    public ShowCredentialResponseMessage(byte[] nonce)
    {
        this.nonce = nonce;
    }

    public byte[] getNonce()
    {
        byte[] copy = new byte[nonce.length];
        System.arraycopy(nonce,0,copy,0,copy.length);
        return copy;
    }

}
