import lib.Block;

import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.util.Random;

public class BlockChain implements BlockChainBase {

    /* Should have fields:
     *      int id            -> node's ID passed from the Node when instantiating the BlockChain object
     *      Node node         -> node that this ledger associated with
     *      byte[] blockChain -> Download from the peers, using downloadBlockchain() to set the value as byte[]
     *      Block genesisBlock-> The genesis block of the current block chain
     *      Block lastBlock   -> The last block of the current block chain
     *      int difficulty    -> Difficulty level of the proof-of-work
     */
    private int id;
    private Node node;
    private byte[] blockChain;
    private Block genesisBlock;
    private Block lastBlock;
    private int difficulty;
    private byte[] newestBlock;

    public BlockChain(int nodeId, Node node) {
        this.id = nodeId;
        this.node = node;
    }

    @Override
    public boolean addBlock(Block block) {
        Block prevBlock = getLastBlock();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < difficulty; i++ ) {
            sb.append("0");
        }
        String prefix = sb.toString();
        boolean accepted = false;
        if (!prevBlock.getHash().equals(block.getPreviousHash())) {
            accepted = false;
        } else if (!block.getHash().startsWith(prefix)) {
            accepted = false;
        } else {
            accepted = true;
        }
        return accepted;
    }

    @Override
    public Block createGenesisBlock() {
        byte[] hashBytes = new byte[32];
        new Random().nextBytes(hashBytes);
        String hash = new String(hashBytes, Charset.forName("utf-8"));

        byte[] prevHash = new byte[32];
        new Random().nextBytes(prevHash);
        String previousHash = new String(prevHash, Charset.forName("utf-8"));

        byte[] dataBytes = new byte[32];
        new Random().nextBytes(dataBytes);
        String data = new String(dataBytes, Charset.forName("utf-8"));

        Block genesisBlock = new Block(hash, previousHash, data, 0);
        this.genesisBlock = genesisBlock;
        return genesisBlock;
    }

    @Override
    public byte[] createNewBlock(String data) {
        newestBlock = data.getBytes();
        return newestBlock;
    }

    @Override
    public boolean broadcastNewBlock() {
        boolean success = false;
        for (int i = 0; i < node.getPeerNumber(); i++) {
            if (i != this.id) {
                try {
                    if (!node.broadcastNewBlockToPeer(i, newestBlock)) {
                        return false;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
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
        return new byte[0];
    }

    @Override
    public void downloadBlockchain() {

    }

    @Override
    public void setNode(Node node) {

    }

    @Override
    public boolean isValidNewBlock(Block newBlock, Block prevBlock) {
        return false;
    }

    @Override
    public Block getLastBlock() {
        return null;
    }

    @Override
    public int getBlockChainLength() {
        return 0;
    }

}
