package util.threads;

public class ExecutionThread extends Thread{
    //odd that this kind of generic reusable thread doesn't already exist...
    //unless it does? This is my first time using multithreading other than CompletableFuture.

    public boolean running;
    private Runnable task;
    public ExecutionThread(){

    }
    /**
     * If this thread was constructed using a separate
     * {@code Runnable} run object, then that
     * {@code Runnable} object's {@code run} method is called;
     * otherwise, this method does nothing and returns.
     * <p>
     * Subclasses of {@code Thread} should override this method.
     *
     * @see #start()
     * @see #stop()
     * @see #Thread(ThreadGroup, Runnable, String)
     */
    @Override
    public void run() {
        running = true;
        super.run();
        task.run();
        running = false;
    }

    public boolean setTask(Runnable task){
        //return false if it is already doing a task
        //return true if it finished the last task and is ready to
        if(this.running) return false;
        this.task = task;
        return true;
    }
}
