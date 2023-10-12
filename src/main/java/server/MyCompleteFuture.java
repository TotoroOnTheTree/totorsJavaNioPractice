package server;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MyCompleteFuture {

    public static class Practice{

        public void createCompleteFuture(){
            CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
                System.out.println("This is task1");
            });
            future1.whenComplete((t, u) -> {
                System.out.println("This is task1's callback");
            });

            CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
                System.out.println("This is task2");
                return "task2's result";
            });
            future2.whenComplete((t, u) -> {
                System.out.println("This is task2's callback");
            });

            CompletableFuture<String> future3 = CompletableFuture.completedFuture("hello");
            future3.whenComplete((t, u) -> {
                System.out.println("This is task3's callback");
            });
        }
    }

    /**
     * 测试complete方法
     * 任务未执行完成，主线程就将任务标记为完成，那么结果就是主线程的结果，不会再被任务执行完成后改变
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static void setCompletableValue() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Task is running");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Task is done";
        });
        System.out.println("Main thread is running");
        TimeUnit.SECONDS.sleep(2);
        System.out.println("main sleep 2s");
        future.complete("Main mark task is done");
        TimeUnit.SECONDS.sleep(6);
        System.out.println("result: "+future.get());
    }

    /**
     * 并行执行
     */
    public void serialExecution(){
        CompletableFuture future = CompletableFuture.supplyAsync(() -> {
            System.out.println("This is task1");
            return "task1's result";
        }).thenApplyAsync(result -> {
            System.out.println("This is task2");
            return "task2's result";
        }).thenCompose(result -> {
            System.out.println("This is task3");
            return CompletableFuture.supplyAsync(() -> {
                System.out.println("This is task4");
                return "task4's result";
            });
        }).thenAccept(result -> {
            System.out.println("This is task5");
        });
    }
}
