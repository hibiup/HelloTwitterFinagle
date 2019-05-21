package com.hibiup.finagle.http

import com.twitter.finagle._
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.util.{Await, Future, FuturePool, Time}
import org.slf4j.LoggerFactory


/**
  * Finagle HTTP 的 Service 是某个具体 http 请求的 endpoint。
  * */
object SimpleFinagleHttpService extends App{
    val logger = LoggerFactory.getLogger(this.getClass)

    /**
      * Service 返回 Twitter Future。和 Scala Future 一样，Twitter Future 只是一个协程单元，在它被提交到线程池之前，
      * 并不意味着一定是多线程的。实际上缺省的 Twitter Future 将执行在当前线程中（阻塞）。
      * */
    val service = new Service[Request, Response] {
        /**
          * 当一个 Service 被调用的时候,它返回一个 Future
          * */
        def apply(req: Request): Future[Response] =
            Future.value {
                if (req.method == Method.Get) {   // 对请求的过滤，参见 Filter
                    logger.debug(req.version.versionString)

                    val resp = Response(req.version, Status.Ok)
                    resp.mediaType = "application/json"
                    resp.contentString =
                        """{
                          |    "Message": "Hello Finagle!"
                          |}""".stripMargin
                    resp
                }
                else Response(req.version, Status.MethodNotAllowed)
            }
    }

    /**
      * 注册 Service, 返回一个 ListeningServer。ListeningService 是一个 Http 服务的端口。
      **/
    val server: ListeningServer = Http.serve(":8088", service)
    Await.ready(server) // 阻塞等待 ListeningServer
}


/**
  * Finagle.serve 不仅可以发布本地服务，还可以作为远程代理
  * */
object FinagleHttpProxy extends App {
    val logger = LoggerFactory.getLogger(this.getClass)

    /**
      * 这个例子中，我们直接利用 http.newService 建立本地存根的功能，代理另一个远程服务：
      * */
    val twitter: Service[Request, Response] = Http
        .client.withTransport.verbose        // 可以设置 client 的参数
        .newService("twitter.com:80")  // create a stub for "twitter.com:80"

    /**
      * 建立一个本地服务端口代理 twitter.com
      * */
    val server = Http
        .server.withTransport.verbose    // 可以设置 server 的参数
        .serve(":8088", twitter)   // Publish "localhost:8088" proxies to "twitter.com:80"
    Await.ready(server)
}

/**
  * 一个 Service 可以通过工厂方法获得。
  * */
object FinagleHttpServiceFactory extends App {
    val logger = LoggerFactory.getLogger(this.getClass)

    /**
      * Service 的工厂是 ServiceFactory，可以通过它动态生成复杂的 Service，比如具有反映动态输入的 Service
      * */
    object MyServiceFactory extends ServiceFactory[Request, Response] {
        override def apply(conn: ClientConnection): Future[Service[Request, Response]] = Future(
            new Service[Request, Response] {
                def apply(req: Request): Future[Response] = {
                    logger.debug(s"Client: ${conn.remoteAddress} -> Server: ${conn.localAddress}")
                    Future.value(Response(req.version, Status.Ok))
                }
            }
        )

        override def close(deadline: Time): Future[Unit] = Future(
            logger.debug("ServiceFactory is closing.")
        )
    }

    val server = Http.serve(":8088", MyServiceFactory)   // Publish "localhost:8088" proxies to "twitter.com:80"
    Await.ready(server)
}
