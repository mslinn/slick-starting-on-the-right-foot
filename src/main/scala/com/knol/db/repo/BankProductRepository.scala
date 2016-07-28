package com.knol.db.repo

import com.knol.db.Misc.Logger
import com.knol.db.connection.{DBComponent, SelectedDB}
import scala.concurrent.{Await, Future}

case class BankProduct(name: String, bankId: Int, id: Option[Int] = None) {
  override def toString = s"Product $name #$bankId"
}

private[repo] trait BankProductTable extends BankTable { this: DBComponent =>
  import driver.api._ // defines DBIOAction and other important bits

  class BankProductTable(tag: Tag) extends Table[BankProduct](tag, "bankproduct") {
    val id     = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val name   = column[String]("name")
    val bankId = column[Int]("bank_id")

    def bankFK = foreignKey("bank_product_fk", bankId, bankTableQuery)(_.id)

    def * = (name, bankId, id.?) <> (BankProduct.tupled, BankProduct.unapply)
  }

  val tableQuery = TableQuery[BankProductTable]

  def autoInc = tableQuery returning tableQuery
}

private[repo] trait BankProductRepositoryLike extends BankProductTable { this: DBComponent =>
  import concurrent.duration.Duration
  import driver.api._ // defines DBIOAction and other important bits

  /** Fails if bankProduct.id is defined and already exists */
  @inline def create(bankProduct: BankProduct): BankProduct =
    Await.result(createAsync(bankProduct), Duration.Inf)

  @inline def createAsync(bankProduct: BankProduct): Future[BankProduct] = db.run { autoInc += bankProduct }


  @inline def deleteAll(): Int = Await.result(deleteAllAsync(), Duration.Inf)
  @inline def deleteAllAsync(): Future[Int] = db.run { tableQuery.delete }


  @inline def getById(id: Int): Option[BankProduct] = Await.result(getByIdAsync(id), Duration.Inf)

  @inline def getByIdAsync(id: Int): Future[Option[BankProduct]] =
    db.run { tableQuery.filter(_.id === id).result.headOption }


  @inline def getAll: List[BankProduct] = Await.result(getAllAsync, Duration.Inf)
  @inline def getAllAsync: Future[List[BankProduct]] = db.run { tableQuery.to[List].result }

  @inline def delete(id: Int): Int = Await.result(deleteAsync(id), Duration.Inf)
  @inline def deleteAsync(id: Int): Future[Int] = db.run { tableQuery.filter(_.id === id).delete }

  /** Get bank and product using foreign key relationship */
  @inline def getBankWithProduct: List[(Bank, BankProduct)] = Await.result(getBankWithProductAsync, Duration.Inf)

  @inline def getBankWithProductAsync: Future[List[(Bank, BankProduct)]] =
    db.run {
      (for {
        product <- tableQuery
        bank    <- product.bankFK
      } yield (bank, product)).to[List].result
    }


  /** Get all bank and their product.It is possible some bank do not have their product */
  @inline def getAllBankWithProduct: List[(Bank, Option[BankProduct])] =
    Await.result(getAllBankWithProductAsync, Duration.Inf)

  @inline def getAllBankWithProductAsync: Future[List[(Bank, Option[BankProduct])]] =
    db.run { bankTableQuery.joinLeft(tableQuery).on(_.id === _.bankId).to[List].result }


  @inline def update(bankProduct: BankProduct): Int = Await.result(updateAsync(bankProduct), Duration.Inf)

  @inline def updateAsync(bankProduct: BankProduct): Future[Int] =
    db.run { tableQuery.filter(_.id === bankProduct.id.get).update(bankProduct) }


  @inline def upsert(bankProduct: BankProduct): Option[BankProduct] =
    Await.result(upsertAsync(bankProduct), Duration.Inf)

  @inline def upsertAsync(bankProduct: BankProduct): Future[Option[BankProduct]] =
    db.run { autoInc.insertOrUpdate(bankProduct) }

  @inline def upsertOrDie(bankProduct: BankProduct): BankProduct =
      try {
        upsert(bankProduct).get
      } catch {
        case ex: Exception =>
          Logger.error(s"Could not upsert $bankProduct; ${ ex.getMessage }: ${ ex.getCause }")
          throw ex
      }
}

object BankProductRepository extends BankProductRepositoryLike with SelectedDB
