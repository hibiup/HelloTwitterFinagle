package com.hibiup.finagle.http

import com.twitter.finagle.http.{Method, Request, Response, Status}

object Example_3_Filter extends App {
    import com.twitter.finagle.{Service, SimpleFilter}
    import com.twitter.util.{Duration, Future, Timer}

    /**
      * Filter 以 Proxy 的方式代理一个 Service:
      *
      *   -> Filter(Request)
      *       -> Service(Request)
      *       <- Service(Response)
      *   <- Filter(Response)
      *
      * Filter[Req, Resp, Req, Resp] 类型参数的含意是：
      *   第一个参数：Request，输入
      *   第二个参数：Response，输出
      *   第三和第四作为代理的 Service 的输入和输出。
      *
      * Filter.apply 接受这个输入 Request 和被代理的 Service[Request, Response]，返回(Future) Response:
      *   def apply(request: ReqIn, service: Service[ReqOut, RepIn]): Future[RepOut]
      *
      * 为了方便使用，Finagle 定义了一个简化的接口：
      *   abstract class SimpleFilter[Req, Resp] extends Filter[Req, Resp, Req, Resp]
      *
      * 开发只需要集成这个 SimpleFilter 就可以了，以 TimeoutFilter 为例：
      * */
    class TimeoutFilter(timeout: Duration, timer: Timer) extends SimpleFilter[Request, Response] {
        override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
            if (request.method != Method.Get)
                Future(Response(request.version, Status.MethodNotAllowed))
            else {
                /**
                  * 通常，如果 Filter 什么都不做，只需将 Request 原封不动传给 Service, 然后将 Future[Response] 返回。
                  **/
                val res: Future[Response] = service(request)

                /**
                  * 但是透传就失去了 Filter 存在的意义，因此通常会做些工作，例如调用 within 给 Future 设定 timeout。
                  *
                  * 当然，也可以修改 Response。甚至在调用 Service 之前修改 Request，比如增加安全认证。
                  **/
                res.within(timer, timeout)
            }
        }
    }
}
