package com.knol.db.repo

import com.knol.db.connection.{DBComponent, SelectedDB}
import com.knol.db.Misc.Logger
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

case class BankInfo(owner: String, branches: Int, bankId: Int, override val id: Option[Int] = None) extends HasId {
  override def toString = s"""BankInfo for $owner; bankId #$bankId; $branches branches."""

  def idAsInt = id.getOrElse(0)
}

protected[repo] trait BankInfoTable extends BankTable { this: DBComponent =>
  import driver.api._ // defines DBIOAction and other important bits

  class BankInfoTable(tag: Tag) extends Table[BankInfo](tag, "bankinfo") with LiftedHasId {
    val id       = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val owner    = column[String]("owner")
    val bankId   = column[Int]("bank_id")
    val branches = column[Int]("branches")

    def bankFK = foreignKey("bank_product_fk", bankId, bankTableQuery)(_.id)

    def * = (owner, branches, bankId, id.?) <> (BankInfo.tupled, BankInfo.unapply)
  }

  type QueryType = TableQuery[BankInfoTable]
  val tableQuery = TableQuery[BankInfoTable]
  def autoInc = tableQuery returning tableQuery
  lazy val schema: driver.DDL = tableQuery.schema
}

protected[repo] trait BankInfoRepositoryLike extends BankInfoTable with HasIdActionLike[BankInfo] { this: DBComponent =>
  import driver.api._ // defines DBIOAction and other important bits

  @inline def create(bankInfo: BankInfo): BankInfo = Await.result(createAsync(bankInfo), Duration.Inf)
  @inline def createAsync(bankInfo: BankInfo): Future[BankInfo] = runAsync { autoInc += bankInfo }

  /** Get bank and info using foreign key relationship */
  @inline def getBankWithInfo: List[(Bank, BankInfo)] = Await.result(getBankWithInfoAsync, Duration.Inf)

  @inline def getBankWithInfoAsync: Future[List[(Bank, BankInfo)]] =
    runAsync {
      (for {
        info <- tableQuery
        bank <- info.bankFK
      } yield (bank, info)).to[List].result
    }


  @inline def getAllBankWithInfo: List[(Bank, Option[BankInfo])] = Await.result(getAllBankWithInfoAsync, Duration.Inf)

  /** Get all bank and their info.It is possible some bank do not have their product */
  @inline def getAllBankWithInfoAsync: Future[List[(Bank, Option[BankInfo])]] =
    runAsync {
      bankTableQuery.joinLeft(tableQuery).on(_.id === _.bankId).to[List].result
    }


  @inline def update(bankInfo: BankInfo): Int = Await.result(updateAsync(bankInfo), Duration.Inf)

  @inline def updateAsync(bankInfo: BankInfo): Future[Int] =
    runAsync { tableQuery.filter(_.id === bankInfo.id.get).update(bankInfo) }


  @inline def upsert(bankInfo: BankInfo)(implicit ec: ExecutionContext): BankInfo =
    Await.result(upsertAsync(bankInfo), Duration.Inf)

  @inline def upsertAsync(bankInfo: BankInfo)(implicit ec: ExecutionContext): Future[BankInfo] =
    db.run { autoInc.insertOrUpdate(bankInfo) }.transform(_.getOrElse(bankInfo), identity)
}

object BankInfoRepository extends BankInfoRepositoryLike with SelectedDB
