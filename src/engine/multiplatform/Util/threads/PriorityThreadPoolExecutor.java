package engine.multiplatform.Util.threads;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//I genuinely don't believe Java doesn't already have a built-in version of this.
//Seriously, it was not something I would have expected.
//Oh well, I was probably going to make my own implementation eventually anyway,
// since I need the priority to dynamically update when the player moves.
public class PriorityThreadPoolExecutor<R extends Runnable> {
    private final List<R> tasks;
    private final Thread[] runners;
    private final Comparator<R> comparator;

    public PriorityThreadPoolExecutor(Comparator<R> comparator, int threads){
        tasks = Collections.synchronizedList(new LinkedList<>());
        runners = new Thread[threads];
        this.comparator = comparator;
        for(int i=0; i<threads; ++i){
            runners[i] = new KillablePoolThread<R>(tasks);
            runners[i].start();
        }
    }

    public void submit(R task){
        tasks.add(task);
        tasks.sort(comparator);

    }

    public void stop(){
        tasks.clear();
        for(Thread t: runners){
            t.interrupt();
        }
    }

    public List<R> getTasks (){
        return tasks;
    }


    private static class KillablePoolThread<T extends Runnable> extends Thread{
        private final AtomicBoolean running;
        private final List<T> queue;
        public KillablePoolThread(List<T> queue){
            this.queue = queue;
            running = new AtomicBoolean(false);
        }

        public void interrupt(){
            running.set(false);
            super.interrupt();
        }



        /**
         * When an object implementing interface {@code Runnable} is used
         * to create a thread, starting the thread causes the object's
         * {@code run} method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method {@code run} is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            running.set(true);
            do {
                try {
                    if (!queue.isEmpty()) {
                        queue.remove(0).run();
                    } else {
                        Thread.sleep(100);
                    }
                } catch (Exception ignored){}
            } while (!Thread.interrupted() && running.get());
            running.set(false);
        }
    }
}
