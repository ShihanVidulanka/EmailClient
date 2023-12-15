package Email.Assignment;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/*
 * if we use normal append mode for save objects to the serialize files we get some error when the file deserialize.
 * the error is "java.io.StreamCorruptedException: invalid stream header: 79737200"
 * because when adding objects add a header for each object 
 * in here it solve by creating a new class extends ObjectOutputStream 
 * when call the AppendableObjectOutputStream() stream header will reset 
 * then the  is solved 
 */
public class AppendableObjectOutputStream extends ObjectOutputStream {

    private boolean append;
    private boolean initialized;
    private DataOutputStream dout;

    protected AppendableObjectOutputStream(boolean append) throws IOException, SecurityException {
        super();
        this.append = append;
        this.initialized = true;
    }

    public AppendableObjectOutputStream(OutputStream out, boolean append) throws IOException {
        super(out);
        this.append = append;
        this.initialized = true;
        this.dout = new DataOutputStream(out);
        this.writeStreamHeader();
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        if (!this.initialized || this.append) return;
        if (dout != null) {
            dout.writeShort(STREAM_MAGIC);
            dout.writeShort(STREAM_VERSION);
        }
    }

}