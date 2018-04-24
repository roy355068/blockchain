import lib.Block;

public class BlockChain implements BlockChainBase {

    /* Should have fields:
     *      int id            -> node's ID passed from the Node when instantiating the BlockChain object
     *      Node node         -> node that this ledger associated with
     *      byte[] blockChain -> Download from the peers, using downloadBlockchain() to set the value as byte[]
     *      Block genesisBlock-> The genesis block of the current block chain
     *      Block lastBlock   -> The last block of the current block chain
     *      int difficulty    -> Difficulty level of the proof-of-work
     */

    @Override
    public boolean addBlock(Block block) {
        return false;
    }

    @Override
    public Block createGenesisBlock() {
        return null;
    }

    @Override
    public byte[] createNewBlock(String data) {
        return new byte[0];
    }

    @Override
    public boolean broadcastNewBlock() {
        return false;
    }

    @Override
    public void setDifficulty(int difficulty) {

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
