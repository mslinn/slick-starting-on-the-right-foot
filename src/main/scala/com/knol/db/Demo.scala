package com.knol.db

import com.knol.db.repo._
import scala.util._
import scala.concurrent.ExecutionContext.Implicits.global

object Demo extends App {
  BankRepository.create(Bank("ICICI bank")) onComplete {
    case Success(id) =>
      BankProductRepository.create(BankProduct("Car loan", id))
      BankInfoRepository.create(BankInfo("Government", 1000, id))
      BankRepository.create(Bank("SBI Bank"))

    case Failure(e) =>
      println(s"Error ${ e.getCause }: ${ e.getMessage }")
  }

  BankInfoRepository.getAllBankWithInfo.foreach(println)
  BankProductRepository.getAllBankWithProduct.foreach(println)
  Thread.sleep(10 * 1000)
}
