package com.knol.db

import com.knol.db.repo._

object Demo extends App {
  val Logger = org.slf4j.LoggerFactory.getLogger("demo")
  val id = BankRepository.create(Bank("ICICI bank"))
  (1 to 99).indices.foreach { i =>
    BankProductRepository.create(BankProduct(s"Car loan $i", id+i))
    BankInfoRepository.create(BankInfo(s"Government $i", 1000+i, id+i))
    BankRepository.create(Bank(s"SBI Bank $i"))
  }

  BankInfoRepository.getAllBankWithInfo.foreach {
    case (bank: Bank, Some(bankInfo)) => println(s"$bank; $bankInfo.")
    case (bank, None) => println(s"$bank has no information available.")
  }

  BankProductRepository.getAllBankWithProduct.foreach {
    case (bank: Bank, Some(bankProduct)) => println(s"$bank; $bankProduct.")
    case (bank, None) => println(s"$bank has no products available.")
  }

  BankInfoRepository.deleteAll()
  BankProductRepository.deleteAll()
  BankRepository.deleteAll()
}
