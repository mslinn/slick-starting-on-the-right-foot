package com.knol.db.repo

import com.knol.db.connection.{DBComponent, SelectedDB}
import scala.concurrent.{Await, Future}
import concurrent.duration.Duration

trait HasId {
  def id: Option[Int] = None
}

trait LiftedHasId {
  def id: slick.lifted.Rep[Int]
}

/** Handles all actions pertaining to HasId or that do not require parameters */
trait DbAction[T <: HasId] { this: DBComponent =>
  import driver.api._ // defines DBIOAction

  type QueryType <: slick.lifted.TableQuery[_ <: Table[T] with LiftedHasId]
  def tableQuery: QueryType

  // Is this defined correctly?
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

case class BankInfo(owner: String, branches: Int, bankId: Int, override val id: Option[Int] = None) extends HasId {
  override def toString = s"""BankInfo for $owner; bankId #$bankId; $branches branches."""
}

private[repo] trait BankInfoTable extends BankTable { this: DBComponent =>
  import driver.api._

  class BankInfoTable(tag: Tag) extends Table[BankInfo](tag, "bankinfo") {
    val id       = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val owner    = column[String]("owner")
    val bankId   = column[Int]("bank_id")
    val branches = column[Int]("branches")

    def bankFK = foreignKey("bank_product_fk", bankId, bankTableQuery)(_.id)

    def * = (owner, branches, bankId, id.?) <> (BankInfo.tupled, BankInfo.unapply)
  }

  type QueryType = TableQuery[BankInfoTable]

  val tableQuery = TableQuery[BankInfoTable]

  def autoInc = tableQuery returning tableQuery.map(_.id)
}

/* Error:(71, 21) overriding type QueryType in trait DbAction with bounds
  <: slick.lifted.TableQuery[_ <: BankInfoRepositoryLike.this.driver.api.Table[com.knol.db.repo.BankInfo] with com.knol.db.repo.LiftedHasId];
 type QueryType in trait BankInfoTable, which equals BankInfoRepositoryLike.this.driver.api.TableQuery[BankInfoRepositoryLike.this.BankInfoTable] has incompatible type
private[repo] trait BankInfoRepositoryLike extends BankInfoTable with DbAction[BankInfo] { this: DBComponent =>
                    ^ */
private[repo] trait BankInfoRepositoryLike extends BankInfoTable with DbAction[BankInfo] { this: DBComponent =>
  import driver.api._

  @inline def updateAsync(bankInfo: BankInfo): Future[Int] =
    run { tableQuery.filter(_.id === bankInfo.id.get).update(bankInfo) }
  @inline def update(bankInfo: BankInfo): Int = Await.result(updateAsync(bankInfo), Duration.Inf)

  @inline def createAsync(bankInfo: BankInfo): Future[Int] = run { autoInc += bankInfo }
  @inline def create(bankInfo: BankInfo): Int = Await.result(createAsync(bankInfo), Duration.Inf)

  /** Get bank and info using foreign key relationship */
  @inline def getBankWithInfoAsync: Future[List[(Bank, BankInfo)]] =
    run {
      (for {
        info <- tableQuery
        bank <- info.bankFK
      } yield (bank, info)).to[List].result
    }
  @inline def getBankWithInfo: List[(Bank, BankInfo)] = Await.result(getBankWithInfoAsync, Duration.Inf)

  /** Get all bank and their info.It is possible some bank do not have their product */
  @inline def getAllBankWithInfoAsync: Future[List[(Bank, Option[BankInfo])]] =
    run {
      bankTableQuery.joinLeft(tableQuery).on(_.id === _.bankId).to[List].result
    }
  @inline def getAllBankWithInfo: List[(Bank, Option[BankInfo])] = Await.result(getAllBankWithInfoAsync, Duration.Inf)
}

object BankInfoRepository extends BankInfoRepositoryLike with SelectedDB
