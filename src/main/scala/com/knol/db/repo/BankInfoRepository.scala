package com.knol.db.repo

import com.knol.db.connection.{DBComponent, SelectedDB}
import scala.concurrent.{Await, Future}

case class BankInfo(owner: String, branches: Int, bankId: Int, id: Option[Int] = None) {
  override def toString = s"""BankInfo for $owner; bankId #$bankId; $branches branches."""
}

private[repo] trait BankInfoTable extends BankTable { this: DBComponent =>
  import driver.api._

  private[BankInfoTable] class BankInfoTable(tag: Tag) extends Table[BankInfo](tag, "bankinfo") {
    val id       = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val owner    = column[String]("owner")
    val bankId   = column[Int]("bank_id")
    val branches = column[Int]("branches")

    def bankFK = foreignKey("bank_product_fk", bankId, bankTableQuery)(_.id)

    def * = (owner, branches, bankId, id.?) <> (BankInfo.tupled, BankInfo.unapply)
  }

  protected val tableQuery = TableQuery[BankInfoTable]

  protected def autoInc = tableQuery returning tableQuery.map(_.id)
}

trait BankInfoRepositoryLike extends BankInfoTable { this: DBComponent =>
  import concurrent.duration.Duration
  import driver.api._

  @inline def createAsync(bankInfo: BankInfo): Future[Int] = db.run { autoInc += bankInfo }
  @inline def create(bankInfo: BankInfo): Int = Await.result(createAsync(bankInfo), Duration.Inf)

  @inline def deleteAllAsync(): Future[Int] = db.run { tableQuery.delete }
  @inline def deleteAll(): Int = Await.result(deleteAllAsync(), Duration.Inf)

  @inline def updateAsync(bankInfo: BankInfo): Future[Int] =
    db.run { tableQuery.filter(_.id === bankInfo.id.get).update(bankInfo) }
  @inline def update(bankInfo: BankInfo): Int = Await.result(updateAsync(bankInfo), Duration.Inf)

  @inline def getByIdAsync(id: Int): Future[Option[BankInfo]] =
    db.run { tableQuery.filter(_.id === id).result.headOption }
  @inline def getById(id: Int): Option[BankInfo] = Await.result(getByIdAsync(id), Duration.Inf)

  @inline def getAllAsync: Future[List[BankInfo]] = db.run { tableQuery.to[List].result }
  @inline def getAll: List[BankInfo] = Await.result(getAllAsync, Duration.Inf)

  @inline def deleteAsync(id: Int): Future[Int] = db.run { tableQuery.filter(_.id === id).delete }
  @inline def delete(id: Int): Int = Await.result(deleteAsync(id), Duration.Inf)

  /** Get bank and info using foreign key relationship */
  @inline def getBankWithInfoAsync: Future[List[(Bank, BankInfo)]] =
    db.run {
      (for {
        info <- tableQuery
        bank <- info.bankFK
      } yield (bank, info)).to[List].result
    }
  @inline def getBankWithInfo: List[(Bank, BankInfo)] = Await.result(getBankWithInfoAsync, Duration.Inf)

  /** Get all bank and their info.It is possible some bank do not have their product */
  @inline def getAllBankWithInfoAsync: Future[List[(Bank, Option[BankInfo])]] =
    db.run {
      bankTableQuery.joinLeft(tableQuery).on(_.id === _.bankId).to[List].result
    }
  @inline def getAllBankWithInfo: List[(Bank, Option[BankInfo])] = Await.result(getAllBankWithInfoAsync, Duration.Inf)
}

object BankInfoRepository extends BankInfoRepositoryLike with SelectedDB
