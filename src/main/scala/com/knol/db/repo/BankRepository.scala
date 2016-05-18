package com.knol.db.repo

import com.knol.db.connection._
import scala.concurrent.Future

trait BankRepository extends BankTable { this: DBComponent =>
  import driver.api._

  /** create new bank */
  def create(bank: Bank): Future[Int] = db.run { bankTableAutoInc += bank }

  /** update existing bank */
  def update(bank: Bank): Future[Int] = db.run { bankTableQuery.filter(_.id === bank.id.get).update(bank) }

  /** Get bank by id */
  def getById(id: Int): Future[Option[Bank]] = db.run { bankTableQuery.filter(_.id === id).result.headOption }

  /** Get all banks */
  def getAll: Future[List[Bank]] = db.run { bankTableQuery.to[List].result }

  /** delete bank by id */
  def delete(id: Int): Future[Int] = db.run { bankTableQuery.filter(_.id === id).delete }
}

private[repo] trait BankTable { this: DBComponent =>
  import driver.api._

  private[BankTable] class BankTable(tag: Tag) extends Table[Bank](tag, "bank") {
    val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val name = column[String]("name")
    def * = (name, id.?) <> (Bank.tupled, Bank.unapply)
  }

  protected val bankTableQuery = TableQuery[BankTable]

  protected def bankTableAutoInc = bankTableQuery returning bankTableQuery.map(_.id)
}

object BankRepository extends BankRepository with PostgresDBComponent

case class Bank(name: String, id: Option[Int] = None) {
  override def toString = s"""Bank $name ${ id.map(x => s"id #$x").mkString }"""
}
