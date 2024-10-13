package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
//import org.bouncycastle.util.Arrays;

public class AEADParameters
    implements CipherParameters
{
    private byte[] associatedText;
    private byte[] nonce;
    private KeyParameter key;
    private int macSize;

    /**
     * Base constructor.
     *
     * @param key key to be used by underlying cipher
     * @param macSize macSize in bits
     * @param nonce nonce to be used
     */
   public AEADParameters(KeyParameter key, int macSize, byte[] nonce)
    {
       this(key, macSize, nonce, null);
    }

    /**
     * Base constructor.
     *
     * @param key key to be used by underlying cipher
     * @param macSize macSize in bits
     * @param nonce nonce to be used
     * @param associatedText initial associated text, if any
     */
    public AEADParameters(KeyParameter key, int macSize, byte[] nonce, byte[] associatedText)
    {
        this.key = key;
        this.nonce = nonce == null ? null : nonce.clone();
        this.macSize = macSize;
        this.associatedText = associatedText == null ? null : associatedText.clone();
    }

    public KeyParameter getKey()
    {
        return key;
    }

    public int getMacSize()
    {
        return macSize;
    }

    public byte[] getAssociatedText()
    {
        return associatedText == null ? null : associatedText.clone();
    }

    public byte[] getNonce()
    {
        return nonce == null ? null : nonce.clone();
    }
}
