package btdex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import btdex.core.Mediators;
import burst.kit.entity.BurstID;
import burst.kit.service.BurstNodeService;
import org.junit.FixMethodOrder;
import org.junit.Test;

import bt.BT;
import btdex.sc.SellContract;
import burst.kit.entity.BurstAddress;
import burst.kit.entity.BurstValue;
import burst.kit.entity.response.AT;
import org.junit.runners.MethodSorters;

import java.io.IOException;

/**
 * We assume a localhost testnet with 0 seconds mock mining is available for the
 * tests to work.
 * 
 * @author jjos
 */

@FixMethodOrder(MethodSorters.JVM)  //calls tests in sequence
public class TestTakeRetake extends BT {
    private static bt.compiler.Compiler compiled;
    private static AT contract;
    private static String makerPass;
    private static long state;
    private static long state_chain;
    private static long taker_chain;
    private static long amount;
    private static long security;
    private static long sent;
    private static long amount_chain;
    private static long security_chain;
    private static long balance;
    private static String takerPass;
    private static BurstAddress taker;
    private static BurstNodeService bns = BT.getNode();

    @Test
    public void initSC() throws IOException {
        CreateSC sc = new CreateSC(SellContract.class, 10000, 100);
        makerPass = Long.toString(System.currentTimeMillis());
        String name = sc.registerSC(makerPass);

        BurstAddress maker = BT.getBurstAddressFromPassphrase(makerPass);
        contract = BT.findContract(maker, name);
        System.out.println("Created contract id " + contract.getId().getID());

        compiled = sc.getCompiled();
        amount = sc.getAmount();
        security = sc.getSecurity();
        sent = amount + security + SellContract.ACTIVATION_FEE;
    }

    @Test
    public void testMediators() {
        Mediators mediators = new Mediators(true);
        BurstID mediator1 = mediators.getMediators()[0];
        BurstID mediator2 = mediators.getMediators()[1];
        long med1_chain = BT.getContractFieldValue(contract, compiled.getField("mediator1").getAddress());
        long med2_chain = BT.getContractFieldValue(contract, compiled.getField("mediator2").getAddress());

        assertEquals("Mediator1 not equal", mediator1.getSignedLongId(), med1_chain);
        assertEquals("Mediator2 not equal", mediator2.getSignedLongId(), med2_chain);
    }

    @Test
    public void testStateFinished(){
        state_chain = BT.getContractFieldValue(contract, compiled.getField("state").getAddress());
        state = SellContract.STATE_FINISHED;
        assertEquals("State not equal", state, state_chain);
    }

    private long accBalance(String pass) {
        return (bns.getAccount(BT.getBurstAddressFromPassphrase(pass)).blockingGet()).getBalance().longValue();
    }

    @Test
    public void testOfferInit(){
        //fund maker if needed
        while(accBalance(makerPass) < sent){
            BT.forgeBlock(makerPass);
        }
        BT.callMethod(makerPass, contract.getId(), compiled.getMethod("update"),
                BurstValue.fromPlanck(sent), BurstValue.fromBurst(0.1), 1000,
                security).blockingGet();
        BT.forgeBlock();
        BT.forgeBlock();

        // should now be open
        state = SellContract.STATE_OPEN;
        state_chain = BT.getContractFieldValue(contract, compiled.getField("state").getAddress());
        amount_chain = BT.getContractFieldValue(contract, compiled.getField("amount").getAddress());
        security_chain = BT.getContractFieldValue(contract, compiled.getField("security").getAddress());

        assertEquals(state, state_chain);
        // assertEquals(pauseTimeout, pauseTimeout_chain);
        assertTrue(amount_chain > amount);
        assertEquals(security, security_chain);

        balance = BT.getContractBalance(contract).longValue();
        System.out.println("balance " + balance );
        assertTrue("not enough balance", balance > amount + security);
    }

    @Test
    public void initTaker() {
        takerPass = Long.toString(System.currentTimeMillis());
        taker = BT.getBurstAddressFromPassphrase(takerPass);
        //register taker in chain
        BT.forgeBlock(takerPass);
        //fund taker if needed
        while (accBalance(takerPass) < security + SellContract.ACTIVATION_FEE) {
            BT.forgeBlock(takerPass);
        }
    }

