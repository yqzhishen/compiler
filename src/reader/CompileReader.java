package reader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class CompileReader {

    private final FileReader fileReader;

    private final PushbackReader pushbackReader;

    private final FilePosition pos = new FilePosition();

    private boolean end = false;

    public CompileReader(String src) throws FileNotFoundException {
        this.fileReader = new FileReader(src);
        this.pushbackReader = new PushbackReader(this.fileReader, 256);
    }

    public FilePosition getPos() {
        return this.pos;
    }

    public int read() throws IOException {
        int c =  this.pushbackReader.read();
        if (c == '\n')
            this.pos.nextLine();
        else {
            if (!this.end)
                this.pos.nextColumn();
            if (c == -1)
                this.end = true;
        }
        return c;
    }

    public void unread(int c) throws IOException {
        this.pushbackReader.unread(c);
        if (c == '\n')
            this.pos.prevLine();
        else
            this.pos.prevColumn();
    }

    public void close() throws IOException {
        this.pushbackReader.close();
        this.fileReader.close();
    }

}
