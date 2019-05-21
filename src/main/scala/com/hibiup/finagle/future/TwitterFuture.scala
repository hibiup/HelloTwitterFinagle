package com.hibiup.finagle.future

object TwitterFuture extends App {
    import com.twitter.util.{Await, Future, FuturePool}
    /**
      * 得到一个阻塞的 Future
      * */
    val f = Future/*.value*/{   // .value 和 .apply 的区别是 value 是 reference by value(和 .pure 一样), apply 是 by name
        Thread.sleep(100)
        println(s"[Thread-${Thread.currentThread.getName}]: Future...")
        "Something from future..."
    } // 阻塞
    //Thread.sleep(200)
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
    //Thread.sleep(200)
    val f2 = f1.rescue{  /** 注意：rescue 返回的 f2 并不等于 f1。 */
        case e:Throwable =>
            Future(s"[Thread-${Thread.currentThread.getName}]: ${e.getMessage}")
    }
    println(s"[Thread-${Thread.currentThread.getName}]: FuturePool invoking is done")
    Await.result(f2.foreach(a => println(s"[Thread-${Thread.currentThread.getName}: $a"))) // 对 recuse 的对象求值会得到正常返回
    Await.result(f1.foreach(a => println(s"[Thread-${Thread.currentThread.getName}: $a"))) // 对原 Future 求值会得到异常
}


object TwitterThrowFromFuture extends App {
    /**
      * Twitter 的 Throw 和 Return 可以取代 Scala 的 Success 和 Failure 让类型更加安全.
      * */
    import com.twitter.util.{Future => TFuture, Promise => TPromise, Return, Throw, Await => TAwait}

    def tp[A]: TPromise[A] = new TPromise[A]

    val tf = TFuture{
        Throw(new RuntimeException("Opss..."))   // Return("Good!!")
    }

    val tr = TAwait.result(tf.respond{
        case Return(v) => tp.setValue(v)     // Good!
        case Throw(e) => tp.setException(e)  // Opss..
    })
    println(tr)

    /**
      * 相比而言, Scala 的 Future 具有明显的副作用
      * */
    import scala.concurrent.{Future => SFuture, Promise => SPromise, Await => SAwait}
    import scala.util.{Success, Failure}
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._

    def sp[A]: SPromise[A] = SPromise()

    val sf = SFuture{
        throw new RuntimeException("Opss...")
    }
    sf.onComplete{
        case Success(v) => sp.success(v)
        case Failure(e) => sp.failure(e)
    }

    val sr = SAwait.result(sf, Duration.Inf)
    println(sr)
}