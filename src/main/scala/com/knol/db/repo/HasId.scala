package com.knol.db.repo

import com.knol.db.connection.DBComponent
import scala.concurrent.{Await, Future}
import concurrent.duration.Duration

trait HasId {
  def id: Option[Int] = None
}

trait LiftedHasId {
  def id: slick.lifted.Rep[Int]
}

/** Handles all actions pertaining to HasId or that do not require parameters
  * @see http://stackoverflow.com/questions/37349378/upper-bound-for-slick-3-1-1-query-type */
trait HasIdActionLike[T <: HasId] { this: DBComponent =>
  import driver.api._  // defines DBIOAction and other important bits

  type QueryType <: TableQuery[_ <: Table[T] with LiftedHasId]
  def tableQuery: QueryType

  @inline def run[R](action: DBIOAction[R, NoStream, Nothing]): Future[R] = db.run { action }

  @inline def deleteAsync(id: Int): Future[Int] = run { tableQuery.filter(_.id === id).delete }
  @inline def delete(id: Int): Int = Await.result(deleteAsync(id), Duration.Inf)

  @inline def deleteAllAsync(): Future[Int] = run { tableQuery.delete }
  @inline def deleteAll(): Int = Await.result(deleteAllAsync(), Duration.Inf)

  @inline def getAllAsync: Future[List[T]] = run { tableQuery.to[List].result }
  @inline def getAll: List[T] = Await.result(getAllAsync, Duration.Inf)

  @inline def getByIdAsync(id: Int): Future[Option[T]] =
    run { tableQuery.filter(_.id === id).result.headOption }

  @inline def getById(id: Int): Option[T] = Await.result(getByIdAsync(id), Duration.Inf)

  @inline def deleteById(id: Option[Int]): Unit =
    for { i <- id } run { tableQuery.filter(_.id === id).delete }

  @inline def findAll: Future[List[T]] = run { tableQuery.to[List].result }
}

