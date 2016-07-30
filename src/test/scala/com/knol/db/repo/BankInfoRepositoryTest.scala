package com.knol.db.repo

import com.knol.db.connection.PostgresDBComponent
import org.scalatest.FunSuite
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import scala.concurrent.ExecutionContext.Implicits.global

class BankInfoRepositoryTest extends FunSuite with BankInfoRepositoryLike with PostgresDBComponent with ScalaFutures {
  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))
  val bankOne: Bank = BankRepository.getAll.head

  test("Add new bank info") {
    whenReady(createAsync(BankInfo("Government", branches=1000, bankId=bankOne.idAsInt))) { bankInfo =>
      assert(bankInfo.id.nonEmpty)
    }
  }

  test("Update bank info") {
    val bankInfoOne: BankInfo = findAll.head
    whenReady(updateAsync(BankInfo("Government", branches=18989, bankId=bankOne.idAsInt, id=bankInfoOne.id))) { updatedRowCount =>
      assert(updatedRowCount === 1)
    }
  }

  test("Delete bank info") {
    val bankInfoOne: BankInfo = findAll.head
    whenReady(deleteAsync(bankInfoOne.idAsInt)) { deletedRowCount =>
      assert(deletedRowCount === 1)
    }
  }

  test("Get bank info list") {
    val bankInfoOne: BankInfo = findAll.head
    val desired: List[BankInfo] = List(BankInfo("Government", branches=10000, bankId=bankOne.idAsInt, bankInfoOne.id))
    whenReady(findAllAsync) { bankInfos =>
      assert(bankInfos.nonEmpty)
    }
  }

  test("Get bank and their info list") {
    whenReady(getBankWithInfoAsync) { result =>
      assert(result.nonEmpty)
    }
  }

  test("Get all bank and info list") {
    whenReady(getAllBankWithInfoAsync) { bankInfos =>
      assert(bankInfos.nonEmpty)
    }
  }
}
