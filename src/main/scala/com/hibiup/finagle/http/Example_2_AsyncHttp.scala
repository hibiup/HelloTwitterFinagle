package com.hibiup.finagle.http

import com.twitter.finagle.{Http, Service, http}
import com.twitter.util.Await
import org.slf4j.LoggerFactory

object Example_2_AsyncHttp extends App{
    import com.twitter.util.{Future, FuturePool}

    val logger = LoggerFactory.getLogger(this.getClass)

    /** 阻塞函数 */
    def someIO(req:http.Request): http.Response = {
        logger.debug(s"[Thread-${Thread.currentThread.getName}]: ${req.version.versionString}")
        Thread.sleep(100)
        http.Response(req.version, http.Status.Ok)
    }

    /**
      * 用线程池来获得异步能力
      * */
    val service = new Service[http.Request, http.Response] {
        def apply(req: http.Request): Future[http.Response] =
            FuturePool.unboundedPool {
                someIO(req)
            }
    }

    /**
      * Http.serve 将服务绑定到一个端口上，然后启动它。
      **/
    val server = Http.serve(":8088", service)
    Await.ready(server) // 阻塞
}
