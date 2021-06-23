package com.valtech.teamb.kafka.kafkaworkshop.banking;

import java.util.Optional;
import java.util.UUID;

public interface BankingService {

    /*
    * Returns a boolean if the transaction is valid
    *
    * A transaction is valid if the following conditions are satisfied:
    *  1. There is a transaction with the given transactionId
    *  2. sender and receiver account are known for the transaction
    *  3. the sender has more money in her/his account than being sent (including creditline)
    */
    boolean isTransactionValid(UUID transactionId);

    /*
    * Returns a boolean if the transaction UUID is known to the system - false otherwise
    *
     */
    boolean isTransactionKnown(UUID transactionId);

    /*
    * Returns the amount of money still in the account (excluding the credit line)
    *
    * If the accountNumber is not known an UnknownAccountException will be thrown
     */
    double getAccountBalance(String accountNumber);

    /*
     * Returns the account number for the given account holder - the account holder can be assumed to be unique
     *
     * Returns an empty optional in case the holder is not known to the system
     */
    Optional<BankingAccountOwner> getHolderForAccountNumber(String accountNumber);
}
