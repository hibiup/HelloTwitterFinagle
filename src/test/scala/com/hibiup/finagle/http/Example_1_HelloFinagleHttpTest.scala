package com.hibiup.finagle.http

import com.twitter.finagle.{Http, Service, http}
import com.twitter.util.{Await, Future}
import org.scalatest.FlatSpec

class Example_1_HelloFinagleHttpTest extends FlatSpec{
    "Finagle http request" should "" in {
        /**
          * Http.newService 可以为是 Service 在本地建一个 stub，注意，它不等同于客户端。
          * */
        val stub: Service[http.Request, http.Response] = Http.newService("localhost:8088")

        /**
          * http.Request 新建一个客户端，然后通过 proxy 来代理请求：
          * */
        val request = http.Request(http.Method.Get, "/")
        request.host = "twitter.com"    // 可选；如果 request 的是代理服务，设置为最终目标域名, 否则可以缺省
        val response: Future[http.Response] = stub(request)

        Await.result(response.onSuccess { rep: http.Response =>
            println("Receive: " + rep)
        })
    }
}
