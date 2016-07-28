package com.knol.db.repo

import com.knol.db.connection.PostgresDBComponent
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.PlaySpec

class BankRepositoryTest extends PlaySpec with BankRepositoryLike with PostgresDBComponent with ScalaFutures {
  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  "BankRepository" must {
    "Add new bank" in {
      whenReady(createAsync(Bank("ICICI bank"))) { bank =>
        assert(bank.id.nonEmpty)
      }
    }

    "Fail when updating non-existent bank" in {
      whenReady(updateAsync(Bank("SBI Bank", Some(1)))) { bankId =>
        assert(bankId === 0)
      }
    }

    "Update existing bank" in {
      whenReady(updateAsync(getAll.head)) { bankId =>
        assert(bankId === 1)
      }
    }

    "Upsert existing bank" in {
      val bankOne: Bank = getAll.head
      whenReady(upsertAsync(bankOne)) { bank => // why is bank==None?
        assert(bank.exists(_.idAsInt >= 0))
      }

      val modifiedBank = bankOne.copy(name="Beyondanana")
      whenReady(upsertAsync(modifiedBank)) { bank => // why is bank==None?
        assert(bank.exists(_.idAsInt >= 0))
      }
    }

    "Upsert new bank" in {
      whenReady(upsertAsync(Bank("ICICI bank"))) { bank =>
        assert(bank.exists(_.idAsInt >= 0))
      }
    }

    "Delete last bank" in {
      getAll.last.id.foreach { id =>
        whenReady(deleteAsync(id)) { response =>
          assert(response === 1)
        }
      }
    }

    "Get bank list" in {
      assert(BankRepository.getAll.nonEmpty)
    }
  }
}
