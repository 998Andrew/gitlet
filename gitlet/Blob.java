package gitlet;
import java.io.Serializable;
import java.io.File;

/** Blob Object class.
 *  @author Andrew Liu 3032695577 cs61b-alt
 */
public class Blob implements Serializable  {

    /** Blob object's name. */
    private String _name;
    /** Blob object's sha1Code. */
    private String _sha1Code;
    /** Blob object's serializedData after reading contents. */
    private byte [] _serializedData;

    /** Method to create new Blob objects from file NAME
     * and set serialized data to file contents
     * and also set the Blob object's sha1 code.*/
    public Blob(String name) {
        this._name = name;
        _serializedData = Utils.readContents(new File(name));
        _sha1Code = Utils.sha1(_serializedData);
    }

    /** Helper accessor method to return Blob object's name.  */
    public String getName() {
        return _name;
    }
    /** Helper accessor method to return Blob object's sha1Code. */
    public String getSha1Code() {
        return _sha1Code;
    }

    /** Helper accessor method to return Blob object's
     * serialized data. */
    public byte[] getSerializedData() {
        return _serializedData;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Blob other = (Blob) obj;

        return this.getSha1Code().equals(other.getSha1Code());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
