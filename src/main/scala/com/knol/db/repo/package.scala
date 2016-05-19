package com.knol.db

package object repo {
  import scala.concurrent.{Await, Awaitable}
  import concurrent.duration.Duration

  def run[R](a: Awaitable[R]): R = Await.result(a, Duration.Inf)
}
