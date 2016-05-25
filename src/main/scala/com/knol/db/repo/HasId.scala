package com.knol.db.repo

import com.knol.db.connection.DBComponent

trait HasId {
  def id: Option[Int] = None
}

trait LiftedHasId {
  def id: slick.lifted.Rep[Int]
}

/** Handles all actions pertaining to HasId or that do not require parameters
  * @see http://stackoverflow.com/questions/37349378/upper-bound-for-slick-3-1-1-query-type */
trait HasIdActionLike[T <: HasId] { this: DBComponent =>
  import scala.concurrent.{Await, Future}
  import DBComponent._
  import driver.api._  // defines DBIOAction and other important bits

  type QueryType <: TableQuery[_ <: Table[T] with LiftedHasId]
  def tableQuery: QueryType

  //@inline def createTable(): Unit = Await.result(createTableAsync(), dbDuration)
  //@inline def createTableAsync(): Future[Unit] = runAsync { tableQuery.schema.create }
  /* Error:(26, 67) value schema is not a member of HasIdActionLike.this.QueryType
    @inline def createTableAsync(): Future[Unit] = runAsync { tableQuery.schema.create }
                                                                    ^ */

  @inline def delete(id: Int): Int = Await.result(deleteAsync(id), dbDuration)
  @inline def deleteAsync(id: Int): Future[Int] = runAsync { tableQuery.filter(_.id === id).delete }

  @inline def deleteById(id: Option[Int]): Option[Int] =
    deleteByIdAsync(id).map(rows => Await.result(rows, dbDuration))

  @inline def deleteByIdAsync(id: Option[Int]): Option[Future[Int]] =
    id.map(idee => runAsync { tableQuery.filter(_.id === idee).delete })

  @inline def deleteAll(): Int = Await.result(deleteAllAsync(), dbDuration)
  @inline def deleteAllAsync(): Future[Int] = runAsync { tableQuery.delete }

  @inline def findAll: List[T] = Await.result(findAllAsync, dbDuration)
  @inline def findAllAsync: Future[List[T]] = runAsync { tableQuery.to[List].result }

  @inline def findById(id: Int): Option[T] = Await.result(findByIdAsync(id), dbDuration)
  @inline def findByIdAsync(id: Int): Future[Option[T]] =
    runAsync { tableQuery.filter(_.id === id).result.headOption }
}
