package com.hibiup.finagle.finch

import io.finch._
import io.finch.syntax._
import com.twitter.finagle.Http
import com.twitter.util.Await

object HelloFinch extends App{
    val home: Endpoint[String] = get("hello") {  // Method: get; Path /hello
        Ok("Hello, World!")                      // Content: Hello, World
    }

    val server = Http.server.serve(":8088", home.toServiceAs[Text.Plain])
    Await.ready(server)
}
