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

    private int index;

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

    public int getIndex() {
        return this.index;
    }


    // TODO: Override toString and fromString methods
    public static Block fromString(String s){

        String[] arr = s.split(",");
        String hash = arr[0];
        String previousHash = arr[1];
        String data = arr[2];
        long timestamp = Long.parseLong(arr[3]);
        long nonce = Long.parseLong(arr[4]);
        int difficulty = Integer.parseInt(arr[5]);
        int index = Integer.parseInt(arr[6]);
        Block newBlock = new Block(hash, previousHash, data, timestamp);
        newBlock.nonce = nonce;
        newBlock.difficulty = difficulty;
        newBlock.index = index;
        return newBlock;
    }


    @Override
    public String toString() {

        String result = "";
        result += hash + ",";
        result += previousHash + ",";
        result += data + ",";
        result += timestamp + ",";
        result += nonce + ",";
        result += difficulty + ",";
        result += index;
        return result;

    }


}

