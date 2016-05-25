package com.knol.db.repo

import com.knol.db.connection._
import scala.concurrent.{Await, Future}

case class Bank(name: String, id: Option[Int] = None) {
  override def toString = s"""Bank $name ${ id.map(x => s"id #$x").mkString }"""
}

protected[repo] trait BankTable { this: DBComponent =>
  import driver.api._ // defines DBIOAction and other important bits

  class BankTable(tag: Tag) extends Table[Bank](tag, "bank") {
    val id   = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val name = column[String]("name")

    def * = (name, id.?) <> (Bank.tupled, Bank.unapply)
  }

  val bankTableQuery = TableQuery[BankTable]

  def bankTableAutoInc = bankTableQuery returning bankTableQuery.map(_.id)
}

protected[repo] trait BankRepositoryLike extends BankTable { this: DBComponent =>
  import concurrent.duration.Duration
  import driver.api._ // defines DBIOAction and other important bits

  // Cannot be a val because the query has not initialized yet when the trait constructor runs
  @inline def schemaDDL = bankTableQuery.schema.create.statements
  @inline def makeSchema(): Unit = Await.ready(db.run(bankTableQuery.schema.create), Duration.Inf)

  /** create new bank */
  @inline def createAsync(bank: Bank): Future[Int] = db.run { bankTableAutoInc += bank }
  @inline def create(bank: Bank): Int = Await.result(createAsync(bank), Duration.Inf)

  /** delete all banks */
  @inline def deleteAllAsync(): Future[Int] = db.run { bankTableQuery.delete }
  @inline def deleteAll(): Int = Await.result(deleteAllAsync(), Duration.Inf)

  /** update existing bank */
  @inline def updateAsync(bank: Bank): Future[Int] = db.run { bankTableQuery.filter(_.id === bank.id.get).update(bank) }
  @inline def update(bank: Bank): Int = Await.result(updateAsync(bank), Duration.Inf)

  /** Get bank by id */
  @inline def getByIdAsync(id: Int): Future[Option[Bank]] =
    db.run { bankTableQuery.filter(_.id === id).result.headOption }
  @inline def getById(id: Int): Option[Bank] = Await.result(getByIdAsync(id), Duration.Inf)

  /** Get all banks */
  @inline def getAllAsync: Future[List[Bank]] = db.run { bankTableQuery.to[List].result }
  @inline def getAll: List[Bank] = Await.result(getAllAsync, Duration.Inf)

  /** delete bank by id */
  @inline def deleteAsync(id: Int): Future[Int] = db.run { bankTableQuery.filter(_.id === id).delete }
  @inline def delete(id: Int): Int = Await.result(deleteAsync(id), Duration.Inf)
}

object BankRepository extends BankRepositoryLike with SelectedDB {
  //println(schemaDDL.mkString("\n"))
}