    @Test
    public void testOfferTake() {
        // Take the offer
        BT.callMethod(takerPass, contract.getId(), compiled.getMethod("take"),
                BurstValue.fromPlanck(security + SellContract.ACTIVATION_FEE), BurstValue.fromBurst(0.1), 100,
                security, amount_chain).blockingGet();
        BT.forgeBlock();
        BT.forgeBlock();
        System.out.println("balance " + BT.getContractBalance(contract).longValue());

        // order should be taken, waiting for payment
        state_chain = BT.getContractFieldValue(contract, compiled.getField("state").getAddress());
        taker_chain = BT.getContractFieldValue(contract, compiled.getField("taker").getAddress());

        state = SellContract.STATE_WAITING_PAYMT;
        assertEquals(state, state_chain);
        assertEquals(taker.getSignedLongId(), taker_chain);
    }

    @Test
    public void testContractsBalanceAfterOfferTake() {
        balance = BT.getContractBalance(contract).longValue();
        assertTrue("not enough balance", balance > amount + security * 2);
        System.out.println("Contract fees to take: " + BurstValue.fromPlanck(sent - balance));
    }

    @Test
    public void testMakerSignal() {
        // Maker signals the payment was received (off-chain)
        BT.callMethod(makerPass, contract.getId(), compiled.getMethod("reportComplete"),
                BurstValue.fromPlanck(SellContract.ACTIVATION_FEE), BurstValue.fromBurst(0.1), 100).blockingGet();
        BT.forgeBlock(makerPass);
        BT.forgeBlock(makerPass);

        // order should be finished
        state_chain = BT.getContractFieldValue(contract, compiled.getField("state").getAddress());
        assertEquals(SellContract.STATE_FINISHED, state_chain);

        balance = BT.getContractBalance(contract).longValue();
        assertTrue("not enough balance", balance == 0);
        assertTrue("taker have not received", accBalance(takerPass) > amount);
    }

    @Test
    public void testReopen(){
        //fund maker if needed
        while(accBalance(makerPass) < amount + security + SellContract.ACTIVATION_FEE){
            BT.forgeBlock(makerPass);
        }
        // Reopen the offer
        BT.callMethod(makerPass, contract.getId(), compiled.getMethod("update"),
                BurstValue.fromPlanck(amount + security + SellContract.ACTIVATION_FEE), BurstValue.fromBurst(0.1), 100,
                security).blockingGet();
        BT.forgeBlock();
        BT.forgeBlock();

        // should now be open
        state = SellContract.STATE_OPEN;
        state_chain = BT.getContractFieldValue(contract, compiled.getField("state").getAddress());
        amount_chain = BT.getContractFieldValue(contract, compiled.getField("amount").getAddress());
        security_chain = BT.getContractFieldValue(contract, compiled.getField("security").getAddress());

        assertEquals(state, state_chain);
        assertTrue(amount_chain > amount);
        assertEquals(security, security_chain);
    }

    @Test
    public void testTakeOfferAgain() {
        // Take the offer again
        BT.callMethod(takerPass, contract.getId(), compiled.getMethod("take"),
                BurstValue.fromPlanck(security + SellContract.ACTIVATION_FEE), BurstValue.fromBurst(0.1), 100,
                security, amount_chain).blockingGet();
        BT.forgeBlock();
        BT.forgeBlock();

        // order should be taken, waiting for payment
        state_chain = BT.getContractFieldValue(contract, compiled.getField("state").getAddress());
        taker_chain = BT.getContractFieldValue(contract, compiled.getField("taker").getAddress());

        assertEquals(SellContract.STATE_WAITING_PAYMT, state_chain);
        assertEquals(taker.getSignedLongId(), taker_chain);
    }

    @Test
    public void testPaymentReceived() {
        // Maker signals the payment was received (off-chain)
        BT.callMethod(makerPass, contract.getId(), compiled.getMethod("reportComplete"),
                BurstValue.fromPlanck(SellContract.ACTIVATION_FEE), BurstValue.fromBurst(0.1), 100).blockingGet();
        BT.forgeBlock();
        BT.forgeBlock();

        // order should be finished
        state_chain = BT.getContractFieldValue(contract, compiled.getField("state").getAddress());
        assertEquals(SellContract.STATE_FINISHED, state_chain);
    }

    @Test
    public void testFinalContractsBalance() {
        balance = BT.getContractBalance(contract).longValue();
        assertTrue("not enough balance", balance == 0);
    }
 }
