import lib.Block;

import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.*;
import org.apache.commons.codec.binary.Base64;

public class BlockChain implements BlockChainBase {


    /* Should have fields:
     *      int id            -> node's ID passed from the Node when instantiating the BlockChain object
     *      Node node         -> node that this ledger associated with
     *      byte[] blockChain -> Download from the peers, using downloadBlockchain() to set the value as byte[]
     *      Block genesisBlock-> The genesis block of the current block chain
     *      Block lastBlock   -> The last block of the current block chain
     *      int difficulty    -> Difficulty level of the proof-of-work
     */

    // genesisBlock
    // id
    // difficulty
    // node
    // List<Block>

    private int id;
    private Node node;
    private Block genesisBlock;
    private int difficulty;
    private List<Block> chain;


    private Block newBlock;

    public BlockChain(int nodeId, Node node) {
        this.id = nodeId;
        this.node = node;
        this.chain = new LinkedList<>();
        this.chain.add(createGenesisBlock());
    }

    @Override
    public boolean addBlock(Block block) {

        Block prevBlock = getLastBlock();
        boolean accepted = isValidNewBlock(block, prevBlock);
        this.chain.add(block);
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

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < difficulty / 4; i++ ) {
            sb.append("0");
        }
        String prefix = sb.toString();


        String prevHash = getLastBlock().getHash();
        Timestamp tempTime = new Timestamp(System.currentTimeMillis());
        long timestamp = tempTime.getTime();

        String hash = UUID.randomUUID().toString();
        while (!hash.startsWith(prefix)) {
            String nonce = UUID.randomUUID().toString();
            String hashData = nonce + prevHash + this.difficulty + timestamp;
            byte[] bData = hashData.getBytes();
            hash = org.apache.commons.codec.binary.Base64.encodeBase64String(bData);


//        List<Byte> binaryData = new LinkedList<>();
//        for (byte b : prevHash.getBytes()) {
//            binaryData.add(b);
//        }
//        for (byte b : Integer.toString(this.difficulty).getBytes()) {
//            binaryData.add(b);
//        }
//        for (byte b : Long.toString(timestamp).getBytes()) {
//            binaryData.add(b);
//        }
//        byte [] bData = new byte [binaryData.size()];
//        for (int i = 0 ; i < binaryData.size(); i++) {
//            bData[i] = binaryData.get(i);
//        }


        }
        String construct = hash + "," + prevHash + "," + data + "," + Long.toString(timestamp);
        Block newBlock = Block.fromString(construct);
        this.newBlock = newBlock;
        return newBlock.toString().getBytes();
    }

    @Override
    public boolean broadcastNewBlock() {
        boolean success = false;
        for (int i = 0; i < node.getPeerNumber(); i++) {
            try {
                if (!node.broadcastNewBlockToPeer(i, this.newBlock.toString().getBytes())) {
//                if (!node.broadcastNewBlockToPeer(i, getLastBlock().toString().getBytes())) {
                    return false;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
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
        List<Byte> returnBytes = new LinkedList<>();
        for (Block block : this.chain) {
            String blockData = block.toString() + "/";
            for (byte dataByte : blockData.getBytes()) {
                returnBytes.add(dataByte);
            }
        }
        byte [] result = new byte[returnBytes.size()];
        for (int i = 0 ; i < returnBytes.size() ; i++) {
            result[i] = returnBytes.get(i);
        }
        return result;
    }

    @Override
    public void downloadBlockchain() {

//        List<Block> candidateChain = this.chain;
        for (int i = 0; i < this.node.getPeerNumber(); i++) {
            List<Block> candidateChain = new LinkedList<>();
            try {
                byte [] dataFromPeer = this.node.getBlockChainDataFromPeer(i);
                String blockData = new String(dataFromPeer);
                String [] blockStrings = blockData.split("/");
                for (String str : blockStrings) {
                    Block newBlock = Block.fromString(str);
                    candidateChain.add(newBlock);
                }

                Block lastBlock = candidateChain.get(candidateChain.size() - 1);
                int length = candidateChain.size();
                if (getBlockChainLength() < length) {
                    this.chain = candidateChain;
                }
                else if (lastBlock.getTimestamp() < getLastBlock().getTimestamp()) {
                    this.chain = candidateChain;
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void setNode(Node node) {
        this.node = node;
    }

    @Override
    public boolean isValidNewBlock(Block newBlock, Block prevBlock) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < difficulty / 4; i++ ) {
            sb.append("0");
        }
        String prefix = sb.toString();
        boolean accepted = false;
        if (!prevBlock.getHash().equals(newBlock.getPreviousHash())) {
            accepted = false;
        } else if (!newBlock.getHash().startsWith(prefix)) {
            accepted = false;
        } else {
            accepted = true;
        }
        return accepted;
    }

    @Override
    public Block getLastBlock() {

        return this.chain.get(this.chain.size() - 1);
    }

    @Override
    public int getBlockChainLength() {
        return this.chain.size();
    }

}
