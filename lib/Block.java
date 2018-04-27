package lib;

/**
 * Block Class, the element to compose a Blockchain.
 */
public class Block {

    private String hash;

    private String previousHash;

    private String data;

    private long timestamp;

    private int difficulty;

    private long nonce;

    public Block() {}

    public Block(String hash, String previousHash, String data,
                 long timestamp) {
        this.hash = hash;
        this.previousHash = previousHash;
        this.data = data;
        this.timestamp = timestamp;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }



    // TODO: Override toString and fromString methods

    public static Block fromString(String s){
        // s has the format as "hash,previousHash,data,timestamp"
        String [] fields = s.split(",");

        String hash = fields[0];
        String previousHash = fields[1];
        String data = fields[2];
        long timestamp = Long.parseLong(fields[3]);

        return new Block(hash, previousHash, data, timestamp);

    }

    @Override
    public String toString() {
        String result = "";
        result += this.hash + ",";
        result += this.previousHash + ",";
        result += this.data +",";
        result += this.timestamp;
        return result;
    }

}
