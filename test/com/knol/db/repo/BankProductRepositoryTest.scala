package com.knol.db.repo

import com.knol.db.connection.PostgresDBComponent
import org.scalatest.FunSuite
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

class BankProductRepositoryTest extends FunSuite with BankProductRepositoryLike with PostgresDBComponent with ScalaFutures {
  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  val bankOne: Bank = BankRepository.getAll.head

  test("Add new Product ") {
    whenReady(createAsync(BankProduct("car loan", bankId=bankOne.idAsInt))) { product =>
      assert(product.id.nonEmpty)
    }
  }

  test("Update bank product ") {
    val bankProductOne: BankProduct = getAll.head
    whenReady(updateAsync(bankProductOne.copy(name="Monster Loan"))) { count =>
      assert(count === 1)
    }
  }

  test("Delete bank info") {
    val bankProductOne: BankProduct = getAll.head
    assert(bankProductOne.id.map(delete).sum === 1)
  }

  test("Get product list") {
    whenReady(getAllAsync) { products =>
      assert(products.nonEmpty)
    }
  }

  test("Get bank and their product list") {
    whenReady(getBankWithProductAsync) { bankProduct =>
      assert(bankProduct.nonEmpty)
    }
  }

  test("Get all bank and product list") {
    whenReady(getAllBankWithProductAsync) { bankProduct =>
      assert(bankProduct.nonEmpty)
    }
  }
}
