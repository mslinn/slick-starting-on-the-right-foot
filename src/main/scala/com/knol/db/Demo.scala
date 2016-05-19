package com.knol.db

import com.knol.db.repo._
import concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

object Demo extends App {
  BankRepository.createAsync(Bank("ICICI bank")) onComplete {
    case Success(id) =>
      BankProductRepository.createAsync(BankProduct("Car loan", id))
      BankInfoRepository.createAsync(BankInfo("Government", 1000, id))
      BankRepository.create(Bank("SBI Bank"))

    case Failure(e) =>
      println(s"Error ${ e.getCause }: ${ e.getMessage }")
  }

  Await.result(BankInfoRepository.getAllBankWithInfoAsync, Duration.Inf) foreach {
    case (bank: Bank, Some(bankInfo)) => println(s"$bank; $bankInfo.")
    case (bank, None) => println(s"$bank has no information available.")
  }

  Await.result(BankProductRepository.getAllBankWithProductAsync, Duration.Inf) foreach {
    case (bank: Bank, Some(bankProduct)) => println(s"$bank; $bankProduct.")
    case (bank, None) => println(s"$bank has no products available.")
  }

  BankRepository.deleteAll()
  BankInfoRepository.deleteAllAsync()
  BankProductRepository.deleteAllAsync()
}
