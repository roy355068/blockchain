import lib.Block;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import java.rmi.RemoteException;
import java.util.Random;

public class BlockChain implements BlockChainBase {
    private int difficulty;
    private int id;
    private Node node;
    private Block genesisBlock;
    List<Block> chain;
    Block newBlock;

    public BlockChain(int id, Node node) {

        this.id = id;
        this.node = node;
        chain = new ArrayList<>();
        chain.add(createGenesisBlock());

    }

    @Override
    public boolean addBlock(Block block) {
        boolean valid = isValidNewBlock(block, getLastBlock());
        if (valid) {
            chain.add(block);
        }
        return valid;
    }


    @Override
    public Block createGenesisBlock(){
        byte[] hashBytes = new byte[32];
        String genesisHash = new String(hashBytes);
        String blockStr = genesisHash + ",,genesis," + 0 + "," + 0 + "," + difficulty +"," + 0;
        genesisBlock = Block.fromString(blockStr);
        return genesisBlock;
    }


    @Override
    public byte[] createNewBlock(String data) {
        long timestamp = System.currentTimeMillis();
        String previousHash = getLastBlock().getHash();
        int index = getLastBlock().getIndex() + 1;
        long nonce = new Random().nextLong();
        String hash = "";
        while  (!testDifficulty(difficulty, hash)) {

            String hashData = nonce + "," + previousHash + "," + data + "," + this.difficulty + "," + timestamp;
            hash = DigestUtils.sha256Hex(hashData.getBytes());
            nonce++;
        }
        String blockStr = hash + "," + previousHash + "," + data + "," + timestamp + "," + nonce + "," + difficulty + "," + index;
        newBlock = Block.fromString(blockStr);
        return newBlock.toString().getBytes();
    }


    /**
     * Helper function. Check if the hash is starts with #difficulty zeros.
     * @param difficulty the difficulty of mining the new block.
     * @param hash the SHA256 hash generated from the information of a block
     * @return true if the hash has correct answer, false otherwise
     */
    private boolean testDifficulty(int difficulty, String hash) {

        int count = 0;
        for (int i = 0 ; i < hash.length() ; i++) {
            int currentByte = Integer.parseInt(String.valueOf(hash.charAt(i)), 16);
            if (currentByte == 0) {
                count += 4;
            }
            if (count == difficulty || currentByte != 0) {
                break;
            }
        }
        return count == difficulty;
    }

    @Override
    public boolean broadcastNewBlock() {

        for (int i = 0; i < this.node.getPeerNumber(); i++) {
            try {
                if (!this.node.broadcastNewBlockToPeer(i, newBlock.toString().getBytes())) {
                    return false;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;

    }

    @Override
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public byte[] getBlockchainData() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.chain.size(); i++) {
            String blockStr = this.chain.get(i).toString();
            sb.append(blockStr);
            if (i < this.chain.size() - 1) {
                sb.append("/");
            }
        }
        return sb.toString().getBytes();

    }

    @Override
    public void downloadBlockchain() {
        String [] candidateChain = new String[0];
        int candidateLength = getBlockChainLength();
        for (int i = 0; i < this.node.getPeerNumber(); i++) {
            if (i == this.id) {
                continue;
            }
            try {
                byte[] dataFromPeer = this.node.getBlockChainDataFromPeer(i);
                String blockData = new String(dataFromPeer);
                String [] blockStrings = blockData.split("/");
                int length = blockStrings.length;
                if (length > candidateLength) {
                    candidateLength = length;
                    candidateChain = blockStrings;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        this.chain = new ArrayList<>();
        for (String blockStr : candidateChain) {
            this.chain.add(Block.fromString(blockStr));
        }
    }

    @Override
    public void setNode(Node node) {
        this.node = node;
    }

    @Override
    public boolean isValidNewBlock(Block newBlock, Block prevBlock) {
        boolean accepted = false;
        if (!prevBlock.getHash().equals(newBlock.getPreviousHash())) {
            accepted = false;
        } else if (!testDifficulty(difficulty, newBlock.getHash())) {
            accepted = false;
        } else if (newBlock.getIndex() != prevBlock.getIndex() + 1) {
            accepted = false;
        } else {
            accepted = true;
        }
        return accepted;
    }

    @Override
    public Block getLastBlock() {
        return this.chain.get(getBlockChainLength() - 1);
    }


    @Override
    public int getBlockChainLength() {
        return this.chain.size();
    }
}
