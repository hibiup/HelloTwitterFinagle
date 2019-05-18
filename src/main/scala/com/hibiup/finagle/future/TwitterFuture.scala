package com.hibiup.finagle.future

import com.twitter.util.{Await, Future, FuturePool}
object TwitterFuture extends App {
    /**
      * 得到一个阻塞的 Future
      * */
    val f = Future.value{
        Thread.sleep(100)
        println(s"[Thread-${Thread.currentThread.getName}]: Future...")
        "Something from future..."
    } // 阻塞
    println(s"[Thread-${Thread.currentThread.getName}]: Future invoking is done")
    /*Await.result(*/f.foreach(a => println(s"[Thread-${Thread.currentThread.getName}: $a"))//)
    /*Await.result(*/f.foreach(a => println(s"[Thread-${Thread.currentThread.getName}: $a"))//)

    /**
      * 从线程池返回一个异步的 Future.
      *
      * twitter Future 也是饥渴求值的．
      * */
    val f1 = FuturePool.unboundedPool{
        Thread.sleep(100)
        println(s"[Thread-${Thread.currentThread.getName}]: FuturePool...")
        throw new RuntimeException(s"[Thread-${Thread.currentThread.getName}]:  Something from future pool...")
    } // 非阻塞
    val f2 = f1.rescue{  /** 注意：rescue 返回的 f2 并不等于 f1。 */
        case e:Throwable =>
            Future(s"[Thread-${Thread.currentThread.getName}]: ${e.getMessage}")
    }
    println(s"[Thread-${Thread.currentThread.getName}]: FuturePool invoking is done")
    Await.result(f2.foreach(a => println(s"[Thread-${Thread.currentThread.getName}: $a"))) // 对 recuse 的对象求值会得到正常返回
    Await.result(f1.foreach(a => println(s"[Thread-${Thread.currentThread.getName}: $a"))) // 对原 Future 求值会得到异常
}
