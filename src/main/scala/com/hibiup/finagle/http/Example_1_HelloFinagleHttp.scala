package com.hibiup.finagle.http

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.{Await, Future, FuturePool}
import org.slf4j.LoggerFactory

object SimpleFinagleHttpService extends App{
    val logger = LoggerFactory.getLogger(this.getClass)
    /**
      * Finagle HTTP 的 Service 是某个具体 http 请求服务的入口。
      *
      * 和 Scala Future 一样，Twitter Future 只是一个协程单元，在它被提交到线程池之前，并不意味着一定是异步的。
      * 缺省将执行在当前线程中（阻塞）。
      * */
    val service = new Service[http.Request, http.Response] {
        def apply(req: http.Request): Future[http.Response] =
            Future.value {
                logger.debug(req.version.versionString)
                http.Response(req.version, http.Status.Ok)
            }
    }

    /**
      * Http.serve 将服务绑定到一个端口上，然后启动它。
      **/
    val server = Http.serve(":8088", service)
    Await.ready(server) // 阻塞
}

object FinagleHttpProxy extends App {
    val logger = LoggerFactory.getLogger(this.getClass)

    /**
      * Finagle.serve 不仅可以发布本地服务，还可以作为远程代理，这个例子中，我们直接利用 http.newService 建立本地存根的
      * 功能，代理另一个远程服务：
      * */
    val twitter: Service[Request, Response] = Http.newService("twitter.com:80")  // create a stub for "twitter.com:80"

    val server = Http.serve(":8088", twitter)   // Publish "localhost:8088" proxies to "twitter.com:80"
    Await.ready(server)
}
