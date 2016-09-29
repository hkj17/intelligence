package com.is.websocket;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SyncFuture<T> implements Future<T>  {

	private CountDownLatch latch = new CountDownLatch(1);
	
	private T response;
	
	private long beginTime = System.currentTimeMillis();
	
    public SyncFuture() {}
	   
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		 if (response != null) {
	            return true;
	        }
	        return false;
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		 latch.await();
	     return this.response;
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		 if(latch.await(timeout, unit)) {
	            return this.response;
	     }
	     return null;
	}

	 // 用于设置响应结果，并且做countDown操作，通知请求线程
    public void setResponse(T response) {
        this.response = response;
        latch.countDown();
    }
    public long getBeginTime() {
        return beginTime;
    }
}
