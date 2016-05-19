package com.knol.db.repo

import org.scalatest.FunSuite
import com.knol.db.connection.H2DBComponent
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Millis, Seconds, Span }

class BankRepositoryTest extends FunSuite with BankRepositoryLike with H2DBComponent with ScalaFutures {
  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  test("Add new bank ") {
    whenReady(createAsync(Bank("ICICI bank"))) { bankId =>
      assert(bankId === 3)
    }
  }

  test("Update  SBI bank  ") {
    whenReady(updateAsync(Bank("SBI Bank", Some(1)))) { bankId =>
      assert(bankId === 1)
    }
  }

  test("Delete SBI bank  ") {
    whenReady(deleteAsync(2)) { response =>
      assert(response === 1)
    }
  }

  test("Get bank list") {
    whenReady(getAllAsync) { result =>
      assert(result === List(Bank("SBI bank", Some(1)), Bank("PNB bank", Some(2))))
    }
  }
}
