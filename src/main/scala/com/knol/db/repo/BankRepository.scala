package com.knol.db.repo

import com.knol.db.connection._
import scala.concurrent.{Await, Future}

protected[repo] trait BankTable { this: DBComponent =>
  import driver.api._

  private[BankTable] class BankTable(tag: Tag) extends Table[Bank](tag, "bank") {
    val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val name = column[String]("name")
    def * = (name, id.?) <> (Bank.tupled, Bank.unapply)
  }

  protected val bankTableQuery = TableQuery[BankTable]

  protected def bankTableAutoInc = bankTableQuery returning bankTableQuery.map(_.id)
}

protected[repo] trait BankRepository extends BankTable { this: DBComponent =>
  import concurrent.duration.Duration
  import driver.api._

  // Cannot be a val because the query has not initialized yet when the trait constructor runs
  def schemaDDL = bankTableQuery.schema.create.statements
  def makeSchema = Await.result(db.run(bankTableQuery.schema.create), Duration.Inf)

  /** create new bank */
  def create(bank: Bank): Future[Int] = db.run { bankTableAutoInc += bank }

  /** delete all banks */
  def deleteAll(): Future[Int] = db.run { bankTableQuery.delete }

  /** update existing bank */
  def update(bank: Bank): Future[Int] = db.run { bankTableQuery.filter(_.id === bank.id.get).update(bank) }

  /** Get bank by id */
  def getById(id: Int): Future[Option[Bank]] = db.run { bankTableQuery.filter(_.id === id).result.headOption }

  /** Get all banks */
  def getAll: Future[List[Bank]] = db.run { bankTableQuery.to[List].result }

  /** delete bank by id */
  def delete(id: Int): Future[Int] = db.run { bankTableQuery.filter(_.id === id).delete }
}

object BankRepository extends BankRepository with SelectedDB {
  //println(schemaDDL.mkString("\n"))
}

case class Bank(name: String, id: Option[Int] = None) {
  override def toString = s"""Bank $name ${ id.map(x => s"id #$x").mkString }"""
}
