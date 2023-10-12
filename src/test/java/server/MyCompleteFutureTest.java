package server;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

class MyCompleteFutureTest {

    @Test
    void setCompletableValue() {
        try {
            MyCompleteFuture.setCompletableValue();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}