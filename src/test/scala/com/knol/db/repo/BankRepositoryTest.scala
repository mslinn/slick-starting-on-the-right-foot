package com.knol.db.repo

import org.scalatest.FunSuite
import com.knol.db.connection.{H2DBComponent, PostgresDBComponent}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

class BankRepositoryTest extends FunSuite with BankRepositoryLike with PostgresDBComponent with ScalaFutures {
  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  test("Add new bank") {
    whenReady(createAsync(Bank("ICICI bank"))) { bank =>
      assert(bank.id.nonEmpty)
    }
  }

  test("Updating non-existent bank should fail") {
    whenReady(updateAsync(Bank("SBI Bank", Some(1)))) { bankId =>
      assert(bankId === 0)
    }
  }

  test("Update existing bank") {
    whenReady(updateAsync(getAll.head)) { bankId =>
      assert(bankId === 1)
    }
  }

  test("Upsert existing bank") {
    val bankOne: Bank = getAll.head
    whenReady(upsertAsync(bankOne)) { bank => // why is bank==None?
      assert(bank.exists(_.idAsInt >= 0))
    }

    val modifiedBank = bankOne.copy(name="Beyondanana")
    whenReady(upsertAsync(modifiedBank)) { bank => // why is bank==None?
      assert(bank.exists(_.idAsInt >= 0))
    }
  }

  test("Upsert new bank") {
    whenReady(upsertAsync(Bank("ICICI bank"))) { bank =>
      assert(bank.exists(_.idAsInt >= 0))
    }
  }

  test("Delete last bank") {
    getAll.last.id.foreach { id =>
      whenReady(deleteAsync(id)) { response =>
        assert(response === 1)
      }
    }
  }

  test("Get bank list") {
    assert(BankRepository.getAll.nonEmpty)
  }
}
