package com.knol.db.repo

import com.knol.db.connection.H2DBComponent
import org.scalatest.FunSuite
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

class BankProductRepositoryTest extends FunSuite with BankProductRepositoryLike with H2DBComponent with ScalaFutures {
  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  test("Add new Product ") {
    whenReady(createAsync(BankProduct("car loan", 1))) { productId =>
      assert(productId === 3)
    }
  }

  test("Update bank product ") {
    whenReady(updateAsync(BankProduct("Home Loan", 1, Some(1)))) { response =>
      assert(response === 1)
    }
  }

  test("Delete bank info") {
    whenReady(deleteAsync(1)) { response =>
      assert(response === 1)
    }
  }

  test("Get product list") {
    val desired = List(
      BankProduct("Home loan", 1, Some(1)),
      BankProduct("Eduction loan", 1, Some(2))
    )
    whenReady(getAllAsync) { products =>
      assert(products === desired)
    }
  }

  test("Get bank and their product list") {
    val desired = List(
      (Bank("SBI bank", Some(1)), BankProduct("Home loan", 1, Some(1))),
      (Bank("SBI bank", Some(1)), BankProduct("Eduction loan", 1, Some(2)))
    )
    whenReady(getBankWithProductAsync) { bankProduct =>
      assert(bankProduct === desired)
    }
  }

  test("Get all bank and  product list") {
    val desired = List(
      (Bank("SBI bank", Some(1)), Some(BankProduct("Home loan", 1, Some(1)))),
      (Bank("SBI bank", Some(1)), Some(BankProduct("Eduction loan", 1, Some(2)))),
      (Bank("PNB bank", Some(2)), None)
    )
    whenReady(getAllBankWithProductAsync) { bankProduct =>
      assert(bankProduct === desired)
    }
  }
}
