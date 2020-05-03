package com.learn.gulimall.search.test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * packageName = com.learn.gulimall.search.test
 * author = Casey
 * Data = 2020/4/27 10:39 下午
 **/
public class ThreadTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程1开始执行----------");
            return new Integer(0);

        }, executorService);

        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程2开始执行----------");
            return "hello";
        }, executorService);

//        CompletableFuture<String> stringCompletableFuture1 = integerCompletableFuture.thenCombineAsync(stringCompletableFuture, (res1, res2) -> {
//            return "res1 = " + res1 + "  res2= " + res2 + "我是三个线程执行的结果";
//        }, executorService);
//

        integerCompletableFuture.runAfterEither(stringCompletableFuture, () -> {
            System.out.println("我开始执行了哦哦哦-------");
        });

        /*CompletableFuture.allOf(integerCompletableFuture, stringCompletableFuture)
                .whenComplete((exp, exception) -> {

                });
*/
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(integerCompletableFuture, stringCompletableFuture);
        voidCompletableFuture.get();
        System.out.println("执行结束了哦哦---------");

    }
}
