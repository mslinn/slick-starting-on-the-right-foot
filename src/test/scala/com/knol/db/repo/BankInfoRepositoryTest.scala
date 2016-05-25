package com.knol.db.repo

import com.knol.db.connection.H2DBComponent
import org.scalatest.FunSuite
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import scala.concurrent.Future

class BankInfoRepositoryTest extends FunSuite with BankInfoRepositoryLike with H2DBComponent with ScalaFutures {
  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  test("Add new bank info") {
    whenReady(createAsync(BankInfo("Government", 1000, 1))) { bankInfoId =>
      assert(bankInfoId === 2)
    }
  }

  test("Update  bank info ") {
    whenReady(updateAsync(BankInfo("Government", 18989, 1, Some(1)))) { updatedRowCount =>
      assert(updatedRowCount === 1)
    }
  }

  test("Delete  bank info  ") {
    whenReady(deleteAsync(1)) { deletedRowCount =>
      assert(deletedRowCount === 1)
    }
  }

  test("Get bank info list") {
    val desired: List[BankInfo] = List(BankInfo("Government", 10000, 1, Some(1)))
    whenReady(findAllAsync) { bankInfos =>
      assert(bankInfos === desired)
    }
  }

  test("Get bank and their info list") {
    val desired = List(
      (Bank("SBI bank", Some(1)), BankInfo("Government", 10000, 1, Some(1)))
    )
    val bankInfo = getBankWithInfoAsync
    whenReady(bankInfo) { result =>
      assert(result === desired)
    }
  }

  test("Get all bank and  info list") {
    val desired = List(
      (Bank("SBI bank", Some(1)), Some(BankInfo("Government", 10000, 1, Some(1)))),
      (Bank("PNB bank", Some(2)), None)
    )
    whenReady(getAllBankWithInfoAsync) { bankInfos =>
      assert(bankInfos === desired)
    }
  }
}
