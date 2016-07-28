package com.knol.db

import com.knol.db.repo._

object Misc {
  val Logger = org.slf4j.LoggerFactory.getLogger("demo")
}

object Demo extends App {
  val newBank: Bank = BankRepository.create(Bank("ICICI bank"))
  (1 to 99).indices.foreach { i =>
    BankProductRepository.upsert(BankProduct(s"Car loan $i", bankId=newBank.idAsInt))
    BankInfoRepository.upsert(BankInfo(s"Government $i", 1000+i, bankId=newBank.idAsInt))
    BankRepository.upsert(Bank(s"SBI Bank $i"))
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
